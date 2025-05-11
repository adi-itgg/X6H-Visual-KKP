package io.aitech.pv.form.login;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.aitech.pv.CustomFocusTraversalPolicy;
import io.aitech.pv.MainFrame;
import io.aitech.pv.form.mainform.MainForm;
import io.aitech.pv.model.ActionCommand;
import io.vertx.sqlclient.Row;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginForm extends JPanel implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(LoginForm.class);

    private final BCrypt.Verifyer verifyer = BCrypt.verifyer(BCrypt.Version.VERSION_2A);

    private final MainFrame mainFrame;

    // UI Components
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JButton btnSignIn;
    private final JButton btnForgotPassword;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new MigLayout("wrap,gapy 3", "[fill,300]"));

        add(new JLabel(new FlatSVGIcon("icon/login_logo.svg", 1.5f)));

        JLabel lbTitle = new JLabel("SIPA X6H", JLabel.CENTER);
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
        txtEmail.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icon/email.svg", 0.35f));
        txtEmail.setActionCommand(ActionCommand.LOGIN_TXT_PASS_FOCUS.name());
        add(txtEmail);

        JLabel lbPassword = new JLabel("Password");
        lbPassword.putClientProperty(FlatClientProperties.STYLE,
                "font:bold;");

        add(lbPassword, "gapy 10 5,split 2");

        this.btnForgotPassword = createNoBorderButton("");
        btnForgotPassword.setActionCommand(ActionCommand.FORGOT_PASSWORD.name());
        add(btnForgotPassword, "grow 0,gapy 10 5");

        this.txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;" +
                        "showRevealButton:true;");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan kata sandi");
        txtPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icon/password.svg", 0.35f));
        txtPassword.setActionCommand(ActionCommand.LOGIN_BTN_FOCUS.name());
        add(txtPassword, "gapy 0 20");

        this.btnSignIn = new JButton("Masuk", new FlatSVGIcon("icon/next.svg")) {
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
        add(btnSignIn, "gapy n 10");

        // set action listeners
        btnForgotPassword.addActionListener(this);
        btnSignIn.addActionListener(this);
        txtEmail.addActionListener(this);
        txtPassword.addActionListener(this);

        // focus for tab navigation
        List<Component> order = List.of(txtEmail, txtPassword, btnSignIn, btnForgotPassword);
        mainFrame.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(order));
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
    public void actionPerformed(ActionEvent actionEvent) {
        mainFrame.runVirtualThread(() -> {
            switch (ActionCommand.valueOf(actionEvent.getActionCommand())) {
                case LOGIN -> {
                    log.info("Login button clicked");
                    login();
                }
                case FORGOT_PASSWORD -> { // TODO implement forgot password
                    log.info("Forgot password button clicked");
                }
                case LOGIN_TXT_PASS_FOCUS -> txtPassword.requestFocus();
                case LOGIN_BTN_FOCUS -> btnSignIn.doClick();
                default -> throw new IllegalStateException("Unexpected value: " + actionEvent.getActionCommand());
            }
        }).onFailure(e -> enableControls());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
            showError("Email tidak boleh kosong");
            return;
        }

        if (txtPassword.getPassword().length == 0) {
            showError("Kata sandi tidak boleh kosong");
            return;
        }

        Row row = mainFrame.loginRepository().findUserByEmail(txtEmail.getText()).await();
        if (row == null) {
            showError("Email tidak ditemukan");
            return;
        }

        String password = row.getString("password");
        if (verifyer.verify(txtPassword.getPassword(), password).verified) {
            log.info("Login success");

            MainForm mainForm = new MainForm(mainFrame);
            mainForm.setSelectedMenu(0, 0);
            mainForm.hideMenu();

            mainFrame.remove(this);
            mainFrame.setSize(new Dimension(1366, 768));
            mainFrame.setContainer(mainForm);
            return;
        }

        showError("Kata sandi salah");
    }

}
