package io.aitech.pv.form;

import javax.swing.*;

public interface BaseForm {

    JPanel getMainPanel();


    default void showError(String message) {
        JOptionPane.showMessageDialog(getMainPanel(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
