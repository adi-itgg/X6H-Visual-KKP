package io.aitech.pv.form.content.teacher;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.TeacherRepository;

import javax.swing.*;
import java.awt.*;

public class MasterTeacherForm extends BaseMasterFormContent<TeacherRepository> {


    private JPanel mp;
    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;


    public MasterTeacherForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.teacherRepository());
        initialize();
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "NIP", "Nama", "Jenis Kelamin", "Mengajar", "Nomor Hp", "Alamat"};
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
        frame.setSize(660, 420);
        return new AddTeacherForm(repository, () -> {
            frame.dispose();
            fetchData();
        });
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }
}
