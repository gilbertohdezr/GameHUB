package juegos.tres_en_raya;

import interfaces.MiniJuego;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class TresEnRaya extends JPanel implements MiniJuego {

    private static final Color FONDO_SUPERIOR = new Color(15, 23, 42);
    private static final Color FONDO_INFERIOR = new Color(30, 41, 59);
    private static final Color SUPERFICIE = new Color(30, 41, 59);
    private static final Color SUPERFICIE_CLARA = new Color(43, 56, 76);
    private static final Color BORDE = new Color(71, 85, 105);
    private static final Color TEXTO = new Color(248, 250, 252);
    private static final Color TEXTO_SUAVE = new Color(148, 163, 184);
    private static final Color COLOR_X = new Color(56, 189, 248);
    private static final Color COLOR_O = new Color(251, 113, 133);
    private static final Color COLOR_EMPATE = new Color(250, 204, 21);

    private final CasillaButton[][] casillas = new CasillaButton[3][3];
    private final char[][] tablero = new char[3][3];

    private JLabel lblEstado;
    private JLabel lblPuntosX;
    private JLabel lblPuntosO;
    private JLabel lblEmpates;
    private TarjetaJugador tarjetaX;
    private TarjetaJugador tarjetaO;
    private char turnoActual = 'X';
    private boolean partidaTerminada;
    private int victoriasX;
    private int victoriasO;
    private int empates;

    public TresEnRaya() {
        construirUI();
        reiniciar();
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 18, 30));
        setOpaque(false);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearZonaJuego(), BorderLayout.CENTER);
        add(crearAcciones(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(18, 0));
        encabezado.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Tres en Raya");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 31));
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDescripcion = new JLabel("Consigue tres fichas en línea.");
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDescripcion.setForeground(TEXTO_SUAVE);
        lblDescripcion.setAlignmentX(LEFT_ALIGNMENT);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblDescripcion);

        JPanel marcador = new JPanel(new GridLayout(1, 3, 8, 0));
        marcador.setOpaque(false);

        tarjetaX = new TarjetaJugador("JUGADOR X", COLOR_X);
        lblPuntosX = tarjetaX.getLblPuntos();
        tarjetaO = new TarjetaJugador("JUGADOR O", COLOR_O);
        lblPuntosO = tarjetaO.getLblPuntos();

        TarjetaMarcador tarjetaEmpates = new TarjetaMarcador(
                "EMPATES", COLOR_EMPATE);
        lblEmpates = tarjetaEmpates.getLblPuntos();

        marcador.add(tarjetaX);
        marcador.add(tarjetaO);
        marcador.add(tarjetaEmpates);

        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(marcador, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearZonaJuego() {
        JPanel zonaJuego = new JPanel(new BorderLayout(0, 12));
        zonaJuego.setOpaque(false);

        lblEstado = new JLabel("", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(30, 41, 59));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        zonaJuego.add(lblEstado, BorderLayout.NORTH);

        JPanel panelTablero = new JPanel(new GridLayout(3, 3, 10, 10));
        panelTablero.setOpaque(false);
        panelTablero.setPreferredSize(new Dimension(385, 385));

        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                CasillaButton casilla = crearCasilla(fila, columna);
                casillas[fila][columna] = casilla;
                panelTablero.add(casilla);
            }
        }

        PanelRedondeado marcoTablero = new PanelRedondeado(
                new Color(15, 23, 42, 175), 28);
        marcoTablero.setLayout(new BorderLayout());
        marcoTablero.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        marcoTablero.add(panelTablero);
        marcoTablero.setPreferredSize(new Dimension(409, 409));

        JPanel contenedorTablero = new ContenedorCuadrado(marcoTablero);
        zonaJuego.add(contenedorTablero, BorderLayout.CENTER);

        return zonaJuego;
    }

    private JPanel crearAcciones() {
        JPanel acciones = new JPanel(new BorderLayout(14, 0));
        acciones.setOpaque(false);

        JLabel instrucciones = new JLabel(
                "X comienza  •  Selecciona una casilla libre para jugar");
        instrucciones.setFont(new Font("SansSerif", Font.PLAIN, 13));
        instrucciones.setForeground(TEXTO_SUAVE);

        BotonAccion btnNuevaPartida = new BotonAccion("↻  Nueva partida");
        btnNuevaPartida.addActionListener(e -> reiniciar());

        acciones.add(instrucciones, BorderLayout.CENTER);
        acciones.add(btnNuevaPartida, BorderLayout.EAST);
        return acciones;
    }

    private CasillaButton crearCasilla(int fila, int columna) {
        CasillaButton casilla = new CasillaButton();
        casilla.addActionListener(e -> realizarMovimiento(fila, columna));
        return casilla;
    }

    private void realizarMovimiento(int fila, int columna) {
        if (partidaTerminada || tablero[fila][columna] != '\0') {
            return;
        }

        tablero[fila][columna] = turnoActual;
        CasillaButton casilla = casillas[fila][columna];
        casilla.setText(String.valueOf(turnoActual));
        casilla.setForeground(turnoActual == 'X' ? COLOR_X : COLOR_O);

        int[][] lineaGanadora = obtenerLineaGanadora(turnoActual);
        if (lineaGanadora != null) {
            partidaTerminada = true;
            if (turnoActual == 'X') {
                victoriasX++;
            } else {
                victoriasO++;
            }

            for (int[] posicion : lineaGanadora) {
                casillas[posicion[0]][posicion[1]].setGanadora(true);
            }

            lblEstado.setText("¡Victoria del jugador " + turnoActual + "!");
            lblEstado.setForeground(
                    turnoActual == 'X' ? COLOR_X : COLOR_O);
            bloquearCasillas();
            actualizarMarcador();
            actualizarTarjetas(false);
            return;
        }

        if (tableroLleno()) {
            partidaTerminada = true;
            empates++;
            lblEstado.setText("Partida empatada — ¡Estuvo cerca!");
            lblEstado.setForeground(COLOR_EMPATE);
            bloquearCasillas();
            actualizarMarcador();
            actualizarTarjetas(false);
            return;
        }

        turnoActual = turnoActual == 'X' ? 'O' : 'X';
        actualizarEstadoTurno();
    }

    private int[][] obtenerLineaGanadora(char ficha) {
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == ficha
                    && tablero[i][1] == ficha
                    && tablero[i][2] == ficha) {
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            }

            if (tablero[0][i] == ficha
                    && tablero[1][i] == ficha
                    && tablero[2][i] == ficha) {
                return new int[][]{{0, i}, {1, i}, {2, i}};
            }
        }

        if (tablero[0][0] == ficha
                && tablero[1][1] == ficha
                && tablero[2][2] == ficha) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }

        if (tablero[0][2] == ficha
                && tablero[1][1] == ficha
                && tablero[2][0] == ficha) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }

        return null;
    }

    private boolean tableroLleno() {
        for (char[] fila : tablero) {
            for (char casilla : fila) {
                if (casilla == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void bloquearCasillas() {
        for (CasillaButton[] fila : casillas) {
            for (CasillaButton casilla : fila) {
                casilla.setEnabled(false);
            }
        }
    }

    private void actualizarEstadoTurno() {
        lblEstado.setText("Turno del jugador " + turnoActual);
        lblEstado.setForeground(turnoActual == 'X' ? COLOR_X : COLOR_O);
        actualizarTarjetas(true);
    }

    private void actualizarTarjetas(boolean mostrarTurno) {
        tarjetaX.setActiva(mostrarTurno && turnoActual == 'X');
        tarjetaO.setActiva(mostrarTurno && turnoActual == 'O');
    }

    private void actualizarMarcador() {
        lblPuntosX.setText(String.valueOf(victoriasX));
        lblPuntosO.setText(String.valueOf(victoriasO));
        lblEmpates.setText(String.valueOf(empates));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setPaint(new GradientPaint(
                0, 0, FONDO_SUPERIOR,
                getWidth(), getHeight(), FONDO_INFERIOR));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    @Override
    public String getNombre() {
        return "Tres en Raya";
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void reiniciar() {
        turnoActual = 'X';
        partidaTerminada = false;

        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                tablero[fila][columna] = '\0';
                casillas[fila][columna].setText("");
                casillas[fila][columna].setEnabled(true);
                casillas[fila][columna].setGanadora(false);
            }
        }

        actualizarEstadoTurno();
        actualizarMarcador();
    }

    private static class PanelRedondeado extends JPanel {

        private final Color colorFondo;
        private final int radio;
        private Color colorBorde;

        PanelRedondeado(Color colorFondo, int radio) {
            this.colorFondo = colorFondo;
            this.radio = radio;
            setOpaque(false);
        }

        void setColorBorde(Color colorBorde) {
            this.colorBorde = colorBorde;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);

            if (colorBorde != null) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(colorBorde);
                g2.drawRoundRect(
                        1, 1, getWidth() - 3, getHeight() - 3,
                        radio, radio);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }


    private static class ContenedorCuadrado extends JPanel {

        private static final int TAMANO_MAXIMO = 409;
        private final JPanel contenido;

        ContenedorCuadrado(JPanel contenido) {
            this.contenido = contenido;
            setOpaque(false);
            setLayout(null);
            add(contenido);
        }

        @Override
        public void doLayout() {
            int tamano = Math.min(
                    TAMANO_MAXIMO,
                    Math.min(getWidth(), getHeight()));
            int x = (getWidth() - tamano) / 2;
            int y = (getHeight() - tamano) / 2;
            contenido.setBounds(x, y, tamano, tamano);
            contenido.doLayout();
        }
    }

    private static class TarjetaMarcador extends PanelRedondeado {

        private final JLabel lblPuntos;

        TarjetaMarcador(String titulo, Color color) {
            super(SUPERFICIE, 20);
            setLayout(new BorderLayout(0, 1));
            setBorder(BorderFactory.createEmptyBorder(8, 13, 8, 13));
            setPreferredSize(new Dimension(84, 67));

            JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 9));
            lblTitulo.setForeground(TEXTO_SUAVE);

            lblPuntos = new JLabel("0", SwingConstants.CENTER);
            lblPuntos.setFont(new Font("SansSerif", Font.BOLD, 23));
            lblPuntos.setForeground(color);

            add(lblTitulo, BorderLayout.NORTH);
            add(lblPuntos, BorderLayout.CENTER);
        }

        JLabel getLblPuntos() {
            return lblPuntos;
        }
    }

    private static class TarjetaJugador extends TarjetaMarcador {

        private final Color colorJugador;

        TarjetaJugador(String titulo, Color colorJugador) {
            super(titulo, colorJugador);
            this.colorJugador = colorJugador;
        }

        void setActiva(boolean activa) {
            setColorBorde(activa ? colorJugador : null);
        }
    }

    private static class CasillaButton extends JButton {

        private boolean cursorEncima;
        private boolean ganadora;

        CasillaButton() {
            setFont(new Font("SansSerif", Font.BOLD, 68));
            setForeground(TEXTO);
            setPreferredSize(new Dimension(120, 120));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    cursorEncima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    cursorEncima = false;
                    repaint();
                }
            });
        }

        void setGanadora(boolean ganadora) {
            this.ganadora = ganadora;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondoCasilla;
            if (ganadora) {
                fondoCasilla = new Color(51, 65, 85);
            } else if (getModel().isPressed()) {
                fondoCasilla = new Color(24, 34, 51);
            } else if (cursorEncima && isEnabled()) {
                fondoCasilla = SUPERFICIE_CLARA;
            } else {
                fondoCasilla = SUPERFICIE;
            }

            g2.setColor(fondoCasilla);
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 24, 24);

            g2.setStroke(new BasicStroke(ganadora ? 3f : 1.25f));
            g2.setColor(ganadora ? getForeground() : BORDE);
            g2.drawRoundRect(
                    1, 1, getWidth() - 3, getHeight() - 3,
                    24, 24);

            String ficha = getText();
            if (!ficha.isEmpty()) {
                g2.setFont(getFont());
                FontMetrics metricas = g2.getFontMetrics();
                int x = (getWidth() - metricas.stringWidth(ficha)) / 2;
                int y = (getHeight() - metricas.getHeight()) / 2
                        + metricas.getAscent();
                g2.setColor(getForeground());
                g2.drawString(ficha, x, y);
            }

            g2.dispose();
        }
    }

    private static class BotonAccion extends JButton {

        private boolean cursorEncima;

        BotonAccion(String texto) {
            super(texto);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(FONDO_SUPERIOR);
            setPreferredSize(new Dimension(158, 38));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    cursorEncima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    cursorEncima = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cursorEncima
                    ? new Color(125, 211, 252)
                    : COLOR_X);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
