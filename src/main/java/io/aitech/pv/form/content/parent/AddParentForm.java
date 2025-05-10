package io.aitech.pv.form.content.parent;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.repository.ParentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddParentForm implements BaseForm, ActionListener {

    private final Logger log = LoggerFactory.getLogger(AddParentForm.class);

    private JPanel mp;
    private JTextField nameTxt;
    private JTextField jobTxt;
    private JTextArea addressRichTxt;
    private JButton simpanButton;
    private JTextField nikTxt;
    private JTextField phoneNumberTxt;

    private final ParentRepository parentRepository;
    private final Runnable updateTable;

    public AddParentForm(ParentRepository parentRepository, Runnable updateTable) {
        this.parentRepository = parentRepository;
        this.updateTable = updateTable;
    }

    @Override
    public JPanel getMainPanel() {
        simpanButton.addActionListener(this);
        return mp;
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        if (nameTxt.getText().isBlank()) {
            showWarningDialog("Nama tidak boleh kosong");
            return;
        }
        if (jobTxt.getText().isBlank()) {
            showWarningDialog("Pekerjaan tidak boleh kosong");
            return;
        }
        if (addressRichTxt.getText().isBlank()) {
            showWarningDialog("Alamat tidak boleh kosong");
            return;
        }
        if (nikTxt.getText().isBlank()) {
            showWarningDialog("NIK tidak boleh kosong");
            return;
        }
        if (phoneNumberTxt.getText().isBlank()) {
            showWarningDialog("Nomor telepon tidak boleh kosong");
            return;
        }

        String nik = nikTxt.getText();
        String name = nameTxt.getText();
        String job = jobTxt.getText();
        String phoneNumber = phoneNumberTxt.getText();
        String address = addressRichTxt.getText();

        parentRepository.addParent(nik, name, job, address, phoneNumber).onSuccess(v -> {
            showInformationDialog("Orang tua berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e -> {
            log.error("Failed to add parent", e);
            showErrorDialog(e.getMessage());
        });

    }

}
