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
    private final Pool pool;

    // repositories
    private final LoginRepository loginRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final CurriculumRepository curriculumRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClassRepository classRepository;
    private final TransactionRepository transactionRepository;
    private final DashboardRepository dashboardRepository;

    public MainFrame(Vertx vertx, Context context, Pool pool, AppConfiguration config) {
        this.vertx = vertx;
        this.context = context;
        this.config = config;
        this.pool = pool;

        // initialize repositories
        this.loginRepository = new LoginRepositoryImpl(pool);
        this.studentRepository = new StudentRepositoryImpl(pool);
        this.parentRepository = new ParentRepositoryImpl(pool);
        this.teacherRepository = new TeacherRepositoryImpl(pool);
        this.curriculumRepository = new CurriculumRepositoryImpl(pool);
        this.invoiceRepository = new InvoiceRepositoryImpl(pool);
        this.classRepository = new ClassRepositoryImpl(pool);
        this.transactionRepository = new TransactionRepositoryImpl(pool);
        this.dashboardRepository = new DashboardRepositoryImpl(pool);


        // setup UI
        log.info("Starting UI application");
        showLoginForm();
        log.info("UI application started");
    }

    private void showLoginForm() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(666, 668));
        this.setLocationRelativeTo(null);
        this.setLayout(new MigLayout("al center center"));
        this.add(new LoginForm(this));
        this.setVisible(true);
    }

    public void showNewFrame() {
        new MainFrame(vertx, context, pool, config);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dispose();
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

    public InvoiceRepository invoiceRepository() {
        return invoiceRepository;
    }

    public ClassRepository classRepository() {
        return classRepository;
    }

    public TransactionRepository transactionRepository() {
        return transactionRepository;
    }

    public DashboardRepository dashboardRepository() {
        return dashboardRepository;
    }
}
