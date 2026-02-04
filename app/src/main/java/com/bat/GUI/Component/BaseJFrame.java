package com.bat.GUI.component;

import javax.swing.JFrame;

public class BaseJFrame extends JFrame {
    private String panelString = "";
    private int height = 800;
    private int width = 400;

    public BaseJFrame() {
        super();
        setSize(width, height);
        setName(panelString);
        init();
    }

    public BaseJFrame(int height, int width, String panelString){
        super();
        setSize(width, height);
        setName(panelString);
        init();
    }
    private void init(){
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
