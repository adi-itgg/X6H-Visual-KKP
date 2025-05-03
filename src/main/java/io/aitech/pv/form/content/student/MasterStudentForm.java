package io.aitech.pv.form.content.student;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.misc.MouseWithKeyAdapter;
import io.aitech.pv.repository.StudentRepository;
import io.vertx.core.Timer;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class MasterStudentForm extends MouseWithKeyAdapter implements BaseForm {

    private final Logger log = LoggerFactory.getLogger(MasterStudentForm.class);

    private JPanel mp;
    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;

    private final DefaultTableModel model;
    private final JPopupMenu popupMenu;

    private final MainFrame mainFrame;
    private final Vertx vertx;
    private final StudentRepository studentRepository;

    private Timer searchDelayTimer;

    public MasterStudentForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.vertx = mainFrame.vertx();
        this.studentRepository = mainFrame.studentRepository();

        // initialize Table
        Object[] columns = {"Id", "Nama", "Jenis Kelamin", "Tempat Lahir", "Tanggal Lahir", "Alamat", "Orang Tua/Wali"};
        this.model = new DefaultTableModel(columns, 0);
        this.mTable.setModel(model);

        // initialize Context Menu
        this.popupMenu = new JPopupMenu();
        JMenuItem itemRefresh = new JMenuItem("Refresh");
        JMenuItem itemSave = new JMenuItem("Simpan");
        JMenuItem itemHapus = new JMenuItem("Hapus");

        popupMenu.add(itemRefresh);
        popupMenu.add(itemSave);
        popupMenu.add(itemHapus);

        mTable.addMouseListener(this);

        itemRefresh.addActionListener(this::fetchData);
        itemSave.addActionListener(this::saveRow);
        itemHapus.addActionListener(this::deleteRow);

        cariText.addKeyListener(this);

        tambahButton.addActionListener(this::addRow);

        // fetch data from database
        fetchData();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }


    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }

    private void showPopup(MouseEvent e) {
        int row = mTable.rowAtPoint(e.getPoint());
        if (row >= 0 && row < mTable.getRowCount()) {
            mTable.setRowSelectionInterval(row, row); // seleksi baris
        } else {
            mTable.clearSelection();
        }
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void addRow(ActionEvent actionEvent) {
        JFrame frame = new JFrame("Tambahkan Siswa");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 420);
        frame.setLocationRelativeTo(null);
        AddStudentForm form = new AddStudentForm(mainFrame, () -> {
            frame.dispose();
            fetchData();
        });
        frame.add(form.getMainPanel());
        frame.setVisible(true);
    }

    private void saveRow(ActionEvent event) {
        int selectedRow = mTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int code = JOptionPane.showConfirmDialog(mp, "Yakin ingin menyimpan baris ini?", UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
        if (code == JOptionPane.YES_OPTION) {
            Long id = ((Numeric) model.getValueAt(selectedRow, 0)).longValue();
            String name = (String) model.getValueAt(selectedRow, 1);
            String gender = (String) model.getValueAt(selectedRow, 2);
            String birthPlace = (String) model.getValueAt(selectedRow, 3);
            LocalDate birthDate = (LocalDate) model.getValueAt(selectedRow, 4);
            String address = (String) model.getValueAt(selectedRow, 5);
            studentRepository.save(id, name, gender, birthPlace, birthDate, address).onSuccess(v -> {
                fetchData();
            }).onFailure(e -> {
                log.error("Failed to save data", e);
                JOptionPane.showMessageDialog(mp, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void deleteRow(ActionEvent e) {
        int selectedRow = mTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(mp, "Yakin ingin hapus baris ini?");
        if (konfirmasi == JOptionPane.YES_OPTION) {
            model.removeRow(selectedRow);
        }
    }

    public void fetchData() {
        fetchData(cariText.getText());
    }

    private void fetchData(ActionEvent actionEvent) {
        fetchData();
    }

    private void fetchData(String keyword) {
        studentRepository.fetchAll(keyword).onSuccess(v -> {
            model.setRowCount(0); // clear table
            for (Row row : v) {
                Object[] data = new Object[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    data[i] = row.getValue(i);
                }
                model.addRow(data);
            }
        }).onFailure(e -> {
            log.error("Failed to fetch data", e);
            JOptionPane.showMessageDialog(mp, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

}
