package io.aitech.pv.form.content.parent;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.ParentRepository;

import javax.swing.*;

public final class MasterParentForm extends BaseMasterFormContent<ParentRepository> {

    private JPanel mp;
    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;

    public MasterParentForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.parentRepository());
        initialize();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "NIK", "Nama", "Pekerjaan", "Nomor Hp", "Alamat"};
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
        frame.setSize(600, 420);
        return new AddParentForm(repository, () -> {
            fetchData();
            frame.dispose();
        });
    }

}
