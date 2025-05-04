package io.aitech.pv.form.content.student;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.StudentRepository;

import javax.swing.*;

public class MasterStudentForm extends BaseMasterFormContent<StudentRepository> {

    private JPanel mp;
    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;

    public MasterStudentForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.studentRepository());
        initialize();
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "Nama", "Jenis Kelamin", "Tempat Lahir", "Tanggal Lahir", "Alamat", "Orang Tua/Wali"};
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
        return new AddStudentForm(repository, () -> {
            frame.dispose();
            fetchData();
        });
    }


}
