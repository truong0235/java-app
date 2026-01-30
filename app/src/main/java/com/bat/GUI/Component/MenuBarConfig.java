package com.bat.GUI.Component;

public class MenuBarConfig {
    public String title;
    public String subtitle;
    public String[] buttonKeys;
    public String[] searchOptions;
    public String searchPlaceholder;
    
    public MenuBarConfig(String title, String subtitle, String[] buttonKeys, 
                        String[] searchOptions, String searchPlaceholder) {
        this.title = title;
        this.subtitle = subtitle;
        this.buttonKeys = buttonKeys;
        this.searchOptions = searchOptions;
        this.searchPlaceholder = searchPlaceholder;
    }
}