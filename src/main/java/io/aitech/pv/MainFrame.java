package io.aitech.pv;

import io.aitech.pv.login.LoginForm;
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

    private final Vertx vertx;
    private final Context context;
    private final Pool pool;

    public MainFrame(Vertx vertx, Context context, Pool pool) {
        this.vertx = vertx;
        this.context = context;
        this.pool = pool;

        showUI();
    }

    private void showUI() {
        log.info("Starting UI application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(666, 668));
        setLocationRelativeTo(null);
        setLayout(new MigLayout("al center center"));
        add(new LoginForm(vertx, context, pool));
        setVisible(true);
        log.info("UI application started");
    }

}
