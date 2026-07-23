package juegos.piedra;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * ============================================================
 * CLASE ReproductorMusica
 * ============================================================
 *
 * Maneja la reproducción en loop del soundtrack del juego.
 * Usa javax.sound.sampled con un archivo WAV en resources.
 *
 * Uso:
 *   ReproductorMusica.iniciar();   // empieza o continúa
 *   ReproductorMusica.detener();   // pausa y cierra
 *
 * Es estático para que el clip sobreviva cambios de panel.
 *
 * @version 1.0
 * ============================================================
 */
public class ReproductorMusica {

    private static final String RUTA_WAV = "/Songs/sountrack_Piedra.wav";

    /** Clip compartido (singleton). */
    private static Clip clip;

    /** Evita instanciación. */
    private ReproductorMusica() {}

    // =========================================================
    // API pública
    // =========================================================

    /**
     * Inicia la música en loop desde el principio.
     * Si ya está sonando no hace nada.
     */
    public static void iniciar() {
        if (clip != null && clip.isRunning()) {
            return; // ya suena
        }
        try {
            // Cerrar clip anterior si existe pero no corre
            if (clip != null) {
                clip.close();
                clip = null;
            }

            URL url = ReproductorMusica.class.getResource(RUTA_WAV);
            if (url == null) return;

            InputStream is   = url.openStream();
            InputStream bis  = new BufferedInputStream(is);
            AudioInputStream audio = AudioSystem.getAudioInputStream(bis);

            clip = AudioSystem.getClip();
            clip.open(audio);

            // Volumen al 80 %
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float rango = vol.getMaximum() - vol.getMinimum();
                vol.setValue(vol.getMinimum() + rango * 0.80f);
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            // Si el audio falla, el juego sigue sin música
            clip = null;
        }
    }

    /**
     * Detiene y libera el clip de audio.
     */
    public static void detener() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.close();
            clip = null;
        }
    }

    /**
     * Indica si la música está reproduciéndose en este momento.
     */
    public static boolean estaReproduciendo() {
        return clip != null && clip.isRunning();
    }
}
