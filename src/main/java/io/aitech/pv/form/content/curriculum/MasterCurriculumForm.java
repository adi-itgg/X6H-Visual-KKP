package io.aitech.pv.form.content.curriculum;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.CurriculumRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class MasterCurriculumForm extends BaseMasterFormContent<CurriculumRepository> {

    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;
    private JPanel mp;

    public MasterCurriculumForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.curriculumRepository());
        initialize();
    }


    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "Nama", "Durasi Jam"};
    }

    @Override
    protected JTextField getCariText() {
        return cariText;
    }

    @Override
    protected JButton getTambahButton() {
        return tambahButton;
    }

    @Override
    protected JTable getTable() {
        return mTable;
    }

    @Override
    protected BaseForm showAddRowForm(JFrame frame) {
        return null;
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    protected void addRow(ActionEvent actionEvent) {
        String nama = JOptionPane.showInputDialog(mp, "Masukkan Nama:", "");
        String durasi = JOptionPane.showInputDialog(mp, "Masukkan Durasi (contoh: 01:00):", "");
        if (nama != null && durasi != null && !nama.trim().isEmpty() && !durasi.trim().isEmpty()) {
            model.addRow(new Object[]{"-", nama, durasi});
        }
    }
}
