package io.aitech.pv.login;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class LoginForm extends JPanel {

    public LoginForm() {
        init();
    }

    private void init() {
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

        JTextField txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan email");
        txtEmail.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/icon/email.svg", 0.35f));

        add(txtEmail);

        JLabel lbPassword = new JLabel("Password");
        lbPassword.putClientProperty(FlatClientProperties.STYLE,
                "font:bold;");

        add(lbPassword, "gapy 10 5,split 2");

        JButton cmdForgotPassword = createNoBorderButton("Lupa kata sandi ?");
        add(cmdForgotPassword, "grow 0,gapy 10 5");

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "iconTextGap:10;" +
                "showRevealButton:true;");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukkan kata sandi");
        txtPassword.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("login/icon/password.svg", 0.35f));

        add(txtPassword, "gapy 0 20");

        JButton cmdSignIn = new JButton("Masuk", new FlatSVGIcon("login/icon/next.svg")) {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        cmdSignIn.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#FFFFFF;" +
                "iconTextGap:10;");
        cmdSignIn.setHorizontalTextPosition(JButton.LEADING);
        add(cmdSignIn, "gapy n 10");

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
}
