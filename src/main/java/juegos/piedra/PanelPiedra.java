package juegos.piedra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * ============================================================
 * CLASE PanelPiedra
 * ============================================================
 *
 * Panel principal del juego Piedra, Papel o Tijeras.
 * Gestiona la lógica de los dos modos de juego:
 *
 *  - MODO_PEPE : 5 vidas, si llegan a 0 → Game Over.
 *  - MODO_DIOS : Infinito. Cada 10 victorias acumuladas
 *                el primero en alcanzar el múltiplo activa
 *                la carita Racha; se mantiene hasta perder
 *                un tiro.
 *
 * Las caritas del meme aparecen en AMBOS lados (jugador y CPU)
 * y los flashes duran 1.5 seg antes de regresar al estado base.
 *
 * @version 1.0
 * ============================================================
 */
public class PanelPiedra extends JPanel {

    // =========================================================
    // Constantes de modo
    // =========================================================

    public static final int MODO_PEPE = 0;
    public static final int MODO_DIOS = 1;

    // =========================================================
    // Estado del juego
    // =========================================================

    private final int      modo;
    private final Runnable onCambiarModo;

    private int     tirosJugados    = 0;
    private int     ganadosJugador  = 0; // victorias totales jugador
    private int     perdidosJugador = 0; // victorias totales CPU
    private int     empates         = 0;

    // Modo Pepe
    private int     vidasPepe = 5;

    // Modo Dios — rachas consecutivas (se resetean al perder, NO en empates)
    private int     rachaConsJugador  = 0;
    private int     rachaConsMaquina  = 0;

    // Modo Dios — ¿tiene activa la carita Racha?
    private boolean rachaActivaJugador  = false;
    private boolean rachaActivaMaquina  = false;

    // =========================================================
    // Componentes UI
    // =========================================================

    private PanelMeme   memeJugador;
    private PanelMeme   memeMaquina;
    private JLabel      lblResultado;
    private JLabel      lblMensaje;
    private JButton     btnPiedra;
    private JButton     btnPapel;
    private JButton     btnTijeras;
    private JButton     btnReiniciar;
    private PanelMarcador marcador;

    // =========================================================
    // Constructor
    // =========================================================

    /**
     * @param modo         MODO_PEPE o MODO_DIOS.
     * @param onCambiarModo Callback que se invoca al pulsar "Cambiar modo".
     */
    public PanelPiedra(int modo, Runnable onCambiarModo) {
        this.modo         = modo;
        this.onCambiarModo = onCambiarModo;
        inicializarComponentes();
    }

    // =========================================================
    // Construcción de la UI
    // =========================================================

    private void inicializarComponentes() {
        setBackground(new Color(28, 28, 35));
        setLayout(new BorderLayout());

        add(crearNorth(),  BorderLayout.NORTH);
        add(crearCenter(), BorderLayout.CENTER);
        add(crearSouth(),  BorderLayout.SOUTH);
    }

    // ---- NORTH: barra de título ----

