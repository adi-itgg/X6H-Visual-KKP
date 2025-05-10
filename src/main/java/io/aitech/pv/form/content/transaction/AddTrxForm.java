package io.aitech.pv.form.content.transaction;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.model.ComboBoxItem;
import io.aitech.pv.repository.TransactionRepository;
import io.vertx.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AddTrxForm implements BaseForm, ActionListener {

    private final Logger log = LoggerFactory.getLogger(AddTrxForm.class);

    private JComboBox<ComboBoxItem> studentComboBox;
    private JPanel mp;
    private JPanel biayaPanel;
    private JButton simpanButton;

    private final TransactionRepository transactionRepository;
    private final Runnable updateTable;

    private final List<JCheckBox> checkBoxList = new ArrayList<>();
    private final List<Long> invoiceAmountList = new ArrayList<>();

    public AddTrxForm(TransactionRepository transactionRepository, Runnable updateTable) {
        this.transactionRepository = transactionRepository;
        this.updateTable = updateTable;


        transactionRepository.fetchStudentItems().onSuccess(v -> {
            for (Row row : v) {
                studentComboBox.addItem(new ComboBoxItem(row.getLong(0), row.getString(1)));
            }
        }).onFailure(e -> {
            log.error("Failed to fetch student data", e);
            showErrorDialog("Failed to fetch student data - " + e.getMessage());
        });

        biayaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        biayaPanel.setOpaque(false);

        transactionRepository.fetchInvoiceItems().onSuccess(v -> {
            for (Row row : v) {
                JCheckBox checkBox = new JCheckBox(row.getString(1));
                checkBox.setActionCommand(row.getLong(0) + "");
                checkBoxList.add(checkBox);
                biayaPanel.add(checkBox);
                invoiceAmountList.add(row.getLong(2));
            }
        }).onFailure(e -> {
            log.error("Failed to fetch invoice data", e);
            showErrorDialog("Failed to fetch invoice data - " + e.getMessage());
        });

        simpanButton.addActionListener(this);
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (studentComboBox.getSelectedItem() == null) {
            showWarningDialog("Siswa tidak boleh kosong");
            return;
        }
        boolean isEmpty = checkBoxList.stream().noneMatch(AbstractButton::isSelected);
        if (isEmpty) {
            showWarningDialog("Belum ada biaya yang dipilih");
            return;
        }

        List<Long> invoiceIds = new ArrayList<>();
        long totalAmount = 0;
        for (int i = 0; i < checkBoxList.size(); i++) {
            boolean isChecked = checkBoxList.get(i).isSelected();
            if (isChecked) {
                totalAmount += invoiceAmountList.get(i);
                invoiceIds.add(Long.parseLong(checkBoxList.get(i).getActionCommand()));
            }
        }

        long studentId = ((ComboBoxItem) studentComboBox.getSelectedItem()).getId();
        long finalTotalAmount = totalAmount;
        transactionRepository.findParentIdByStudentId(studentId).compose(parentId -> {
            return transactionRepository.transaction(connection -> {
                return transactionRepository.addBillStudentTx(connection, studentId, parentId, finalTotalAmount).compose(v -> {
                    return transactionRepository.getIdBillStudent(connection).compose(id -> {
                        return transactionRepository.addBillDetailStudentTx(connection, id, invoiceIds).mapEmpty();
                    });
                });
            });
        }).onSuccess(v -> {
            showInformationDialog("Transaksi berhasil ditambahkan");
            updateTable.run();
        }).onFailure(e1 -> {
            log.error("Failed to add transaction", e1);
            showErrorDialog("Failed to add transaction - " + e1.getMessage());
        });
    }

}
