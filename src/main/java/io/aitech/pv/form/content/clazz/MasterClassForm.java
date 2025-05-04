package io.aitech.pv.form.content.clazz;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.ClassRepository;

import javax.swing.*;

public class MasterClassForm extends BaseMasterFormContent<ClassRepository> {


    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;
    private JPanel mp;

    public MasterClassForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.classRepository());
        initialize();
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "Nama", "Guru", "Maksimal Siswa", "Jadwal", "Durasi", "Biaya"};
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
        frame.setSize(660, 500);
        return new AddClassForm(repository, () -> {
            frame.dispose();
            fetchData();
        });
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

}
