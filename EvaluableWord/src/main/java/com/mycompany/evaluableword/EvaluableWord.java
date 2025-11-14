package com.mycompany.evaluableword;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class EvaluableWord {
public static void main(String[] args) {
    try {
      
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
    } catch (Exception e) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    new ProcesadorTexto().setVisible(true);
}
}