package io.aitech.pv;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import io.aitech.pv.config.AppConfiguration;
import io.aitech.pv.form.LoginForm;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private final AppConfiguration config;

    public MainFrame(Vertx vertx, Context context, Pool pool, AppConfiguration config) {
        this.config = config;
        // setup UI
        log.info("Starting UI application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(666, 668));
        setLocationRelativeTo(null);
        setLayout(new MigLayout("al center center"));
        add(new LoginForm(vertx, context, pool, this));
        setVisible(true);
        log.info("UI application started");
    }

    public AppConfiguration config() {
        return config;
    }

    public void setContainer(JComponent component) {
        setLocationRelativeTo(null);
        FlatAnimatedLafChange.showSnapshot();
        setContentPane(component);
        component.applyComponentOrientation(getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(component);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

}
