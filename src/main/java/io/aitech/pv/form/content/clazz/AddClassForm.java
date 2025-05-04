package io.aitech.pv.form.content.clazz;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.repository.ClassRepository;
import io.vertx.sqlclient.Row;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raven.datetime.TimePicker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class AddClassForm implements BaseForm, ActionListener {

    private final Logger log = LoggerFactory.getLogger(AddClassForm.class);

    private JTextField nameTxt;
    private JTextField capacityTxt;
    private JPanel schedulePanel;
    private JButton simpanButton;
    private JComboBox<Invoice> invoiceComboBox;
    private JPanel mp;
    private JComboBox<Teacher> teacherComboBox;
    private JPanel durationPanel;

    private final ClassRepository classRepository;
    private final Runnable updateTable;

    private TimePicker scheduleTimePicker;
    private TimePicker durationTimePicker;

    public AddClassForm(ClassRepository classRepository, Runnable updateTable) {
        this.classRepository = classRepository;
        this.updateTable = updateTable;

        simpanButton.addActionListener(this);

        this.scheduleTimePicker = new TimePicker();
        scheduleTimePicker.setToolTipText("Jadwal");
        JFormattedTextField editor = new JFormattedTextField();
        scheduleTimePicker.setEditor(editor);
        schedulePanel.setLayout(new MigLayout());
        schedulePanel.add(editor, "width 250");

        this.durationTimePicker = new TimePicker();
        durationTimePicker.setToolTipText("Jadwal");
        JFormattedTextField durationTimeEditor = new JFormattedTextField();
        durationTimePicker.setEditor(durationTimeEditor);
        durationPanel.setLayout(new MigLayout());
        durationPanel.add(durationTimeEditor, "width 250");

        fetchTeacherItems();
        fetchInvoiceItems();
    }

    private void fetchTeacherItems() {
        classRepository.fetchTeacherItems().onSuccess(items -> {
            teacherComboBox.removeAllItems();
            for (Row item : items) {
                Teacher teacher = new Teacher();
                teacher.id = item.getLong("id");
                teacher.name = item.getString("name");
                teacherComboBox.addItem(teacher);
            }
        }).onFailure(e -> {
            log.error("Failed to fetch teacher data", e);
            showErrorDialog("Failed to fetch teacher data - " + e.getMessage());
        });
    }

    private void fetchInvoiceItems() {
        classRepository.fetchInvoiceItems().onSuccess(items -> {
            invoiceComboBox.removeAllItems();
            Invoice none = new Invoice();
            none.name = "-";
            invoiceComboBox.addItem(none);
            invoiceComboBox.setSelectedIndex(0);
            for (Row item : items) {
                Invoice invoice = new Invoice();
                invoice.id = item.getLong("id");
                invoice.name = item.getString("name");
                invoiceComboBox.addItem(invoice);
            }
        }).onFailure(e -> {
            log.error("Failed to fetch invoice data", e);
            showErrorDialog("Failed to fetch invoice data - " + e.getMessage());
        });
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (nameTxt.getText().isBlank()) {
            showErrorDialog("Nama tidak boleh kosong");
            return;
        }
        if (teacherComboBox.getSelectedItem() == null) {
            showErrorDialog("Guru tidak boleh kosong");
            return;
        }
        if (capacityTxt.getText().isBlank()) {
            showErrorDialog("Kapasitas tidak boleh kosong");
            return;
        }

        LocalTime scheduleTime = scheduleTimePicker.getSelectedTime();
        LocalTime durationTime = durationTimePicker.getSelectedTime();
        if (scheduleTime == null) {
            showErrorDialog("Jadwal tidak boleh kosong");
            return;
        }
        if (durationTime == null) {
            showErrorDialog("Durasi tidak boleh kosong");
            return;
        }

        if (invoiceComboBox.getSelectedItem() == null) {
            showErrorDialog("Tagihan tidak boleh kosong");
            return;
        }

        String name = nameTxt.getText();
        Teacher teacher = (Teacher) teacherComboBox.getSelectedItem();
        int capacity = Integer.parseInt(capacityTxt.getText());
        Invoice invoice = (Invoice) invoiceComboBox.getSelectedItem();
        classRepository.addClass(name, teacher.id, capacity, scheduleTime, durationTime, invoice.id).onSuccess(v -> {
            showInformationDialog("Kelas berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e1 -> {
            log.error("Failed to add class", e1);
            showErrorDialog("Kelas gagal ditambahkan");
        });

    }


    static class Invoice {

        private Long id;
        private String name;

        @Override
        public String toString() {
            return name;
        }
    }

    static class Teacher {

        private Long id;
        private String name;


        @Override
        public String toString() {
            return name;
        }
    }

}
