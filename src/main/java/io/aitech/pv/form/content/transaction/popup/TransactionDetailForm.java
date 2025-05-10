package io.aitech.pv.form.content.transaction.popup;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.misc.MouseWithKeyAdapter;
import io.aitech.pv.repository.TransactionRepository;
import io.vertx.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class TransactionDetailForm extends MouseWithKeyAdapter implements BaseForm, MouseListener {

    private final Logger log = LoggerFactory.getLogger(TransactionDetailForm.class);

    private JTable mTable;
    private JPanel mp;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final TransactionRepository transactionRepository;
    private final Long transactionId;

    private final DefaultTableModel model;
    private final JPopupMenu popupMenu;

    public TransactionDetailForm(TransactionRepository transactionRepository, Long transactionId) {
        this.transactionRepository = transactionRepository;
        this.transactionId = transactionId;

        // initialize Table
        String[] headerColumns = {"Id", "Nama Transaksi", "Rp", "Tanggal Transaksi"};
        this.model = new DefaultTableModel(headerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mTable.setModel(model);
        // center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < mTable.getColumnCount(); i++) {
            mTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // initialize Context Menu
        this.popupMenu = new JPopupMenu();
        JMenuItem itemRefresh = new JMenuItem("Refresh");

        popupMenu.add(itemRefresh);

        mTable.addMouseListener(this);

        itemRefresh.addActionListener(a -> fetchData());

        // fetch data from database
        fetchData();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    private void fetchData() {
        transactionRepository.fetchDetailsAll(transactionId).onSuccess(v -> {
            model.setRowCount(0); // clear table
            for (Row row : v) {
                Object[] data = new Object[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    Object value = row.getValue(i);

                    if (i == 2) { // amount
                        data[i] = currencyFormatter.format(value).split(",")[0].replace("Rp", "Rp ");
                        continue;
                    }

                    if (i == 3) { // created_at
                        data[i] = dateTimeFormatter.format((TemporalAccessor) value);
                        continue;
                    }

                    data[i] = value;
                }
                model.addRow(data);
            }
        }).onFailure(e -> {
            log.error("Failed to fetch data", e);
            showErrorDialog("Failed to fetch data - " + e.getMessage());
        });
    }
}
