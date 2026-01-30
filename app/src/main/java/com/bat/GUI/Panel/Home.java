package com.bat.GUI.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class Home extends JPanel {
    public Home() {
        this.add(new JLabel("Home Page"));
        FlatIntelliJLaf.registerCustomDefaultsSource("style");
        FlatIntelliJLaf.setup();
    }
}
