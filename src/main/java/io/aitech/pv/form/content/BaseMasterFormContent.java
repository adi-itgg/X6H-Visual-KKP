package io.aitech.pv.form.content;

import io.aitech.pv.form.BaseForm;
import io.aitech.pv.misc.MouseWithKeyAdapter;
import io.aitech.pv.repository.BaseMasterRepository;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BaseMasterFormContent<R extends BaseMasterRepository> extends MouseWithKeyAdapter implements BaseForm {

    private boolean isInitialized;

    protected final Logger log = LoggerFactory.getLogger(getClass().asSubclass(getClass()));

    protected final Vertx vertx;
    protected final R repository;

    protected DefaultTableModel model;
    protected JPopupMenu popupMenu;

    protected Timer searchDelayTimer;

    public BaseMasterFormContent(Vertx vertx, R repository) {
        this.vertx = vertx;
        this.repository = repository;
    }

    protected void initialize() {

        if (this.isInitialized) {
            return;
        }
        this.isInitialized = true;
        // initialize Table
        this.model = new DefaultTableModel(getHeaderColumns(), 0);
        getTable().setModel(model);
        // center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setVerticalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < getTable().getColumnCount(); i++) {
            getTable().getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // initialize Context Menu
        this.popupMenu = new JPopupMenu();
        JMenuItem itemRefresh = new JMenuItem("Refresh");
        JMenuItem itemSave = new JMenuItem("Simpan");
        JMenuItem itemHapus = new JMenuItem("Hapus");

        popupMenu.add(itemRefresh);
        popupMenu.add(itemSave);
        popupMenu.add(itemHapus);

        getTable().addMouseListener(this);

        itemRefresh.addActionListener(a -> fetchData());
        itemSave.addActionListener(this::saveRow);
        itemHapus.addActionListener(this::deleteRow);

        getCariText().addKeyListener(this);

        getTambahButton().addActionListener(this::addRow);

        // fetch data from database
        fetchData();
    }

    protected abstract String[] getHeaderColumns();

    protected abstract JTextField getCariText();

    protected abstract JButton getTambahButton();

    protected abstract JTable getTable();

    protected abstract BaseForm showAddRowForm(JFrame frame);

    protected void fetchData() {
        String keyword = getCariText().getText();
        repository.fetchAll(keyword).onSuccess(v -> {
            model.setRowCount(0); // clear table
            for (Row row : v) {
                Object[] data = new Object[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    Object value = row.getValue(i);
                    data[i] = value;
                    if (value instanceof Duration duration) {
                        long hour = duration.toHours();
                        long minutes = duration.minusHours(hour).toMinutes();

                        data[i] = String.format("%02d:%02d", hour, minutes);
                    }
                    Object newValue = mapDisplayColumnValue(i, data[i]);
                    if (newValue != null) {
                        data[i] = newValue;
                    }
                }
                model.addRow(data);
            }
        }).onFailure(e -> {
            log.error("Failed to fetch data", e);
            showErrorDialog("Failed to fetch data - " + e.getMessage());
        });
    }

    protected Object mapDisplayColumnValue(int index, Object value) {
        return value;
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
        int row = getTable().rowAtPoint(e.getPoint());
        if (row >= 0 && row < getTable().getRowCount()) {
            getTable().setRowSelectionInterval(row, row);
        } else {
            getTable().clearSelection();
        }
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void addRow(ActionEvent actionEvent) {
        JFrame frame = new JFrame("Tambahkan Data");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(660, 420);
        frame.setLocationRelativeTo(null);
        frame.add(showAddRowForm(frame).getMainPanel());
        frame.setVisible(true);
    }

    protected void saveRow(ActionEvent event) {
        int selectedRow = getTable().getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int code = JOptionPane.showConfirmDialog(getMainPanel(), "Yakin ingin menyimpan baris ini?", UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
        if (code == JOptionPane.YES_OPTION) {
            List<Object> params = new ArrayList<>();
            for (int i = 0; i < getHeaderColumns().length; i++) {
                Object value = model.getValueAt(selectedRow, i);
                value = mapColumnValue(i, value);
                params.add(value);
            }
            repository.save(params).onSuccess(v -> {
                fetchData();
            }).onFailure(e -> {
                log.error("Failed to save data", e);
                showErrorDialog("Failed to save data - " + e.getMessage());
            });
        }
    }

    protected Object mapColumnValue(int index, Object value) {
        return value;
    }

    protected void deleteRow(ActionEvent event) {
        int selectedRow = getTable().getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(getMainPanel(), "Yakin ingin hapus baris ini?");
        if (konfirmasi == JOptionPane.YES_OPTION) {
            Object id = model.getValueAt(selectedRow, 0); // dynamic using object
            repository.delete(id).onSuccess(v -> {
                fetchData();
            }).onFailure(e -> {
                log.error("Failed to delete data", e);
                showErrorDialog("Failed to delete data - " + e.getMessage());
            });
        }
    }

}
