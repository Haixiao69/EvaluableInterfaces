package com.mycompany.evaluableword;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProcesadorTexto extends JFrame {

    private JTextArea areaTexto;
    private JComboBox<String> comboArchivo;
    private JComboBox<String> comboFuente;
    private JSpinner spinnerTamano;
    private JButton botonNegrita;
    private JButton botonColor;
    private JButton botonAyuda;
    private JButton botonAutor;

    private String fuente = "Segoe UI";
    private int tamano = 13;
    private boolean negrita = false;
    private Color colorTexto = Color.BLACK;
    private boolean modoOscuro = false;

    public ProcesadorTexto() {
        setTitle("Editor de Texto - Word");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 300));

        crearAreaTexto();
        setLayout(new BorderLayout());
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
       
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.weightx = 0;

        // Combo Archivo
        comboArchivo = new JComboBox<>(new String[]{"Archivo", "Nuevo", "Abrir", "Guardar", "Salir"});
        comboArchivo.setMaximumRowCount(5);
        comboArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboArchivo.addActionListener(e -> {
            String seleccion = (String) comboArchivo.getSelectedItem();
            switch (seleccion) {
                case "Nuevo": areaTexto.setText(""); break;
                case "Abrir": abrirArchivo(); break;
                case "Guardar": guardarArchivo(); break;
                case "Salir": System.exit(0);
            }
            comboArchivo.setSelectedIndex(0);
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(comboArchivo, gbc);

        
        comboFuente = new JComboBox<>(new String[]{"Arial", "Courier New", "Times New Roman", "Verdana", "Calibri"});
        comboFuente.setSelectedItem("Arial");
        comboFuente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFuente.addActionListener(e -> {
            fuente = (String) comboFuente.getSelectedItem();
            actualizarFuente();
        });
        gbc.gridx = 1;
        panel.add(new JLabel("Fuente:"), gbc);
        
        gbc.gridx = 2;
        panel.add(comboFuente, gbc);

    
        spinnerTamano = new JSpinner(new SpinnerNumberModel(12, 8, 72, 1));
        spinnerTamano.setValue(12);
        spinnerTamano.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinnerTamano.setPreferredSize(new Dimension(60, 28));
        spinnerTamano.addChangeListener(e -> {
            tamano = (int) spinnerTamano.getValue();
            actualizarFuente();
        });
        gbc.gridx = 3;
        panel.add(new JLabel("Tamaño:"), gbc);
        
        gbc.gridx = 4;
        panel.add(spinnerTamano, gbc);

        botonNegrita = new JButton("Negrita");
        botonNegrita.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonNegrita.setPreferredSize(new Dimension(90, 28));
        botonNegrita.addActionListener(e -> {
        negrita = !negrita;
        botonNegrita.setText(negrita ? "Negrita ✓" : "Negrita"); 
        actualizarFuente();
});
        gbc.gridx = 5;
        panel.add(botonNegrita, gbc);

        botonColor = new JButton("Color");
        botonColor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonColor.setPreferredSize(new Dimension(70, 28));
        botonColor.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Color de texto", colorTexto);
            if (nuevo != null) {
                colorTexto = nuevo;
                actualizarFuente();
            }
        });
        gbc.gridx = 6;
        panel.add(botonColor, gbc);

        botonAyuda = new JButton("Ayuda");
        botonAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        botonAyuda.setPreferredSize(new Dimension(80, 28)); 
        botonAyuda.setToolTipText("Ayuda");
        botonAyuda.addActionListener(e -> mostrarAyuda());
        gbc.gridx = 7;
        panel.add(botonAyuda, gbc);
   
        botonAutor = new JButton("Autor");
        botonAutor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonAutor.setPreferredSize(new Dimension(70, 28));
        botonAutor.addActionListener(e -> mostrarAutor());
        gbc.gridx = 8;
        panel.add(botonAutor, gbc);

        JButton botonTema = new JButton("Tema");
        botonTema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonTema.setPreferredSize(new Dimension(70, 28));
        botonTema.addActionListener(e -> {
            modoOscuro = !modoOscuro;
            aplicarTema();
        });
        gbc.gridx = 9;
        panel.add(botonTema, gbc);

        JPanel panelCompacto = crearPanelCompacto();
        panelCompacto.setVisible(false); 

        JPanel panelContenedor = new JPanel(new CardLayout());
        panelContenedor.add(panel, "normal");
        panelContenedor.add(panelCompacto, "compacto");
        
        

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                CardLayout cl = (CardLayout) panelContenedor.getLayout();
                if (getWidth() < 700) {
                    cl.show(panelContenedor, "compacto");
                } else {
                    cl.show(panelContenedor, "normal");
                }
            }
        });

        return panelContenedor;
    }

    private JPanel crearPanelCompacto() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1;

        // Fila 1 - Controles principales
        comboArchivo = new JComboBox<>(new String[]{"Archivo", "Nuevo", "Abrir", "Guardar", "Salir"});
        comboArchivo.setMaximumRowCount(5);
        comboArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboArchivo.addActionListener(e -> {
            String seleccion = (String) comboArchivo.getSelectedItem();
            switch (seleccion) {
                case "Nuevo": areaTexto.setText(""); break;
                case "Abrir": abrirArchivo(); break;
                case "Guardar": guardarArchivo(); break;
                case "Salir": System.exit(0);
            }
            comboArchivo.setSelectedIndex(0);
        });
        
        comboFuente = new JComboBox<>(new String[]{"Arial", "Courier", "Times", "Verdana", "Calibri"});
        comboFuente.setSelectedItem("Arial");
        comboFuente.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboFuente.addActionListener(e -> {
            fuente = (String) comboFuente.getSelectedItem();
            actualizarFuente();
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(comboArchivo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.4;
        panel.add(comboFuente, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.3;
        spinnerTamano = new JSpinner(new SpinnerNumberModel(12, 8, 72, 1));
        spinnerTamano.setValue(12);
        spinnerTamano.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        spinnerTamano.setPreferredSize(new Dimension(50, 26));
        spinnerTamano.addChangeListener(e -> {
            tamano = (int) spinnerTamano.getValue();
            actualizarFuente();
        });
        panel.add(spinnerTamano, gbc);

     
        gbc.gridy = 1;

        botonNegrita = new JButton("N");
        botonNegrita.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        botonNegrita.setPreferredSize(new Dimension(90, 28));
        botonNegrita.addActionListener(e -> {
        negrita = !negrita;
        botonNegrita.setText(negrita ? "N" : "N"); 
        actualizarFuente();
});
        
        botonColor = new JButton("C");
        botonColor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        botonColor.setPreferredSize(new Dimension(35, 26));
        botonColor.setToolTipText("Color");
        botonColor.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Color de texto", colorTexto);
            if (nuevo != null) {
                colorTexto = nuevo;
                actualizarFuente();
            }
        });
        
        botonAyuda = new JButton("?");
        botonAyuda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        botonAyuda.setPreferredSize(new Dimension(35, 26));
        botonAyuda.setToolTipText("Ayuda");
        botonAyuda.addActionListener(e -> mostrarAyuda());
        
        botonAutor = new JButton("A");
        botonAutor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        botonAutor.setPreferredSize(new Dimension(35, 26));
        botonAutor.setToolTipText("Autor");
        botonAutor.addActionListener(e -> mostrarAutor());
        
        JButton botonTema = new JButton("T");
        botonTema.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        botonTema.setPreferredSize(new Dimension(35, 26));
        botonTema.setToolTipText("Tema");
        botonTema.addActionListener(e -> {
            modoOscuro = !modoOscuro;
            aplicarTema();
        });

        gbc.gridx = 0; gbc.weightx = 0.2;
        panel.add(botonNegrita, gbc);
        gbc.gridx = 1;
        panel.add(botonColor, gbc);
        gbc.gridx = 2;
        panel.add(botonAyuda, gbc);
        gbc.gridx = 3;
        panel.add(botonAutor, gbc);
        gbc.gridx = 4;
        panel.add(botonTema, gbc);

        return panel;
    }

    private void crearAreaTexto() {
        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl N"), "nuevo");
        areaTexto.getActionMap().put("nuevo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { areaTexto.setText(""); }
        });

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl O"), "abrir");
        areaTexto.getActionMap().put("abrir", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { abrirArchivo(); }
        });

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl S"), "guardar");
        areaTexto.getActionMap().put("guardar", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { guardarArchivo(); }
        });

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl B"), "negrita");
        areaTexto.getActionMap().put("negrita", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                negrita = !negrita;
                actualizarFuente();
                botonNegrita.setBackground(negrita ? new Color(70, 130, 180) : null);
                botonNegrita.setForeground(negrita ? Color.WHITE : Color.BLACK);
            }
        });

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl T"), "tema");
        areaTexto.getActionMap().put("tema", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                modoOscuro = !modoOscuro;
                aplicarTema();
            }
        });

        areaTexto.getInputMap().put(KeyStroke.getKeyStroke("ctrl K"), "color");
        areaTexto.getActionMap().put("color", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                Color nuevo = JColorChooser.showDialog(ProcesadorTexto.this, "Color", colorTexto);
                if (nuevo != null) {
                    colorTexto = nuevo;
                    actualizarFuente();
                    botonColor.setForeground(colorTexto);
                }
            }
        });
    }

    private void actualizarFuente() {
        int estilo = negrita ? Font.BOLD : Font.PLAIN;
        Font nuevaFuente = new Font(fuente, estilo, tamano);
        areaTexto.setFont(nuevaFuente);
        areaTexto.setForeground(colorTexto);
    }

    private void aplicarTema() {
        if (modoOscuro) {
            getContentPane().setBackground(new Color(28, 28, 30));
            areaTexto.setBackground(new Color(38, 38, 40));
            areaTexto.setForeground(new Color(220, 220, 220));
            setTitle("Editor de Texto - DAM | Modo oscuro");
        } else {
            getContentPane().setBackground(Color.WHITE);
            areaTexto.setBackground(Color.WHITE);
            areaTexto.setForeground(Color.BLACK);
            setTitle("Editor de Texto - DAM");
        }
        repaint();
    }

    private void guardarArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("documento.txt"));
        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivo = fc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(archivo)) {
                pw.print(areaTexto.getText());
                JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File archivo = fc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                areaTexto.read(br, null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al abrir:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this,
            "<html><b>Atajos de teclado:</b><br><br>" +
            "• Ctrl + N → Nuevo documento<br>" +
            "• Ctrl + O → Abrir archivo<br>" +
            "• Ctrl + S → Guardar<br>" +
            "• Ctrl + B → Alternar negrita<br>" +
            "• Ctrl + K → Cambiar color de texto<br>" +
            "• Ctrl + T → Cambiar tema (claro/oscuro)</html>",
            "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAutor() {
        JOptionPane.showMessageDialog(this,
            "<html><b>Diseñador de Interfaces</b><br><br>" +
            "• Nombre: <b>Haixiao Wang</b><br>" +
            "• Curso: <b>2º DAM</b><br>" +
            "• Asignatura: <b>Desarrollo de Interfaces</b><br>",
            "Sobre mí", JOptionPane.INFORMATION_MESSAGE);
    }

  }