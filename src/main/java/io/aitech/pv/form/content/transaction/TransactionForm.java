package io.aitech.pv.form.content.transaction;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.transaction.popup.TransactionDetailForm;
import io.aitech.pv.misc.MouseWithKeyAdapter;
import io.aitech.pv.repository.TransactionRepository;
import io.vertx.core.Timer;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TransactionForm extends MouseWithKeyAdapter implements BaseForm, MouseListener {


    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;
    private JPanel mp;


    private final Logger log = LoggerFactory.getLogger(getClass().asSubclass(getClass()));
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Vertx vertx;
    private final TransactionRepository transactionRepository;

    private final DefaultTableModel model;
    private final JPopupMenu popupMenu;

    private Timer searchDelayTimer;

    public TransactionForm(MainFrame mainFrame) {
        this.vertx = mainFrame.vertx();
        this.transactionRepository = mainFrame.transactionRepository();

        // initialize Table
        String[] headerColumns = {"Id", "Nama Siswa", "Orang Tua/Wali", "Total", "Tanggal Transaksi"};
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

        cariText.addKeyListener(this);

        tambahButton.addActionListener(this::addRow);

        // fetch data from database
        fetchData();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    private void fetchData() {
        String keyword = cariText.getText();
        transactionRepository.fetchAll(keyword).onSuccess(v -> {
            model.setRowCount(0); // clear table
            for (Row row : v) {
                Object[] data = new Object[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    Object value = row.getValue(i);

                    if (i == 3) { // total_amount
                        data[i] = currencyFormatter.format(value).split(",")[0].replace("Rp", "Rp ");
                        continue;
                    }

                    if (i == 4) { // created_at
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

    private void onSearch() {
        if (searchDelayTimer != null) {
            searchDelayTimer.cancel();
        }
        searchDelayTimer = vertx.timer(800, TimeUnit.MILLISECONDS);
        searchDelayTimer.onSuccess(v -> fetchData());
    }


    @Override
    public void keyTyped(KeyEvent e) {
        onSearch();
    }


    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }

    protected void showPopup(MouseEvent e) {
        int row = mTable.rowAtPoint(e.getPoint());
        if (row >= 0 && row < mTable.getRowCount()) {
            mTable.setRowSelectionInterval(row, row);
        } else {
            mTable.clearSelection();
        }
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void addRow(ActionEvent actionEvent) {
        JFrame frame = new JFrame("Tambahkan Data Pembayaran");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(660, 420);
        frame.setLocationRelativeTo(null);
        frame.add(new AddTrxForm(transactionRepository, () -> {
            frame.dispose();
            fetchData();
        }).getMainPanel());
        frame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2 && mTable.getSelectedRow() != -1) {
            JFrame frame = new JFrame("Detail Pembayaran");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(660, 420);
            frame.setLocationRelativeTo(null);
            int row = mTable.getSelectedRow();
            long id = ((Number) mTable.getValueAt(row, 0)).longValue();
            frame.add(new TransactionDetailForm(transactionRepository, id).getMainPanel());
            frame.setVisible(true);
        }
    }
}
