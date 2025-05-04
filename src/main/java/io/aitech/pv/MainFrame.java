package io.aitech.pv;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import io.aitech.pv.config.AppConfiguration;
import io.aitech.pv.form.login.LoginForm;
import io.aitech.pv.repository.*;
import io.aitech.pv.repository.impl.*;
import io.aitech.pv.util.URunnable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private final Vertx vertx;
    private final Context context;
    private final AppConfiguration config;

    // repositories
    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final CurriculumRepository curriculumRepository;

    public MainFrame(Vertx vertx, Context context, Pool pool, AppConfiguration config) {
        this.vertx = vertx;
        this.context = context;
        this.config = config;

        // initialize repositories
        this.loginRepository = new LoginRepositoryImpl(pool);
        this.studentRepository = new StudentRepositoryImpl(pool);
        this.parentRepository = new ParentRepositoryImpl(pool);
        this.teacherRepository = new TeacherRepositoryImpl(pool);
        this.curriculumRepository = new CurriculumRepositoryImpl(pool);


        // setup UI
        log.info("Starting UI application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(666, 668));
        setLocationRelativeTo(null);
        setLayout(new MigLayout("al center center"));
        add(new LoginForm(this));
        setVisible(true);
        log.info("UI application started");
    }

    public AppConfiguration config() {
        return config;
    }

    public LoginRepository loginRepository() {
        return loginRepository;
    }

    public void setContainer(JComponent component) {
        setLocationRelativeTo(null);
        FlatAnimatedLafChange.showSnapshot();
        setContentPane(component);
        component.applyComponentOrientation(getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(component);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public <T> Future<T> runVirtualThread(URunnable runnable) {
        Promise<T> promise = Promise.promise();
        context.runOnContext(v -> {
            try {
                runnable.run();
                promise.complete();
            } catch (Throwable error) {
                log.error("Failed to run virtual thread", error);
                promise.fail(error);
                JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return promise.future();
    }

    public Vertx vertx() {
        return vertx;
    }

    public StudentRepository studentRepository() {
        return studentRepository;
    }

    public ParentRepository parentRepository() {
        return parentRepository;
    }

    public TeacherRepository teacherRepository() {
        return teacherRepository;
    }

    public CurriculumRepository curriculumRepository() {
        return curriculumRepository;
    }
}
