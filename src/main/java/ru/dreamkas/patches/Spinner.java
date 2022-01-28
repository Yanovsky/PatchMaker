package ru.dreamkas.patches;

import java.awt.Component;
import java.awt.Image;
import java.util.Optional;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class Spinner {
    private static JDialog dialog;
    private static JProgressBar jProgressBar;
    private static Component parentComponent;

    public static void show(String title, String message) {
        show(null, null, title, message);
    }

    public static void show(String title, String message, Integer max) {
        show(null, null, title, message, max);
    }

    public static void show(Image icon, String title, String message) {
        show(null, icon, title, message);
    }

    public static void show(Component parent, Image icon, String title, String message) {
        show(parent, icon, title, message, null);
    }

    public static void show(Component parent, Image icon, String title, String message, Integer max) {
        hide();
        parentComponent = parent;
        Optional.ofNullable(parentComponent).ifPresent(p -> p.setEnabled(false));

        JOptionPane messagePane = new JOptionPane(message);
        messagePane.setOptions(new Object[0]);
        jProgressBar = new JProgressBar();
        jProgressBar.setIndeterminate(max == null);
        if (max != null) {
            jProgressBar.setMinimum(0);
            jProgressBar.setMaximum(max);
        }
        messagePane.add(jProgressBar, 1);
        dialog = messagePane.createDialog(parent, title);
        dialog.setModal(false);
        if (icon != null) {
            dialog.setIconImage(icon);
        }
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    public static void setValue(int value) {
        if (jProgressBar != null && !jProgressBar.isIndeterminate()) {
            jProgressBar.setValue(Math.min(jProgressBar.getMaximum(), value));
        }
    }

    public static void hide() {
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
            jProgressBar = null;
            dialog = null;
            Optional.ofNullable(parentComponent).ifPresent(p -> p.setEnabled(true));
        }
    }
}
