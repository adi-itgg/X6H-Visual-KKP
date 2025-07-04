package io.aitech.pv.form.content.student;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.repository.StudentRepository;
import io.vertx.sqlclient.Row;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raven.datetime.DatePicker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class AddStudentForm implements BaseForm, ActionListener {

    private final Logger log = LoggerFactory.getLogger(AddStudentForm.class);

    private JPanel mp;
    private JTextField nameTxt;
    private JRadioButton lakiLakiRadioButton;
    private JTextField bornPlaceTxt;
    private JRadioButton perempuanRadioButton;
    private JPanel bornDatePanel;
    private JTextArea addressRichTxt;
    private JButton simpanButton;
    private JComboBox<Parent> parentComboBox;

    private final StudentRepository studentRepository;
    private final Runnable updateTable;
    private DatePicker datePicker;

    public AddStudentForm(StudentRepository studentRepository, Runnable updateTable) {
        this.studentRepository = studentRepository;
        this.updateTable = updateTable;
    }

    private void initialize() {
        lakiLakiRadioButton.addActionListener(e -> {
            if (lakiLakiRadioButton.isSelected()) {
                perempuanRadioButton.setSelected(false);
            }
        });
        perempuanRadioButton.addActionListener(e -> {
            if (perempuanRadioButton.isSelected()) {
                lakiLakiRadioButton.setSelected(false);
            }
        });

        simpanButton.addActionListener(this);

        this.datePicker = new DatePicker();
        datePicker.setToolTipText("Tanggal Lahir");
        datePicker.setDateSelectionAble((date) -> !date.isAfter(LocalDate.now()));
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        JFormattedTextField editor = new JFormattedTextField();
        datePicker.setEditor(editor);
        bornDatePanel.setLayout(new MigLayout());
        bornDatePanel.add(editor, "width 250");

        fetchParents();
    }

    @Override
    public JPanel getMainPanel() {
        initialize();
        return mp;
    }

    static class Parent {

        private Long id;
        private String name;

        @Override
        public String toString() {
            return name;
        }

    }


    private void fetchParents() {
        studentRepository.fetchParentNames().onSuccess(v -> {
            parentComboBox.removeAllItems();
            for (Row row : v) {
                Parent parent = new Parent();
                parent.id = row.getLong("id");
                parent.name = row.getString("name");
                parentComboBox.addItem(parent);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (nameTxt.getText().isBlank()) {
            showWarningDialog("Nama tidak boleh kosong");
            return;
        }
        if (bornPlaceTxt.getText().isBlank()) {
            showWarningDialog("Tempat lahir tidak boleh kosong");
            return;
        }
        if (addressRichTxt.getText().isBlank()) {
            showWarningDialog("Alamat tidak boleh kosong");
            return;
        }
        Parent parent = (Parent) parentComboBox.getSelectedItem();
        if (parent == null) {
            showWarningDialog("Orang tua tidak boleh kosong");
            return;
        }
        LocalDate bornDate = datePicker.getSelectedDate();
        if (bornDate == null) {
            showWarningDialog("Tanggal lahir tidak boleh kosong");
            return;
        }
        char gender = lakiLakiRadioButton.isSelected() ? 'L' : 'P';
        studentRepository.addStudent(nameTxt.getText(), gender, bornDate, bornPlaceTxt.getText(), addressRichTxt.getText(), parent.id).onSuccess(v -> {
            showInformationDialog("Siswa berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e1 -> {
            log.error("Failed to add student", e1);
            showErrorDialog(e1.getMessage());
        });
    }


}
