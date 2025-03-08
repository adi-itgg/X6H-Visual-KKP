package io.aitech.pv.login;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.aitech.pv.model.ActionCommand;
import io.aitech.pv.repository.LoginRepository;
import io.aitech.pv.repository.LoginRepositoryImpl;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class LoginForm extends JPanel implements ActionListener, Consumer<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(LoginForm.class);

    private final BCrypt.Verifyer verifyer = BCrypt.verifyer(BCrypt.Version.VERSION_2A);

    private final Vertx vertx;
    private final Context context; // virtual thread context
    private final LoginRepository loginRepository;

    // UI Components
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JButton btnSignIn;
    private final JButton btnForgotPassword;

    public LoginForm(Vertx vertx, Context context, Pool pool) {
        this.vertx = vertx;
        this.context = context;
        this.loginRepository = new LoginRepositoryImpl(pool);

        setLayout(new MigLayout("wrap,gapy 3", "[fill,300]"));

        add(new JLabel(new FlatSVGIcon("login/icon/logo.svg", 1.5f)));

        JLabel lbTitle = new JLabel("Selamat datang", JLabel.CENTER);
        lbTitle.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +15;");
        add(lbTitle, "gapy 8 8");

        add(new JLabel("Login untuk mengakses Dashboard,", JLabel.CENTER), "gapy 8");
        add(new JLabel("monitoring dan konfigurasi aplikasi.", JLabel.CENTER), "gapy 0 32");

        JLabel lbSeparator = new JLabel("Masuk Dashboard");
        lbSeparator.putClientProperty(FlatClientProperties.STYLE,
                "foreground:$Label.disabledForeground;" +
                        "font:-1;");

        add(createSeparator(), "split 3,sizegroup g1");
        add(lbSeparator, "sizegroup g1");
        add(createSeparator(), "sizegroup g1");

        JLabel lbEmail = new JLabel("Email");
        lbEmail.putClientProperty(FlatClientProperties.STYLE,
                "font:bold;");
        add(lbEmail, "gapy 10 5");

        this.txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan email");
        txtEmail.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/icon/email.svg", 0.35f));

        add(txtEmail);

        JLabel lbPassword = new JLabel("Password");
        lbPassword.putClientProperty(FlatClientProperties.STYLE,
                "font:bold;");

        add(lbPassword, "gapy 10 5,split 2");

        this.btnForgotPassword = createNoBorderButton("Lupa kata sandi ?");
        add(btnForgotPassword, "grow 0,gapy 10 5");

        this.txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;" +
                        "showRevealButton:true;");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan kata sandi");
        txtPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/icon/password.svg", 0.35f));

        add(txtPassword, "gapy 0 20");

        this.btnSignIn = new JButton("Masuk", new FlatSVGIcon("login/icon/next.svg")) {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        btnSignIn.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#FFFFFF;" +
                        "iconTextGap:10;");
        btnSignIn.setHorizontalTextPosition(JButton.LEADING);
        btnSignIn.setActionCommand(ActionCommand.LOGIN.name());
        btnSignIn.addActionListener(this);
        add(btnSignIn, "gapy n 10");

    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE,
                "stripeIndent:8;");
        return separator;
    }

    private JButton createNoBorderButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE,
                "foreground:$Component.accentColor;" +
                        "margin:1,5,1,5;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "background:null;");
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        context.runOnContext(v -> {
            try {
                this.accept(e);
            } catch (Throwable error) {
                log.error("Failed to login", error);
                showError(error.getMessage());
            }
        });
    }


    @Override
    public void accept(ActionEvent actionEvent) {
        log.info("Login button clicked");

        switch (ActionCommand.valueOf(actionEvent.getActionCommand())) {
            case LOGIN -> login();
            case FORGOT_PASSWORD -> {
            } // TODO implement forgot password
        }

    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        enableControls();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
        enableControls();
    }

    private void enableControls() {
        // enable all controls
        txtEmail.setEnabled(true);
        txtPassword.setEnabled(true);
        btnForgotPassword.setEnabled(true);
        btnSignIn.setEnabled(true);
    }

    private void login() {
        // disable all controls
        txtEmail.setEnabled(false);
        txtPassword.setEnabled(false);
        btnForgotPassword.setEnabled(false);
        btnSignIn.setEnabled(false);

        if (txtEmail.getText().isBlank()) {
            showInfo("Email tidak boleh kosong");
            return;
        }

        if (txtPassword.getPassword().length == 0) {
            showInfo("Kata sandi tidak boleh kosong");
            return;
        }

        Row row = loginRepository.findUserByEmail(txtEmail.getText()).await();
        if (row == null) {
            showError("Email tidak ditemukan");
            return;
        }

        String password = row.getString("password");
        if (verifyer.verify(txtPassword.getPassword(), password).verified) {
            log.info("Login success");
            // TODO implement redirect to dashboard
            return;
        }

        showError("Kata sandi salah");
    }

}
