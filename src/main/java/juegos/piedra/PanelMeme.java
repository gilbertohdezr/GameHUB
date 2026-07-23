package juegos.piedra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * ============================================================
 * CLASE PanelMeme
 * ============================================================
 *
 * Panel reutilizable que muestra la cara del meme de un jugador
 * y la imagen de su última jugada (mano).
 *
 * Soporta tres modos de cambio de imagen:
 *  - flashMeme()       : muestra un meme 1.5 seg y revierte al base.
 *  - mostrarMeme()     : muestra un meme de forma permanente (nuevo base).
 *  - setBaseRetorno()  : actualiza el base sin cambiar imagen actual,
 *                        de modo que el próximo flash revierta al nuevo base.
 *
 * @version 1.0
 * ============================================================
 */
public class PanelMeme extends JPanel {

    // ---- Rutas de imágenes de memes ----
    public static final String MEME_DEFAULT  = "/images/Piedra_Papel_0_Tijeras/Default.png";
    public static final String MEME_PENSANDO = "/images/Piedra_Papel_0_Tijeras/Pensando.png";
    public static final String MEME_TE_GANE  = "/images/Piedra_Papel_0_Tijeras/Te_gane.png";
    public static final String MEME_ENOJADO  = "/images/Piedra_Papel_0_Tijeras/Enojado.png";
    public static final String MEME_OMG      = "/images/Piedra_Papel_0_Tijeras/OMG.png";
    public static final String MEME_MMMM     = "/images/Piedra_Papel_0_Tijeras/mmmmmmm.png";
    public static final String MEME_EMPATE   = "/images/Piedra_Papel_0_Tijeras/Empate.png";
    public static final String MEME_RACHA    = "/images/Piedra_Papel_0_Tijeras/Racha.png";
    public static final String MEME_PERO_QUE = "/images/Piedra_Papel_0_Tijeras/Pero_que_a_pasado.png";

    /** Duración del flash en milisegundos. */
    private static final int FLASH_MS   = 1500;
    private static final int MEME_SIZE  = 140;
    private static final int MANO_SIZE  = 88;

    // ---- Componentes internos ----
    private final JLabel lblNombre;
    private final JLabel lblMeme;
    private final JLabel lblMano;
    private final JLabel lblJugada;

    /** Imagen base a la que se revierte después de un flash. */
    private String memeBase = MEME_DEFAULT;

    /** Timer para transiciones temporales. */
    private Timer timerFlash;

    // =========================================================
    // Constructor
    // =========================================================

    /**
     * @param nombre      Nombre del jugador ("TÚ" o "CPU").
     * @param colorNombre Color del texto del nombre.
     */
    public PanelMeme(String nombre, Color colorNombre) {

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        lblNombre = crearLabel(nombre, colorNombre,
                new Font("SansSerif", Font.BOLD, 17));

        lblMeme = new JLabel();
        lblMeme.setAlignmentX(CENTER_ALIGNMENT);

        lblMano = new JLabel();
        lblMano.setAlignmentX(CENTER_ALIGNMENT);

        lblJugada = crearLabel(" ", new Color(160, 160, 180),
                new Font("SansSerif", Font.ITALIC, 13));

        // Estado inicial
        aplicarImagenMeme(MEME_DEFAULT);

        add(lblNombre);
        add(Box.createVerticalStrut(8));
        add(lblMeme);
        add(Box.createVerticalStrut(6));
        add(lblMano);
        add(Box.createVerticalStrut(3));
        add(lblJugada);
    }

    // =========================================================
    // API pública
    // =========================================================

    /**
     * Muestra un meme durante 1.5 segundos y luego revierte al memeBase.
     *
     * @param ruta Ruta del meme a mostrar temporalmente.
     */
    public void flashMeme(String ruta) {
        detenerTimer();
        aplicarImagenMeme(ruta);
        timerFlash = new Timer(FLASH_MS, e -> {
            aplicarImagenMeme(memeBase);
            ((Timer) e.getSource()).stop();
        });
        timerFlash.setRepeats(false);
        timerFlash.start();
    }

    /**
     * Muestra un meme de forma permanente y lo establece como base.
     * Detiene cualquier flash en curso.
     *
     * @param ruta Ruta del meme permanente.
     */
    public void mostrarMeme(String ruta) {
        detenerTimer();
        memeBase = ruta;
        aplicarImagenMeme(ruta);
    }

    /**
     * Actualiza el memeBase sin cambiar la imagen actualmente visible.
     * Útil para que el próximo flash revierta a una imagen diferente.
     * Si hay un flash en curso, éste revertirá al nuevo base al terminar.
     *
     * @param ruta Nueva ruta base de retorno.
     */
    public void setBaseRetorno(String ruta) {
        memeBase = ruta;
    }

    /**
     * Muestra la imagen de la mano (última jugada) y su nombre.
     *
     * @param jugada Jugada a mostrar, o null para ocultar.
     */
    public void setJugada(Jugada jugada) {
        if (jugada == null) {
            lblMano.setIcon(null);
            lblJugada.setText(" ");
        } else {
            aplicarImagenMano(jugada.getRutaImagen());
            lblJugada.setText(jugada.getNombre());
        }
    }

    /**
     * Reinicia el panel al estado inicial: meme default, sin mano.
     */
    public void reiniciar() {
        detenerTimer();
        memeBase = MEME_DEFAULT;
        aplicarImagenMeme(MEME_DEFAULT);
        lblMano.setIcon(null);
        lblJugada.setText(" ");
    }

    // =========================================================
    // Métodos privados
    // =========================================================

    private void detenerTimer() {
        if (timerFlash != null && timerFlash.isRunning()) {
            timerFlash.stop();
        }
    }

    private void aplicarImagenMeme(String ruta) {
        ImageIcon icon = cargarImagen(ruta, MEME_SIZE, MEME_SIZE);
        if (icon != null) {
            lblMeme.setIcon(icon);
            lblMeme.setText(null);
        } else {
            lblMeme.setIcon(null);
            lblMeme.setText("?");
        }
    }

    private void aplicarImagenMano(String ruta) {
        ImageIcon icon = cargarImagen(ruta, MANO_SIZE, MANO_SIZE);
        if (icon != null) {
            lblMano.setIcon(icon);
            lblMano.setText(null);
        } else {
            lblMano.setIcon(null);
            lblMano.setText("?");
        }
    }

    private ImageIcon cargarImagen(String ruta, int ancho, int alto) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private JLabel crearLabel(String texto, Color color, Font fuente) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        lbl.setForeground(color);
        lbl.setFont(fuente);
        return lbl;
    }
}
