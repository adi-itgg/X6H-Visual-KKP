package io.aitech.pv.form;

import javax.swing.*;

public interface BaseForm {

    JPanel getMainPanel();


    default void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(getMainPanel(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    default void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(getMainPanel(), message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    default void showInformationDialog(String message) {
        JOptionPane.showMessageDialog(getMainPanel(), message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

}
