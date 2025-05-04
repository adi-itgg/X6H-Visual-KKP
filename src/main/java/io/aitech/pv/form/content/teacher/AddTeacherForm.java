package io.aitech.pv.form.content.teacher;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTeacherForm implements BaseForm, ActionListener {

    private final Logger log = LoggerFactory.getLogger(AddTeacherForm.class);

    private JPanel mp;
    private JTextField nameTxt;
    private JTextField eduTxt;
    private JTextArea addressRichTxt;
    private JButton simpanButton;
    private JTextField nipTxt;
    private JTextField phoneNumberTxt;
    private JRadioButton lakiLakiRadioButton;
    private JRadioButton perempuanRadioButton;

    private final TeacherRepository teacherRepository;
    private final Runnable updateTable;
    public AddTeacherForm(TeacherRepository teacherRepository, Runnable updateTable) {
        this.teacherRepository = teacherRepository;
        this.updateTable = updateTable;
        simpanButton.addActionListener(this);
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (nipTxt.getText().isBlank()) {
            showErrorDialog("NIP tidak boleh kosong");
            return;
        }
        if (nameTxt.getText().isBlank()) {
            showErrorDialog("Nama tidak boleh kosong");
            return;
        }
        if (eduTxt.getText().isBlank()) {
            showErrorDialog("Pendidikan tidak boleh kosong");
            return;
        }
        if (phoneNumberTxt.getText().isBlank()) {
            showErrorDialog("Nomor telepon tidak boleh kosong");
            return;
        }
        if (addressRichTxt.getText().isBlank()) {
            showErrorDialog("Alamat tidak boleh kosong");
            return;
        }

        char gender = lakiLakiRadioButton.isSelected() ? 'L' : 'P';
        teacherRepository.addTeacher(nipTxt.getText(), nameTxt.getText(), gender, eduTxt.getText(), phoneNumberTxt.getText(), addressRichTxt.getText()).onSuccess(v -> {
            showInformationDialog("Guru berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e1 -> {
            log.error("Failed to add teacher", e1);
            showErrorDialog("Guru gagal ditambahkan");
        });
    }

}
