package io.aitech.pv.form.content.parent;

import io.aitech.pv.repository.ParentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddParentForm implements ActionListener {

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
        simpanButton.addActionListener(this);
    }

    public JPanel getMainPanel() {
        return mp;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(mp, message);
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        if (nameTxt.getText().isBlank()) {
            showError("Nama tidak boleh kosong");
            return;
        }
        if (jobTxt.getText().isBlank()) {
            showError("Pekerjaan tidak boleh kosong");
            return;
        }
        if (addressRichTxt.getText().isBlank()) {
            showError("Alamat tidak boleh kosong");
            return;
        }
        if (nikTxt.getText().isBlank()) {
            showError("NIK tidak boleh kosong");
            return;
        }
        if (phoneNumberTxt.getText().isBlank()) {
            showError("Nomor telepon tidak boleh kosong");
            return;
        }

        String nik = nikTxt.getText();
        String name = nameTxt.getText();
        String job = jobTxt.getText();
        String phoneNumber = phoneNumberTxt.getText();
        String address = addressRichTxt.getText();

        parentRepository.addParent(nik, name, job, address, phoneNumber).onSuccess(v -> {
            JOptionPane.showMessageDialog(mp, "Orang tua berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e -> {
            log.error("Failed to add parent", e);
            showError(e.getMessage());
        });

    }

}
