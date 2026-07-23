package juegos.memorama;

import interfaces.MiniJuego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelMemorama extends JPanel implements MiniJuego {
    private MemoramaGame juego;
    private JButton[][] botones;
    
    private JButton primerBoton = null;
    private int f1 = -1, c1 = -1;
    private boolean bloqueado = false;

    public PanelMemorama() {
        this.juego = new MemoramaGame();
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(235, 240, 248));

        // --- PANEL SUPERIOR: Título y Botón Estilizado ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("JUEGO DE MEMORAMA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(30, 41, 59));

        JButton btnReiniciar = crearBotonReiniciarEstilizado();
        btnReiniciar.addActionListener(e -> reiniciar());

        panelSuperior.add(lblTitulo);
        panelSuperior.add(btnReiniciar);
        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Tablero de Cartas (Sin bordes) ---
        JPanel panelTablero = new JPanel(new GridLayout(juego.getFilas(), juego.getColumnas(), 12, 12));
        panelTablero.setOpaque(false);
        panelTablero.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 40));

        botones = new JButton[juego.getFilas()][juego.getColumnas()];
        for (int r = 0; r < juego.getFilas(); r++) {
            for (int c = 0; c < juego.getColumnas(); c++) {
                JButton btn = new JButton("?");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
                btn.setFocusable(false);
                
                // Quitamos bordes visuales a las tarjetas
                btn.setBorder(null);
                btn.setBorderPainted(false);
                
                btn.setBackground(new Color(47, 85, 151));
                btn.setForeground(Color.WHITE);

                final int fila = r;
                final int col = c;
                btn.addActionListener(e -> manejarClick(fila, col, btn));

                botones[r][c] = btn;
                panelTablero.add(btn);
            }
        }
        add(panelTablero, BorderLayout.CENTER);
    }

    private JButton crearBotonReiniciarEstilizado() {
        JButton btn = new JButton("Reiniciar Juego");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusable(false);
        btn.setBackground(new Color(37, 99, 235));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(29, 78, 216));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(37, 99, 235));
            }
        });

        return btn;
    }

    private void manejarClick(int f, int c, JButton btn) {
        if (bloqueado || !btn.getText().equals("?")) return;

        // Revelar carta
        btn.setText(String.valueOf(juego.getValor(f, c)));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 41, 59));

        if (primerBoton == null) {
            primerBoton = btn;
            f1 = f;
            c1 = c;
        } else {
            bloqueado = true;
            JButton segundoBoton = btn;

            if (juego.esPareja(f1, c1, f, c)) {
                // ACIERTO: Verde claro y desaparecen
                primerBoton.setBackground(new Color(134, 239, 172));
                segundoBoton.setBackground(new Color(134, 239, 172));

                Timer timerExito = new Timer(350, e -> {
                    primerBoton.setVisible(false);
                    segundoBoton.setVisible(false);
                    resetSeleccion();

                    if (juego.esJuegoTerminado()) {
                        mostrarDialogoVictoria();
                    }
                });
                timerExito.setRepeats(false);
                timerExito.start();

            } else {
                // ERROR: Fondo rojo y temblor
                primerBoton.setBackground(new Color(252, 165, 165));
                primerBoton.setForeground(new Color(153, 27, 27));

                segundoBoton.setBackground(new Color(252, 165, 165));
                segundoBoton.setForeground(new Color(153, 27, 27));

                animarTemblor(primerBoton, segundoBoton, () -> {
                    primerBoton.setText("?");
                    primerBoton.setBackground(new Color(47, 85, 151));
                    primerBoton.setForeground(Color.WHITE);

                    segundoBoton.setText("?");
                    segundoBoton.setBackground(new Color(47, 85, 151));
                    segundoBoton.setForeground(Color.WHITE);

                    resetSeleccion();
                });
            }
        }
    }

    private void animarTemblor(JButton b1, JButton b2, Runnable alFinalizar) {
        final Point posOriginal1 = b1.getLocation();
        final Point posOriginal2 = b2.getLocation();
        final int[] desplazamientos = {-6, 6, -4, 4, -2, 2, 0};

        Timer timerTemblor = new Timer(40, null);
        timerTemblor.addActionListener(new java.awt.event.ActionListener() {
            int paso = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (paso < desplazamientos.length) {
                    int offset = desplazamientos[paso];
                    b1.setLocation(posOriginal1.x + offset, posOriginal1.y);
                    b2.setLocation(posOriginal2.x + offset, posOriginal2.y);
                    paso++;
                } else {
                    b1.setLocation(posOriginal1);
                    b2.setLocation(posOriginal2);
                    ((Timer) e.getSource()).stop();

                    Timer timerPausa = new Timer(250, ev -> alFinalizar.run());
                    timerPausa.setRepeats(false);
                    timerPausa.start();
                }
            }
        });
        timerTemblor.start();
    }

    private void resetSeleccion() {
        primerBoton = null;
        f1 = -1;
        c1 = -1;
        bloqueado = false;
    }

    private void mostrarDialogoVictoria() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "¡Victoria!", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(340, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panelModal = new JPanel(new BorderLayout(10, 10));
        panelModal.setBackground(Color.WHITE);
        panelModal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        PanelEstrellaAnimada panelEstrella = new PanelEstrellaAnimada();
        panelModal.add(panelEstrella, BorderLayout.CENTER);

        JLabel lblTexto = new JLabel("<html><center><h2>¡Felicidades!</h2><p>Has encontrado todos los pares.</p></center></html>", SwingConstants.CENTER);
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelModal.add(lblTexto, BorderLayout.NORTH);

        JButton btnAceptar = new JButton("¡Genial!");
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAceptar.setBackground(new Color(16, 185, 129));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusable(false);
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAceptar.addActionListener(e -> {
            panelEstrella.detenerAnimacion();
            dialog.dispose();
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.add(btnAceptar);
        panelModal.add(panelBoton, BorderLayout.SOUTH);

        dialog.add(panelModal);
        panelEstrella.iniciarAnimacion();
        dialog.setVisible(true);
    }

    @Override
    public void reiniciar() {
        juego.reiniciar();
        resetSeleccion();

        for (int r = 0; r < juego.getFilas(); r++) {
            for (int c = 0; c < juego.getColumnas(); c++) {
                botones[r][c].setText("?");
                botones[r][c].setVisible(true);
                botones[r][c].setEnabled(true);
                botones[r][c].setBackground(new Color(47, 85, 151));
                botones[r][c].setForeground(Color.WHITE);
            }
        }
    }

    @Override
    public JPanel getPanel() { return this; }

    @Override
    public String getNombre() { return "Memorama"; }

    private static class PanelEstrellaAnimada extends JPanel {
        private double escala = 0.6;
        private boolean creciendo = true;
        private Timer timerAnimacion;

        public PanelEstrellaAnimada() {
            setOpaque(false);
            setPreferredSize(new Dimension(100, 100));
        }

        public void iniciarAnimacion() {
            timerAnimacion = new Timer(30, e -> {
                if (creciendo) {
                    escala += 0.03;
                    if (escala >= 1.1) creciendo = false;
                } else {
                    escala -= 0.03;
                    if (escala <= 0.7) creciendo = true;
                }
                repaint();
            });
            timerAnimacion.start();
        }

        public void detenerAnimacion() {
            if (timerAnimacion != null) timerAnimacion.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            g2d.translate(cx, cy);
            g2d.scale(escala, escala);

            int[] xPoints = {0, 15, 50, 22, 32, 0, -32, -22, -50, -15};
            int[] yPoints = {-50, -18, -15, 8, 45, 23, 45, 8, -15, -18};

            g2d.setColor(new Color(245, 158, 11));
            g2d.fillPolygon(xPoints, yPoints, 10);

            g2d.setColor(new Color(251, 191, 36));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawPolygon(xPoints, yPoints, 10);

            g2d.dispose();
        }
    }
}