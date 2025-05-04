package io.aitech.pv.form.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import io.aitech.pv.form.menu.mode.LightDarkMode;
import io.aitech.pv.form.menu.mode.ToolBarAccentColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Menu extends JPanel implements LayoutManager {

    private final String[][] menuItems = {
            {"~MAIN~"},
            {"Dashboard"}, // 0
            {"~Master Data~"},
            {"Orang Tua/Wali"}, // 1
            {"Siswa"}, // 2
            {"Guru"}, // 3
            {"Kurikulum"}, // 4
            {"Kelas"}, // 5
            {"Tagihan"}, // 6
            {"~Transaksi~"},
            {"Pembayaran"}, // 7
//            {"~COMPONENT~"},
//            {"Advanced UI", "Cropper", "Owl Carousel", "Sweet Alert"},
//            {"Forms", "Basic Elements", "Advanced Elements", "Editors", "Wizard"},
            {"~Lainnya~"},
//            {"Charts", "Apex", "Flot", "Peity", "Sparkline"},
//            {"Icons", "Feather Icons", "Flag Icons", "Mdi Icons"},
//            {"Special Pages", "Blank page", "Faq", "Invoice", "Profile", "Pricing", "Timeline"},
            {"Keluar"}
    };

    private final List<MenuEvent> events = new ArrayList<>();
    private boolean menuFull = true;

    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 250;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;

    private final String headerName;

    public Menu(String headerName) {
        this.headerName = headerName;
        setLayout(this);
        putClientProperty(FlatClientProperties.STYLE, "border:20,2,2,2;"
                + "background:$Menu.background;"
                + "arc:10");
        header = new JLabel(headerName);
        header.setIcon(new FlatSVGIcon("icon/logo.svg"));
        header.putClientProperty(FlatClientProperties.STYLE, "font:$Menu.header.font;"
                + "foreground:$Menu.foreground");

        //  Menu
        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuItemLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5;"
                + "background:$Menu.background");

        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, "border:null");
        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
//        vscroll.putClientProperty(FlatClientProperties.STYLE,  "width:$Menu.scroll.width;"
//                + "trackInsets:$Menu.scroll.trackInsets;"
//                + "thumbInsets:$Menu.scroll.thumbInsets;"
//                + "background:$Menu.ScrollBar.background;"
//                + "thumb:$Menu.ScrollBar.thumb");
        createMenu();
        lightDarkMode = new LightDarkMode();
        toolBarAccentColor = new ToolBarAccentColor(this);
        toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));
        add(header);
        add(scroll);
        add(lightDarkMode);
        add(toolBarAccentColor);
    }

    public boolean isMenuFull() {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull) {
        this.menuFull = menuFull;
        if (menuFull) {
            header.setText(headerName);
            header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
        } else {
            header.setText("");
            header.setHorizontalAlignment(JLabel.CENTER);
        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
        lightDarkMode.setMenuFull(menuFull);
        toolBarAccentColor.setMenuFull(menuFull);
    }

    private void createMenu() {
        int index = 0;
        for (String[] item : menuItems) {
            String menuName = item[0];
            if (menuName.startsWith("~") && menuName.endsWith("~")) {
                panelMenu.add(createTitle(menuName));
            } else {
                MenuItem menuItem = new MenuItem(this, item, index++, events);
                panelMenu.add(menuItem);
            }
        }
    }

    private JLabel createTitle(String title) {
        String menuName = title.substring(1, title.length() - 1);
        JLabel lbTitle = new JLabel(menuName);
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:$Menu.label.font;"
                + "foreground:$Menu.title.foreground");
        return lbTitle;
    }

    public void setSelectedMenu(int index, int subIndex) {
        runEvent(index, subIndex);
    }

    protected void setSelected(int index, int subIndex) {
        int size = panelMenu.getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = panelMenu.getComponent(i);
            if (com instanceof MenuItem item) {
                if (item.getMenuIndex() == index) {
                    item.setSelectedIndex(subIndex);
                } else {
                    item.setSelectedIndex(-1);
                }
            }
        }
    }

    protected void runEvent(int index, int subIndex) {
        MenuAction menuAction = new MenuAction();
        for (MenuEvent event : events) {
            event.menuSelected(index, subIndex, menuAction);
        }
        if (!menuAction.isCancel()) {
            setSelected(index, subIndex);
        }
    }

    public void addMenuEvent(MenuEvent event) {
        events.add(event);
    }

    public void hideMenuItem() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).hideMenuItem();
            }
        }
        revalidate();
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
    }

    private JLabel header;
    private JScrollPane scroll;
    private JPanel panelMenu;
    private LightDarkMode lightDarkMode;
    private ToolBarAccentColor toolBarAccentColor;

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
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int gap = UIScale.scale(5);
            int sheaderFullHgap = UIScale.scale(headerFullHgap);
            int width = parent.getWidth() - (insets.left + insets.right);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            int iconHeight = header.getPreferredSize().height;
            int hgap = menuFull ? sheaderFullHgap : 0;
            int accentColorHeight = 0;
            if (toolBarAccentColor.isVisible()) {
                accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
            }

            header.setBounds(x + hgap, y, width - (hgap * 2), iconHeight);
            int ldgap = UIScale.scale(10);
            int ldWidth = width - ldgap * 2;
            int ldHeight = lightDarkMode.getPreferredSize().height;
            int ldx = x + ldgap;
            int ldy = y + height - ldHeight - ldgap - accentColorHeight;

            int menuy = y + iconHeight + gap;
            int menuHeight = height - (iconHeight + gap) - (ldHeight + ldgap * 2) - (accentColorHeight);
            scroll.setBounds(x, menuy, width, menuHeight);

            lightDarkMode.setBounds(ldx, ldy, ldWidth, ldHeight);

            if (toolBarAccentColor.isVisible()) {
                int tbheight = toolBarAccentColor.getPreferredSize().height;
                int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
                int tby = y + height - tbheight - ldgap;
                int tbx = ldx + ((ldWidth - tbwidth) / 2);
                toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
            }
        }
    }
}
