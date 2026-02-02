package com.bat.GUI.component;

import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class MenuFunction extends JToolBar {
    public HashMap<String, JButton> buttons = new HashMap<>();
    
    public MenuFunction(String [] listBtn) {
        initData();
        initComponents(listBtn);
    }

    public void initData() {       
        buttons.put("create", new ButtonToolbar("Thêm", "add.svg", "create"));
        buttons.put("delete", new ButtonToolbar("Xóa", "delete.svg", "delete"));
        buttons.put("update", new ButtonToolbar("Sửa", "edit.svg", "update"));
        buttons.put("detail", new ButtonToolbar("Chi Tiết", "info.svg", "view"));
        buttons.put("export", new ButtonToolbar("Xuất Excel", "export_excel.svg", "view"));
    }

    private void initComponents(String[] listBtn) {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 6,0));
        this.setOpaque(false);        
        this.setRollover(true);  
        initData();
        
        for (String btnKey : listBtn) {
            if (buttons.containsKey(btnKey)) {
                JButton btn = buttons.get(btnKey);
                this.add(btn);
            }
        }
    }
}
