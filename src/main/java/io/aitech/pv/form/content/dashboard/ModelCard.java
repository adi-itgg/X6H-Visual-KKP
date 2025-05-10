package io.aitech.pv.form.content.dashboard;

import javax.swing.*;

public class ModelCard {

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("DataFlowIssue")
    public ModelCard(String iconPath, String title, String values, String description) {
        this.icon = new ImageIcon(getClass().getResource(iconPath));
        this.title = title;
        this.values = values;
        this.description = description;
    }

    public ModelCard() {
    }

    private Icon icon;
    private String title;
    private String values;
    private String description;
}
