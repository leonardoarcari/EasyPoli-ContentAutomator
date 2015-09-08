package com.easypoli.contentAutomator;

import javax.swing.*;

public class Main {

    /**
     * Running our beautiful application
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationUI());
    }
}
