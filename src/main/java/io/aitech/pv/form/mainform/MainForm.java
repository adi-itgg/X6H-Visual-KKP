package io.aitech.pv.form.mainform;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.FormDashboard;
import io.aitech.pv.form.content.curriculum.MasterCurriculumForm;
import io.aitech.pv.form.content.invoice.MasterInvoiceForm;
import io.aitech.pv.form.content.parent.MasterParentForm;
import io.aitech.pv.form.content.student.MasterStudentForm;
import io.aitech.pv.form.content.teacher.MasterTeacherForm;
import io.aitech.pv.form.menu.Menu;
import io.aitech.pv.form.menu.MenuAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainForm extends JLayeredPane implements LayoutManager {

    private final MainFrame mainFrame;
    private final Menu menu;
    private final JPanel panelBody;
    private JButton menuButton;

    public MainForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(this);
        menu = new Menu(mainFrame.config().appName());
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        assert menuButton != null;
        menuButton.putClientProperty(FlatClientProperties.STYLE, "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        initMenuEvent();
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("icon/" + icon, 0.8f));
    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            if (index == 0) {
                showForm(new FormDashboard());
            } else if (index == 1) {
                showForm(new MasterParentForm(mainFrame));
            } else if (index == 2) {
                showForm(new MasterStudentForm(mainFrame));
            } else if (index == 3) {
                showForm(new MasterTeacherForm(mainFrame));
            } else if (index == 4) {
                showForm(new MasterCurriculumForm(mainFrame));
            } else if (index == 6) {
                showForm(new MasterInvoiceForm(mainFrame));
            } else if (index == 9) {
//                logout();
            } else {
                action.cancel();
            }
        });
    }

    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("icon/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(BaseForm form) {
        showForm(form.getMainPanel());
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return new Dimension(5, 5);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return new Dimension(0, 0);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            boolean ltr = parent.getComponentOrientation().isLeftToRight();
            Insets insets = UIScale.scale(parent.getInsets());
            int x = insets.left;
            int y = insets.top;
            int width = parent.getWidth() - (insets.left + insets.right);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
            int menuX = ltr ? x : x + width - menuWidth;
            menu.setBounds(menuX, y, menuWidth, height);
            int menuButtonWidth = menuButton.getPreferredSize().width;
            int menuButtonHeight = menuButton.getPreferredSize().height;
            int menubX;
            if (ltr) {
                menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
            } else {
                menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
            }
            menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
            int gap = UIScale.scale(5);
            int bodyWidth = width - menuWidth - gap;
            int bodyx = ltr ? (x + menuWidth + gap) : x;
            panelBody.setBounds(bodyx, y, bodyWidth, height);
        }
    }
}
