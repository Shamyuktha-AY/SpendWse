package edu.spendsense;

import edu.spendsense.ui.SpendSenseFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                System.out.println("Using default look and feel.");
            }
            new SpendSenseFrame().setVisible(true);
        });
    }
}
