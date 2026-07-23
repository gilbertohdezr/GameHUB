package juegos.piedra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * ============================================================
 * CLASE PanelCountdown
 * ============================================================
 *
 * Overlay semitransparente que muestra una cuenta regresiva
 * de 3 → 2 → 1 → ¡YA! antes de que comience la partida.
 *
 * Uso típico:
 *   PanelCountdown cd = new PanelCountdown(() -> iniciarJuego());
 *   layeredPane.add(cd, JLayeredPane.POPUP_LAYER);
 *
 * El panel se gestiona directamente en PanelPiedra usando
 * un CardLayout interno (capa countdown / capa juego).
 *
 * @version 1.0
 * ============================================================
 */
public class PanelCountdown extends JPanel {

    /** Callback que se ejecuta cuando el countdown termina. */
    private final Runnable onTerminado;

    private final JLabel lblNumero;
    private final JLabel lblSubtitulo;

    private int cuenta = 3;
    private Timer timer;

    // Colores de cada número
    private static final Color[] COLORES = {
        new Color(220, 80,  80),   // 3 — rojo
        new Color(220, 160, 40),   // 2 — naranja
        new Color(80,  200, 100),  // 1 — verde
        new Color(255, 255, 255)   // ¡YA! — blanco
    };

    // =========================================================
    // Constructor
    // =========================================================

    /**
     * @param onTerminado Callback que se dispara al llegar a 0.
     */
    public PanelCountdown(Runnable onTerminado) {
        this.onTerminado = onTerminado;
        setOpaque(false); // fondo dibujado manualmente (semitransparente)
        setLayout(new BorderLayout());

        // Número grande en el centro
        lblNumero = new JLabel("3", SwingConstants.CENTER);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 140));
        lblNumero.setForeground(COLORES[0]);

        // Texto debajo del número
        lblSubtitulo = new JLabel("Preparate...", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("SansSerif", Font.ITALIC, 22));
        lblSubtitulo.setForeground(new Color(220, 220, 220));

        // Panel centrado verticalmente
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(lblNumero,   BorderLayout.CENTER);
        centro.add(lblSubtitulo, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);
    }

    // =========================================================
    // Dibuja el fondo semitransparente
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // Fondo negro con 75 % de opacidad
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    // =========================================================
    // Control del countdown
    // =========================================================

    /**
     * Inicia la cuenta regresiva.
     * La música NO se inicia aquí; se inicia en el callback onTerminado.
     */
    public void iniciar() {
        cuenta = 3;
        actualizarVista();

        timer = new Timer(1000, e -> {
            cuenta--;
            if (cuenta > 0) {
                actualizarVista();
            } else {
                // Mostrar "¡YA!" brevemente y luego notificar
                lblNumero.setText("¡YA!");
                lblNumero.setForeground(COLORES[3]);
                lblSubtitulo.setText("¡Que empiece el juego!");
                repaint();

                // Esperar 600 ms y disparar callback
                Timer tFinal = new Timer(600, ev -> {
                    detener();
                    if (onTerminado != null) onTerminado.run();
                    ((Timer) ev.getSource()).stop();
                });
                tFinal.setRepeats(false);
                tFinal.start();

                ((Timer) e.getSource()).stop();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    /** Detiene el timer si estaba corriendo. */
    public void detener() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    // =========================================================
    // Helpers
    // =========================================================

    private void actualizarVista() {
        int idx = 3 - cuenta; // 0=3, 1=2, 2=1
        lblNumero.setText(String.valueOf(cuenta));
        lblNumero.setForeground(COLORES[Math.min(idx, COLORES.length - 1)]);
        lblSubtitulo.setText(switch (cuenta) {
            case 3 -> "Prepárate...";
            case 2 -> "¡Ya casi!";
            case 1 -> "¡Última oportunidad!";
            default -> "¡Que empiece el juego!";
        });
        repaint();
    }
}
