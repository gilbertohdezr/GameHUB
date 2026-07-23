package juegos.tres_en_raya;

import interfaces.MiniJuego;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    private final List<Particula> confeti = new ArrayList<>();
    private final Random aleatorio = new Random();
    private final Timer relojAnimacion;
    private final Timer temporizadorBot;

    private JLabel lblEstado;
    private JLabel lblInstrucciones;
    private TarjetaJugador tarjetaX;
    private TarjetaJugador tarjetaO;
    private TarjetaMarcador tarjetaEmpates;
    private BotonAccion btnNuevaPartida;
    private BotonAccion btnReiniciarMarcador;
    private BotonModo btnModoAmigo;
    private BotonModo btnModoBot;
    private ModoJuego modoJuego = ModoJuego.AMIGO;
    private char turnoActual = 'X';
    private boolean partidaTerminada;
    private boolean esperandoBot;
    private int victoriasX;
    private int victoriasO;
    private int empates;
    private long ultimoFrame;
    private double tiempoAnimacion;

    public TresEnRaya() {
        construirUI();

        temporizadorBot = new Timer(520, e -> jugarTurnoBot());
        temporizadorBot.setRepeats(false);

        reiniciar();
        ultimoFrame = System.nanoTime();
        relojAnimacion = new Timer(25, e -> actualizarAnimaciones());
        relojAnimacion.setCoalesce(true);
        relojAnimacion.start();
    }

    private enum ModoJuego {
        AMIGO,
        BOT
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
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(
                "Piensa rápido. Conecta tres. Domina el tablero.");
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDescripcion.setForeground(TEXTO_SUAVE);
        lblDescripcion.setAlignmentX(LEFT_ALIGNMENT);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblDescripcion);
        textos.add(Box.createVerticalStrut(8));
        textos.add(crearSelectorModo());

        JPanel marcador = new JPanel(new GridLayout(1, 3, 8, 0));
        marcador.setOpaque(false);

        tarjetaX = new TarjetaJugador("JUGADOR X", COLOR_X);
        tarjetaO = new TarjetaJugador("JUGADOR O", COLOR_O);
        tarjetaEmpates = new TarjetaMarcador(
                "EMPATES", COLOR_EMPATE);

        marcador.add(tarjetaX);
        marcador.add(tarjetaO);
        marcador.add(tarjetaEmpates);

        encabezado.add(textos, BorderLayout.CENTER);
        encabezado.add(marcador, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearSelectorModo() {
        PanelRedondeado selector = new PanelRedondeado(
                new Color(15, 23, 42, 185), 16);
        selector.setLayout(new GridLayout(1, 2, 4, 0));
        selector.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        selector.setPreferredSize(new Dimension(252, 31));
        selector.setMaximumSize(new Dimension(252, 31));
        selector.setAlignmentX(LEFT_ALIGNMENT);

        btnModoAmigo = new BotonModo("2 JUGADORES");
        btnModoAmigo.addActionListener(
                e -> cambiarModo(ModoJuego.AMIGO));

        btnModoBot = new BotonModo("VS BOT");
        btnModoBot.addActionListener(
                e -> cambiarModo(ModoJuego.BOT));

        selector.add(btnModoAmigo);
        selector.add(btnModoBot);
        return selector;
    }

    private JPanel crearZonaJuego() {
        JPanel zonaJuego = new JPanel(new BorderLayout(0, 12));
        zonaJuego.setOpaque(false);

        lblEstado = new JLabel("", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblEstado.setOpaque(false);

        PanelRedondeado barraEstado = new PanelRedondeado(
                new Color(30, 41, 59, 220), 18);
        barraEstado.setLayout(new BorderLayout());
        barraEstado.setBorder(
                BorderFactory.createEmptyBorder(9, 14, 9, 14));
        barraEstado.setColorBorde(new Color(71, 85, 105, 90));
        barraEstado.add(lblEstado, BorderLayout.CENTER);
        zonaJuego.add(barraEstado, BorderLayout.NORTH);

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
        marcoTablero.setColorBorde(new Color(56, 189, 248, 35));

        JPanel contenedorTablero = new ContenedorCuadrado(marcoTablero);
        zonaJuego.add(contenedorTablero, BorderLayout.CENTER);

        return zonaJuego;
    }

    private JPanel crearAcciones() {
        JPanel acciones = new JPanel(new BorderLayout(14, 0));
        acciones.setOpaque(false);

        lblInstrucciones = new JLabel(
                "X comienza  •  Elige una casilla libre");
        lblInstrucciones.setFont(new Font(
                "SansSerif", Font.PLAIN, 13));
        lblInstrucciones.setForeground(TEXTO_SUAVE);

        JPanel botones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);

        btnReiniciarMarcador = new BotonAccion(
                "⟲  Reiniciar puntos", false);
        btnReiniciarMarcador.addActionListener(
                e -> reiniciarPuntuacion());

        btnNuevaPartida = new BotonAccion(
                "↻  Nueva partida", true);
        btnNuevaPartida.addActionListener(e -> reiniciar());

        botones.add(btnReiniciarMarcador);
        botones.add(btnNuevaPartida);

        acciones.add(lblInstrucciones, BorderLayout.CENTER);
        acciones.add(botones, BorderLayout.EAST);
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

        if (modoJuego == ModoJuego.BOT
                && (esperandoBot || turnoActual == 'O')) {
            return;
        }

        procesarMovimiento(fila, columna);
    }

    private void procesarMovimiento(int fila, int columna) {
        tablero[fila][columna] = turnoActual;
        CasillaButton casilla = casillas[fila][columna];
        Color colorTurno = turnoActual == 'X' ? COLOR_X : COLOR_O;
        casilla.colocarFicha(turnoActual, colorTurno);

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
            iniciarCelebracion(colorTurno);
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
            iniciarCelebracion(COLOR_EMPATE);
            return;
        }

        turnoActual = turnoActual == 'X' ? 'O' : 'X';
        if (modoJuego == ModoJuego.BOT && turnoActual == 'O') {
            prepararTurnoBot();
        } else {
            actualizarEstadoTurno();
        }
    }

    private void prepararTurnoBot() {
        esperandoBot = true;
        actualizarEstadoTurno();
        temporizadorBot.restart();
    }

    private void jugarTurnoBot() {
        if (modoJuego != ModoJuego.BOT
                || partidaTerminada
                || turnoActual != 'O') {
            esperandoBot = false;
            return;
        }

        int[] movimiento = buscarMejorMovimiento();
        esperandoBot = false;

        if (movimiento != null) {
            procesarMovimiento(movimiento[0], movimiento[1]);
        }
    }

    private int[] buscarMejorMovimiento() {
        int mejorPuntuacion = Integer.MIN_VALUE;
        List<int[]> mejoresMovimientos = new ArrayList<>();

        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                if (tablero[fila][columna] != '\0') {
                    continue;
                }

                tablero[fila][columna] = 'O';
                int puntuacion = minimax(0, false);
                tablero[fila][columna] = '\0';

                if (puntuacion > mejorPuntuacion) {
                    mejorPuntuacion = puntuacion;
                    mejoresMovimientos.clear();
                    mejoresMovimientos.add(new int[]{fila, columna});
                } else if (puntuacion == mejorPuntuacion) {
                    mejoresMovimientos.add(new int[]{fila, columna});
                }
            }
        }

        if (mejoresMovimientos.isEmpty()) {
            return null;
        }

        return mejoresMovimientos.get(
                aleatorio.nextInt(mejoresMovimientos.size()));
    }

    private int minimax(int profundidad, boolean turnoBot) {
        if (obtenerLineaGanadora('O') != null) {
            return 10 - profundidad;
        }
        if (obtenerLineaGanadora('X') != null) {
            return profundidad - 10;
        }
        if (tableroLleno()) {
            return 0;
        }

        int mejorPuntuacion = turnoBot
                ? Integer.MIN_VALUE
                : Integer.MAX_VALUE;

        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                if (tablero[fila][columna] != '\0') {
                    continue;
                }

                tablero[fila][columna] = turnoBot ? 'O' : 'X';
                int puntuacion = minimax(
                        profundidad + 1, !turnoBot);
                tablero[fila][columna] = '\0';

                if (turnoBot) {
                    mejorPuntuacion = Math.max(
                            mejorPuntuacion, puntuacion);
                } else {
                    mejorPuntuacion = Math.min(
                            mejorPuntuacion, puntuacion);
                }
            }
        }

        return mejorPuntuacion;
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
                casilla.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void actualizarEstadoTurno() {
        Color colorTurno = turnoActual == 'X' ? COLOR_X : COLOR_O;
        lblEstado.setForeground(colorTurno);

        boolean turnoDisponible = true;
        if (modoJuego == ModoJuego.BOT) {
            if (turnoActual == 'O') {
                lblEstado.setText("El bot está pensando");
                turnoDisponible = false;
            } else {
                lblEstado.setText("Tu turno — juegas con X");
            }
        } else {
            lblEstado.setText("Turno del jugador " + turnoActual);
        }

        for (CasillaButton[] fila : casillas) {
            for (CasillaButton casilla : fila) {
                casilla.setSugerencia(turnoActual, colorTurno);
                casilla.setInteraccionPermitida(turnoDisponible);
            }
        }
        actualizarTarjetas(true);
    }

    private void actualizarTarjetas(boolean mostrarTurno) {
        tarjetaX.setActiva(mostrarTurno && turnoActual == 'X');
        tarjetaO.setActiva(mostrarTurno && turnoActual == 'O');
    }

    private void actualizarMarcador() {
        tarjetaX.setPuntos(victoriasX);
        tarjetaO.setPuntos(victoriasO);
        tarjetaEmpates.setPuntos(empates);
    }

    private void cambiarModo(ModoJuego nuevoModo) {
        if (modoJuego == nuevoModo) {
            return;
        }

        modoJuego = nuevoModo;
        actualizarPresentacionModo();
        reiniciarPuntuacion();
    }

    private void actualizarPresentacionModo() {
        boolean contraBot = modoJuego == ModoJuego.BOT;
        btnModoAmigo.setSeleccionado(!contraBot);
        btnModoBot.setSeleccionado(contraBot);

        tarjetaX.setTitulo(contraBot ? "TÚ  ·  X" : "JUGADOR X");
        tarjetaO.setTitulo(contraBot ? "BOT  ·  O" : "JUGADOR O");
        lblInstrucciones.setText(contraBot
                ? "Tú eres X  •  El bot juega con O"
                : "X comienza  •  Elige una casilla libre");
    }

    private void actualizarAnimaciones() {
        long ahora = System.nanoTime();
        double delta = Math.min(
                0.05, (ahora - ultimoFrame) / 1_000_000_000.0);
        ultimoFrame = ahora;

        if (!isShowing()) {
            return;
        }

        tiempoAnimacion += delta;

        if (esperandoBot) {
            int cantidadPuntos = (int) (tiempoAnimacion * 3) % 4;
            lblEstado.setText(
                    "El bot está pensando" + ".".repeat(cantidadPuntos));
        }

        for (CasillaButton[] fila : casillas) {
            for (CasillaButton casilla : fila) {
                casilla.actualizar(delta, tiempoAnimacion);
            }
        }

        tarjetaX.actualizar(delta, tiempoAnimacion);
        tarjetaO.actualizar(delta, tiempoAnimacion);
        tarjetaEmpates.actualizar(delta, tiempoAnimacion);
        btnModoAmigo.actualizar(delta);
        btnModoBot.actualizar(delta);
        btnReiniciarMarcador.actualizar(delta);
        btnNuevaPartida.actualizar(delta);
        actualizarConfeti(delta);
        repaint();
    }

    private void iniciarCelebracion(Color colorPrincipal) {
        confeti.clear();
        int cantidad = colorPrincipal == COLOR_EMPATE ? 38 : 70;

        for (int i = 0; i < cantidad; i++) {
            Color color;
            int variante = aleatorio.nextInt(5);
            if (variante == 0) {
                color = TEXTO;
            } else if (variante == 1) {
                color = COLOR_EMPATE;
            } else if (variante == 2) {
                color = COLOR_X;
            } else if (variante == 3) {
                color = COLOR_O;
            } else {
                color = colorPrincipal;
            }

            double angulo = Math.toRadians(205 + aleatorio.nextDouble() * 130);
            double velocidad = 170 + aleatorio.nextDouble() * 260;
            confeti.add(new Particula(
                    getWidth() / 2.0,
                    Math.max(130, getHeight() * 0.28),
                    Math.cos(angulo) * velocidad,
                    Math.sin(angulo) * velocidad,
                    color,
                    5 + aleatorio.nextInt(7),
                    1.35 + aleatorio.nextDouble() * 0.9,
                    aleatorio.nextDouble() * Math.PI));
        }
    }

    private void actualizarConfeti(double delta) {
        Iterator<Particula> iterador = confeti.iterator();
        while (iterador.hasNext()) {
            Particula particula = iterador.next();
            particula.actualizar(delta);
            if (particula.vida <= 0) {
                iterador.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        int desplazamiento = (int) (Math.sin(tiempoAnimacion * 0.35) * 70);
        g2.setPaint(new GradientPaint(
                desplazamiento, 0, FONDO_SUPERIOR,
                getWidth() - desplazamiento, getHeight(), FONDO_INFERIOR));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int brilloX = (int) (getWidth() * 0.72
                + Math.sin(tiempoAnimacion * 0.45) * 35);
        int brilloY = (int) (getHeight() * 0.20
                + Math.cos(tiempoAnimacion * 0.38) * 25);
        g2.setColor(new Color(56, 189, 248, 13));
        g2.fillOval(brilloX - 170, brilloY - 170, 340, 340);

        int brilloOX = (int) (getWidth() * 0.18
                + Math.cos(tiempoAnimacion * 0.32) * 28);
        int brilloOY = (int) (getHeight() * 0.76
                + Math.sin(tiempoAnimacion * 0.40) * 24);
        g2.setColor(new Color(251, 113, 133, 10));
        g2.fillOval(brilloOX - 145, brilloOY - 145, 290, 290);

        g2.setColor(new Color(255, 255, 255, 8));
        int paso = 34;
        int offset = (int) (tiempoAnimacion * 4) % paso;
        for (int y = -paso + offset; y < getHeight(); y += paso) {
            for (int x = 16; x < getWidth(); x += paso) {
                g2.fillOval(x, y, 2, 2);
            }
        }
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (confeti.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particula particula : confeti) {
            Graphics2D pieza = (Graphics2D) g2.create();
            float opacidad = (float) Math.min(1.0, particula.vida * 1.8);
            pieza.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, opacidad));
            pieza.translate(particula.x, particula.y);
            pieza.rotate(particula.rotacion);
            pieza.setColor(particula.color);
            pieza.fillRoundRect(
                    -particula.tamano / 2,
                    -particula.tamano,
                    particula.tamano,
                    particula.tamano * 2,
                    3, 3);
            pieza.dispose();
        }
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
        temporizadorBot.stop();
        esperandoBot = false;
        turnoActual = 'X';
        partidaTerminada = false;
        confeti.clear();

        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                tablero[fila][columna] = '\0';
                casillas[fila][columna].reiniciar(
                        (fila + columna) * 0.055);
            }
        }

        actualizarPresentacionModo();
        actualizarEstadoTurno();
        actualizarMarcador();
    }

    private void reiniciarPuntuacion() {
        victoriasX = 0;
        victoriasO = 0;
        empates = 0;
        reiniciar();
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

        private final JLabel lblTitulo;
        private final JLabel lblPuntos;
        private int puntos;
        private double rebote;

        TarjetaMarcador(String titulo, Color color) {
            super(SUPERFICIE, 20);
            setLayout(new BorderLayout(0, 1));
            setBorder(BorderFactory.createEmptyBorder(8, 13, 8, 13));
            setPreferredSize(new Dimension(84, 67));

            lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 9));
            lblTitulo.setForeground(TEXTO_SUAVE);

            lblPuntos = new JLabel("0", SwingConstants.CENTER);
            lblPuntos.setFont(new Font("SansSerif", Font.BOLD, 23));
            lblPuntos.setForeground(color);

            add(lblTitulo, BorderLayout.NORTH);
            add(lblPuntos, BorderLayout.CENTER);
        }

        void setTitulo(String titulo) {
            lblTitulo.setText(titulo);
        }

        void setPuntos(int nuevosPuntos) {
            if (puntos != nuevosPuntos) {
                puntos = nuevosPuntos;
                lblPuntos.setText(String.valueOf(nuevosPuntos));
                rebote = 1.0;
            }
        }

        void actualizar(double delta, double tiempo) {
            if (rebote <= 0) {
                return;
            }

            rebote = Math.max(0, rebote - delta * 2.8);
            double curva = Math.sin((1.0 - rebote) * Math.PI);
            lblPuntos.setFont(new Font(
                    "SansSerif", Font.BOLD,
                    23 + (int) Math.round(curva * 6)));
            repaint();
        }
    }

    private static class TarjetaJugador extends TarjetaMarcador {

        private final Color colorJugador;
        private boolean activa;
        private double pulso;

        TarjetaJugador(String titulo, Color colorJugador) {
            super(titulo, colorJugador);
            this.colorJugador = colorJugador;
        }

        void setActiva(boolean activa) {
            this.activa = activa;
            repaint();
        }

        @Override
        void actualizar(double delta, double tiempo) {
            super.actualizar(delta, tiempo);
            pulso = tiempo;
            if (activa) {
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!activa) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            float intensidad = (float) (
                    0.60 + (Math.sin(pulso * 3.2) + 1) * 0.15);
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, intensidad));
            g2.setColor(colorJugador);
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(
                    1, 1, getWidth() - 3, getHeight() - 3,
                    20, 20);
            g2.dispose();
        }
    }

    private static class CasillaButton extends JButton {

        private boolean cursorEncima;
        private boolean ganadora;
        private boolean interaccionPermitida = true;
        private char fichaSugerida = 'X';
        private Color colorSugerencia = COLOR_X;
        private double hoverProgreso;
        private double entradaProgreso = 1;
        private double retrasoEntrada;
        private double marcaProgreso = 1;
        private double impacto;
        private double pulsoGanador;

        CasillaButton() {
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

        void setSugerencia(char ficha, Color color) {
            fichaSugerida = ficha;
            colorSugerencia = color;
        }

        void setInteraccionPermitida(boolean permitida) {
            interaccionPermitida = permitida;
            if (getText().isEmpty() && isEnabled()) {
                setCursor(permitida
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
            if (!permitida) {
                cursorEncima = false;
            }
        }

        void colocarFicha(char ficha, Color color) {
            setText(String.valueOf(ficha));
            setForeground(color);
            marcaProgreso = 0;
            impacto = 1;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        void reiniciar(double retraso) {
            setText("");
            setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ganadora = false;
            interaccionPermitida = true;
            cursorEncima = false;
            hoverProgreso = 0;
            entradaProgreso = 0;
            retrasoEntrada = retraso;
            marcaProgreso = 0;
            impacto = 0;
            repaint();
        }

        void actualizar(double delta, double tiempo) {
            double objetivoHover = cursorEncima
                    && isEnabled()
                    && interaccionPermitida
                    && getText().isEmpty() ? 1 : 0;
            hoverProgreso += (objetivoHover - hoverProgreso)
                    * Math.min(1, delta * 11);

            if (retrasoEntrada > 0) {
                retrasoEntrada -= delta;
            } else if (entradaProgreso < 1) {
                entradaProgreso = Math.min(
                        1, entradaProgreso + delta * 4.6);
            }

            if (!getText().isEmpty() && marcaProgreso < 1) {
                marcaProgreso = Math.min(
                        1, marcaProgreso + delta * 4.8);
            }

            impacto = Math.max(0, impacto - delta * 3.5);
            pulsoGanador = tiempo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            float opacidadEntrada = (float) Math.max(
                    0, Math.min(1, entradaProgreso));
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, opacidadEntrada));

            double salidaSuave = 1
                    - Math.pow(1 - entradaProgreso, 3);
            double escala = 0.72 + salidaSuave * 0.28;
            escala += impacto * 0.035;
            g2.translate(getWidth() / 2.0, getHeight() / 2.0);
            g2.scale(escala, escala);
            g2.translate(-getWidth() / 2.0, -getHeight() / 2.0);

            int sombra = 4;
            g2.setColor(new Color(2, 6, 23, 90));
            g2.fillRoundRect(
                    sombra, sombra,
                    getWidth() - sombra, getHeight() - sombra,
                    26, 26);

            Color fondoCasilla = mezclar(
                    SUPERFICIE, SUPERFICIE_CLARA, hoverProgreso);
            if (getModel().isPressed()) {
                fondoCasilla = new Color(24, 34, 51);
            }
            if (ganadora) {
                double onda = (Math.sin(pulsoGanador * 5.5) + 1) / 2;
                fondoCasilla = mezclar(
                        new Color(42, 55, 75),
                        new Color(62, 79, 103),
                        0.28 + onda * 0.25);
            }

            g2.setColor(fondoCasilla);
            g2.fillRoundRect(
                    0, 0, getWidth() - 4, getHeight() - 4,
                    26, 26);

            if (hoverProgreso > 0.02 && !ganadora) {
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        (float) (opacidadEntrada
                                * hoverProgreso * 0.20)));
                g2.setColor(colorSugerencia);
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(
                        3, 3, getWidth() - 10, getHeight() - 10,
                        24, 24);
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, opacidadEntrada));
            }

            float grosorBorde = ganadora
                    ? (float) (2.8
                            + (Math.sin(pulsoGanador * 5.5) + 1) * 0.7)
                    : 1.25f;
            g2.setStroke(new BasicStroke(grosorBorde));
            g2.setColor(ganadora ? getForeground()
                    : mezclar(BORDE, colorSugerencia,
                            hoverProgreso * 0.45));
            g2.drawRoundRect(
                    1, 1, getWidth() - 6, getHeight() - 6,
                    26, 26);

            String ficha = getText();
            if (!ficha.isEmpty()) {
                dibujarFicha(
                        g2, ficha.charAt(0), getForeground(),
                        marcaProgreso, 1.0);
            } else if (hoverProgreso > 0.03
                    && isEnabled()
                    && interaccionPermitida) {
                dibujarFicha(
                        g2, fichaSugerida, colorSugerencia,
                        1.0, hoverProgreso * 0.22);
            }

            g2.dispose();
        }

        private void dibujarFicha(
                Graphics2D g2,
                char ficha,
                Color color,
                double progreso,
                double opacidad) {

            Graphics2D marca = (Graphics2D) g2.create();
            marca.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    (float) Math.max(0, Math.min(1, opacidad))));
            marca.setColor(color);

            float grosor = Math.max(
                    6f, Math.min(getWidth(), getHeight()) * 0.075f);
            marca.setStroke(new BasicStroke(
                    grosor,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            double margen = Math.min(getWidth(), getHeight()) * 0.285;
            double izquierda = margen;
            double derecha = getWidth() - margen - 4;
            double arriba = margen;
            double abajo = getHeight() - margen - 4;

            if (ficha == 'X') {
                double primerTrazo = Math.min(1, progreso * 2);
                marca.draw(new Line2D.Double(
                        izquierda,
                        arriba,
                        izquierda + (derecha - izquierda) * primerTrazo,
                        arriba + (abajo - arriba) * primerTrazo));

                if (progreso > 0.5) {
                    double segundoTrazo = (progreso - 0.5) * 2;
                    marca.draw(new Line2D.Double(
                            derecha,
                            arriba,
                            derecha - (derecha - izquierda) * segundoTrazo,
                            arriba + (abajo - arriba) * segundoTrazo));
                }
            } else {
                marca.draw(new Arc2D.Double(
                        izquierda,
                        arriba,
                        derecha - izquierda,
                        abajo - arriba,
                        90,
                        -360 * progreso,
                        Arc2D.OPEN));
            }

            marca.dispose();
        }

        private static Color mezclar(
                Color inicio, Color fin, double porcentaje) {
            double p = Math.max(0, Math.min(1, porcentaje));
            return new Color(
                    (int) (inicio.getRed()
                            + (fin.getRed() - inicio.getRed()) * p),
                    (int) (inicio.getGreen()
                            + (fin.getGreen() - inicio.getGreen()) * p),
                    (int) (inicio.getBlue()
                            + (fin.getBlue() - inicio.getBlue()) * p));
        }
    }

    private static class BotonModo extends JButton {

        private boolean seleccionado;
        private boolean cursorEncima;
        private double seleccionProgreso;
        private double hoverProgreso;

        BotonModo(String texto) {
            super(texto);
            setFont(new Font("SansSerif", Font.BOLD, 10));
            setForeground(TEXTO_SUAVE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    cursorEncima = true;
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    cursorEncima = false;
                }
            });
        }

        void setSeleccionado(boolean seleccionado) {
            this.seleccionado = seleccionado;
            repaint();
        }

        void actualizar(double delta) {
            double objetivoSeleccion = seleccionado ? 1 : 0;
            seleccionProgreso += (
                    objetivoSeleccion - seleccionProgreso)
                    * Math.min(1, delta * 12);

            double objetivoHover = cursorEncima ? 1 : 0;
            hoverProgreso += (objetivoHover - hoverProgreso)
                    * Math.min(1, delta * 11);

            setForeground(CasillaButton.mezclar(
                    TEXTO_SUAVE,
                    FONDO_SUPERIOR,
                    seleccionProgreso));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondoHover = new Color(
                    SUPERFICIE_CLARA.getRed(),
                    SUPERFICIE_CLARA.getGreen(),
                    SUPERFICIE_CLARA.getBlue(),
                    (int) (hoverProgreso * 150));
            g2.setColor(fondoHover);
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 12, 12);

            if (seleccionProgreso > 0.01) {
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        (float) seleccionProgreso));
                g2.setColor(COLOR_X);
                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 12, 12);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonAccion extends JButton {

        private final boolean principal;
        private boolean cursorEncima;
        private double hoverProgreso;

        BotonAccion(String texto, boolean principal) {
            super(texto);
            this.principal = principal;
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(principal ? FONDO_SUPERIOR : TEXTO);
            setPreferredSize(new Dimension(
                    principal ? 158 : 172, 38));
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

        void actualizar(double delta) {
            double objetivo = cursorEncima ? 1 : 0;
            hoverProgreso += (objetivo - hoverProgreso)
                    * Math.min(1, delta * 10);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int ajuste = getModel().isPressed() ? 2 : 0;
            g2.setColor(new Color(2, 6, 23, 100));
            g2.fillRoundRect(
                    1, 3, getWidth() - 1, getHeight() - 3,
                    18, 18);

            Color colorFinal;
            if (principal) {
                colorFinal = CasillaButton.mezclar(
                        COLOR_X,
                        new Color(165, 225, 250),
                        hoverProgreso);
                g2.setPaint(new GradientPaint(
                        0, ajuste, colorFinal,
                        getWidth(), getHeight(),
                        CasillaButton.mezclar(
                                colorFinal, COLOR_X, 0.35)));
            } else {
                colorFinal = CasillaButton.mezclar(
                        new Color(38, 51, 70),
                        new Color(58, 75, 98),
                        hoverProgreso);
                g2.setColor(colorFinal);
            }
            g2.fillRoundRect(
                    ajuste, ajuste,
                    getWidth() - ajuste * 2,
                    getHeight() - ajuste * 2,
                    18, 18);

            if (!principal) {
                g2.setColor(CasillaButton.mezclar(
                        BORDE, COLOR_X, hoverProgreso * 0.65));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(
                        ajuste, ajuste,
                        getWidth() - ajuste * 2 - 1,
                        getHeight() - ajuste * 2 - 1,
                        18, 18);
            } else if (hoverProgreso > 0.02) {
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        (float) (hoverProgreso * 0.28)));
                g2.setColor(Color.WHITE);
                int brilloX = (int) (
                        -45 + (getWidth() + 90) * hoverProgreso);
                g2.rotate(-0.25, brilloX, getHeight() / 2.0);
                g2.fillRoundRect(
                        brilloX, -8, 22, getHeight() + 16,
                        12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class Particula {

        private double x;
        private double y;
        private double velocidadX;
        private double velocidadY;
        private final Color color;
        private final int tamano;
        private double vida;
        private double rotacion;
        private final double velocidadRotacion;

        Particula(
                double x,
                double y,
                double velocidadX,
                double velocidadY,
                Color color,
                int tamano,
                double vida,
                double rotacion) {

            this.x = x;
            this.y = y;
            this.velocidadX = velocidadX;
            this.velocidadY = velocidadY;
            this.color = color;
            this.tamano = tamano;
            this.vida = vida;
            this.rotacion = rotacion;
            velocidadRotacion = (Math.random() - 0.5) * 9;
        }

        void actualizar(double delta) {
            x += velocidadX * delta;
            y += velocidadY * delta;
            velocidadY += 390 * delta;
            velocidadX *= Math.pow(0.985, delta * 60);
            rotacion += velocidadRotacion * delta;
            vida -= delta;
        }
    }
}