    private JPanel crearNorth() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 26));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(70, 70, 100)),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));

        JLabel lblTitulo = new JLabel("✋  Piedra, Papel o Tijeras");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblModo = new JLabel(modo == MODO_PEPE
                ? "   |   🐸 Modo Pepe  —  5 vidas"
                : "   |   ⚡ Modo Dios  —  Sin límite");
        lblModo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblModo.setForeground(modo == MODO_PEPE
                ? new Color(100, 220, 100)
                : new Color(255, 215, 0));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        izq.add(lblTitulo);
        izq.add(lblModo);

        JButton btnCambiar = botonSecundario("← Cambiar modo");
        btnCambiar.addActionListener(e -> {
            if (onCambiarModo != null) onCambiarModo.run();
        });

        panel.add(izq,        BorderLayout.WEST);
        panel.add(btnCambiar, BorderLayout.EAST);

        return panel;
    }

    // ---- CENTER: área de juego (jugador | resultado | CPU) ----

    private JPanel crearCenter() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 6, 14));

        memeJugador = new PanelMeme("TÚ",  new Color(100, 200, 255));
        memeMaquina = new PanelMeme("CPU", new Color(255, 120, 120));

        JPanel wrapJ = envoltorioMeme(memeJugador, new Color(30, 42, 56));
        JPanel wrapM = envoltorioMeme(memeMaquina, new Color(52, 30, 30));

        panel.add(wrapJ,           BorderLayout.WEST);
        panel.add(crearResultado(), BorderLayout.CENTER);
        panel.add(wrapM,           BorderLayout.EAST);

        return panel;
    }

    private JPanel envoltorioMeme(PanelMeme meme, Color fondo) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(fondo);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 100), 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        wrap.setPreferredSize(new Dimension(195, 0));
        wrap.add(meme, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel crearResultado() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblVS = new JLabel("VS", SwingConstants.CENTER);
        lblVS.setAlignmentX(CENTER_ALIGNMENT);
        lblVS.setFont(new Font("SansSerif", Font.BOLD, 44));
        lblVS.setForeground(new Color(170, 170, 180));

        lblResultado = new JLabel("¡Elige tu jugada!", SwingConstants.CENTER);
        lblResultado.setAlignmentX(CENTER_ALIGNMENT);
        lblResultado.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblResultado.setForeground(new Color(200, 200, 210));

        lblMensaje = new JLabel("Escoge Piedra, Papel o Tijeras", SwingConstants.CENTER);
        lblMensaje.setAlignmentX(CENTER_ALIGNMENT);
        lblMensaje.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblMensaje.setForeground(new Color(150, 150, 165));

        panel.add(Box.createVerticalGlue());
        panel.add(lblVS);
        panel.add(Box.createVerticalStrut(16));
        panel.add(lblResultado);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblMensaje);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ---- SOUTH: botones de jugada + marcador ----

    private JPanel crearSouth() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Fila de botones
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
        filaBotones.setOpaque(false);

        btnPiedra  = botonJugada("✊  Piedra",  Jugada.PIEDRA,  new Color(120, 85, 45));
        btnPapel   = botonJugada("✋  Papel",   Jugada.PAPEL,   new Color(50, 95, 155));
        btnTijeras = botonJugada("✌  Tijeras", Jugada.TIJERAS, new Color(155, 50, 50));
        btnReiniciar = botonSecundario("🔄 Nueva partida");
        btnReiniciar.addActionListener(e -> reiniciar());

        filaBotones.add(btnPiedra);
        filaBotones.add(btnPapel);
        filaBotones.add(btnTijeras);
        filaBotones.add(Box.createHorizontalStrut(12));
        filaBotones.add(btnReiniciar);

        // Marcador
        marcador = new PanelMarcador(modo == MODO_PEPE);
        marcador.reiniciar();

        panel.add(filaBotones);
        panel.add(marcador);

        return panel;
    }

    // =========================================================
    // Lógica de juego
    // =========================================================

    /**
     * Ejecuta un tiro completo con la jugada del jugador.
     * Deshabilita los botones durante el flash (1.7 seg).
     */
    private void jugar(Jugada jugadaJugador) {
        setBotonesEnabled(false);

        Jugada jugadaMaquina = LogicaPPT.generarJugadaMaquina();
        ResultadoTiro resultado = LogicaPPT.calcularResultado(jugadaJugador, jugadaMaquina);

        memeJugador.setJugada(jugadaJugador);
        memeMaquina.setJugada(jugadaMaquina);

        tirosJugados++;

        switch (resultado) {
            case GANA:   procesarGana(jugadaJugador, jugadaMaquina);   break;
            case PIERDE: procesarPierde(jugadaJugador, jugadaMaquina); break;
            case EMPATE: procesarEmpate(jugadaJugador);                break;
        }

        actualizarMarcador();

        // Re-habilitar botones tras el flash
        Timer tRehabilitar = new Timer(1700, e -> {
            boolean finJuego = (modo == MODO_PEPE && vidasPepe <= 0);
            if (!finJuego) setBotonesEnabled(true);
            ((Timer) e.getSource()).stop();
        });
        tRehabilitar.setRepeats(false);
        tRehabilitar.start();
    }

    // ---- Procesar victoria del jugador ----

    private void procesarGana(Jugada j, Jugada m) {
        ganadosJugador++;
        lblResultado.setText("¡GANASTE! 🎉");
        lblResultado.setForeground(new Color(80, 220, 100));

        if (modo == MODO_DIOS) {
            rachaConsJugador++;
            rachaConsMaquina = 0; // CPU pierde racha consecutiva

            boolean milestone = (ganadosJugador % 10 == 0); // 10, 20, 30...
            if (milestone) rachaActivaJugador = true;

            // Si CPU tenía Racha activa, la pierde
            if (rachaActivaMaquina) {
                rachaActivaMaquina = false;
                memeMaquina.setBaseRetorno(PanelMeme.MEME_DEFAULT);
            }

            if (rachaActivaJugador) {
                // Jugador en Racha — la carita se queda
                memeJugador.mostrarMeme(PanelMeme.MEME_RACHA);
                memeMaquina.flashMeme(PanelMeme.MEME_ENOJADO);
                if (milestone) {
                    lblMensaje.setText("🏆 ¡MILESTONE " + ganadosJugador + "! ¡Racha activada!");
                } else {
                    lblMensaje.setText("🔥 Racha activa — " + rachaConsJugador + " victorias seguidas");
                }
            } else {
                memeJugador.flashMeme(PanelMeme.MEME_TE_GANE);
                memeMaquina.flashMeme(PanelMeme.MEME_ENOJADO);
                lblMensaje.setText(j.getNombre() + " vence a " + m.getNombre());
            }

        } else {
            // Modo Pepe: si tenía OMG (1 vida), al ganar vuelve a default
            if (vidasPepe == 1) {
                memeJugador.setBaseRetorno(PanelMeme.MEME_DEFAULT);
            }
            memeJugador.flashMeme(PanelMeme.MEME_TE_GANE);
            memeMaquina.flashMeme(PanelMeme.MEME_ENOJADO);
            lblMensaje.setText(j.getNombre() + " vence a " + m.getNombre());
        }
    }

    // ---- Procesar derrota del jugador ----

    private void procesarPierde(Jugada j, Jugada m) {
        perdidosJugador++;
        lblResultado.setText("¡PERDISTE! 😤");
        lblResultado.setForeground(new Color(220, 80, 80));

        if (modo == MODO_DIOS) {
            rachaConsMaquina++;
            rachaConsJugador = 0; // Jugador pierde racha consecutiva

            boolean milestone = (perdidosJugador % 10 == 0); // CPU: 10, 20, 30...
            if (milestone) rachaActivaMaquina = true;

            // Si jugador tenía Racha activa, la pierde
            if (rachaActivaJugador) {
                rachaActivaJugador = false;
                // Flash "Pero qué ha pasado" y luego vuelve a default
                memeJugador.setBaseRetorno(PanelMeme.MEME_DEFAULT);
                memeJugador.flashMeme(PanelMeme.MEME_PERO_QUE);
            } else {
                memeJugador.flashMeme(PanelMeme.MEME_ENOJADO);
            }

            if (rachaActivaMaquina) {
                memeMaquina.mostrarMeme(PanelMeme.MEME_RACHA);
                if (milestone) {
                    lblMensaje.setText("🤖 ¡CPU MILESTONE " + perdidosJugador + "! CPU en racha!");
                } else {
                    lblMensaje.setText("🤖 CPU en racha — " + rachaConsMaquina + " victorias seguidas");
                }
            } else {
                memeMaquina.flashMeme(PanelMeme.MEME_TE_GANE);
                lblMensaje.setText(m.getNombre() + " vence a " + j.getNombre());
            }

        } else {
            // Modo Pepe
            vidasPepe--;

            if (vidasPepe > 1) {
                // Pérdida normal — flash enojado y vuelve a default
                memeJugador.flashMeme(PanelMeme.MEME_ENOJADO);
                lblMensaje.setText(m.getNombre() + " vence a " + j.getNombre()
                        + " — Vidas: " + vidasPepe);
            } else if (vidasPepe == 1) {
                // ¡Última vida! — flash enojado y luego OMG permanente
                memeJugador.setBaseRetorno(PanelMeme.MEME_OMG);
                memeJugador.flashMeme(PanelMeme.MEME_ENOJADO);
                lblMensaje.setText("⚠️ ¡ÚLTIMA VIDA! ¡No falles más!");
            } else {
                // 0 vidas — game over
                memeJugador.mostrarMeme(PanelMeme.MEME_ENOJADO);
                lblMensaje.setText("💀 ¡Sin vidas! Game Over.");
            }

            memeMaquina.flashMeme(PanelMeme.MEME_TE_GANE);

            if (vidasPepe <= 0) {
                Timer tGameOver = new Timer(1900, e -> {
                    mostrarGameOver();
                    ((Timer) e.getSource()).stop();
                });
                tGameOver.setRepeats(false);
                tGameOver.start();
            }
        }
    }

    // ---- Procesar empate ----

    private void procesarEmpate(Jugada j) {
        empates++;
        lblResultado.setText("¡EMPATE! 🤝");
        lblResultado.setForeground(new Color(200, 200, 70));

        // Las rachas activas NO se rompen en empate
        // Pero las rachas consecutivas sí se detienen (no suman)

        if (j == Jugada.TIJERAS) {
            // Caso especial: Tijeras vs Tijeras → mmmmmmm
            memeJugador.flashMeme(PanelMeme.MEME_MMMM);
            memeMaquina.flashMeme(PanelMeme.MEME_MMMM);
            lblMensaje.setText("✌ Mmmmm... dos tijeras... qué interesante");
        } else {
            memeJugador.flashMeme(PanelMeme.MEME_EMPATE);
            memeMaquina.flashMeme(PanelMeme.MEME_EMPATE);
            lblMensaje.setText("Ambos eligieron " + j.getNombre() + " — ¡Sin ganador!");
        }
    }

    // =========================================================
    // Marcador y estado
    // =========================================================

    private void actualizarMarcador() {
        int especial = (modo == MODO_PEPE) ? vidasPepe : rachaConsJugador;
        marcador.actualizar(tirosJugados, ganadosJugador, perdidosJugador, empates, especial);
    }

    private void setBotonesEnabled(boolean habilitados) {
        btnPiedra.setEnabled(habilitados);
        btnPapel.setEnabled(habilitados);
        btnTijeras.setEnabled(habilitados);
    }

    // =========================================================
    // Game Over (Modo Pepe)
    // =========================================================

    private void mostrarGameOver() {
        lblResultado.setText("💀  GAME OVER");
        lblResultado.setForeground(new Color(255, 60, 60));

        int opcion = JOptionPane.showOptionDialog(
                this,
                "<html><center>"
                + "<b style='font-size:14px'>💀 ¡GAME OVER! — Modo Pepe</b><br><br>"
                + "Perdiste todas tus <b>5 vidas</b>.<br><br>"
                + "🎯 Tiros: <b>" + tirosJugados + "</b>&nbsp;&nbsp;"
                + "✅ Ganados: <b>" + ganadosJugador + "</b>&nbsp;&nbsp;"
                + "❌ Perdidos: <b>" + perdidosJugador + "</b>&nbsp;&nbsp;"
                + "🤝 Empates: <b>" + empates + "</b>"
                + "</center></html>",
                "Game Over — Modo Pepe",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"🔄 Jugar de nuevo", "🏠 Cambiar modo"},
                "🔄 Jugar de nuevo"
        );

        if (opcion == 0) {
            reiniciar();
        } else {
            if (onCambiarModo != null) onCambiarModo.run();
        }
    }

    // =========================================================
    // Reinicio completo
    // =========================================================

    /**
     * Reinicia todas las estadísticas y el estado visual al inicio.
     */
    public void reiniciar() {
        tirosJugados      = 0;
        ganadosJugador    = 0;
        perdidosJugador   = 0;
        empates           = 0;
        vidasPepe         = 5;
        rachaConsJugador  = 0;
        rachaConsMaquina  = 0;
        rachaActivaJugador = false;
        rachaActivaMaquina = false;

        memeJugador.reiniciar();
        memeMaquina.reiniciar();

        lblResultado.setText("¡Elige tu jugada!");
        lblResultado.setForeground(new Color(200, 200, 210));
        lblMensaje.setText("Escoge Piedra, Papel o Tijeras");

        marcador.reiniciar();
        setBotonesEnabled(true);
    }

    // =========================================================
    // Helpers de UI
    // =========================================================

    private JButton botonJugada(String texto, Jugada jugada, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 48));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> jugar(jugada));

        // Imagen de la mano encima del texto
        try {
            URL url = getClass().getResource(jugada.getRutaImagen());
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            }
        } catch (Exception ignored) {}

        Color hover = color.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(65, 65, 85));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
