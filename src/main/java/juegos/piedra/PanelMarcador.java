package juegos.piedra;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;

/**
 * ============================================================
 * CLASE PanelMarcador
 * ============================================================
 *
 * Panel inferior que muestra el marcador de la partida.
 *
 * Modo Pepe : Tiros | Ganados | Perdidos | Empates | Vidas (corazones)
 * Modo Dios : Tiros | Mis victorias | CPU victorias | Empates | Racha actual
 *
 * @version 1.0
 * ============================================================
 */
public class PanelMarcador extends JPanel {

    private final boolean modoPepe;

    private JLabel lblTiros;
    private JLabel lblGanados;
    private JLabel lblPerdidos;
    private JLabel lblEmpates;
    private JLabel lblEspecial;

    // =========================================================
    // Constructor
    // =========================================================

    /**
     * @param modoPepe true si se juega en Modo Pepe, false si es Modo Dios.
     */
    public PanelMarcador(boolean modoPepe) {
        this.modoPepe = modoPepe;
        inicializarComponentes();
    }

    // =========================================================
    // Inicialización
    // =========================================================

    private void inicializarComponentes() {

        setBackground(new Color(18, 18, 24));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(70, 70, 100)),
                BorderFactory.createEmptyBorder(7, 20, 7, 20)
        ));
        setLayout(new FlowLayout(FlowLayout.CENTER, 28, 3));

        lblTiros    = crearLabel("Tiros: 0",         Color.WHITE);
        lblGanados  = crearLabel("Victorias: 0",     new Color(80, 220, 110));
        lblPerdidos = crearLabel("Derrotas: 0",      new Color(220, 80, 80));
        lblEmpates  = crearLabel("Empates: 0",       new Color(200, 200, 70));
        lblEspecial = crearLabel(
                modoPepe ? "Vidas: 5" : "Mi racha: 0",
                modoPepe ? new Color(255, 100, 100) : new Color(255, 165, 0)
        );

        add(lblTiros);
        add(lblGanados);
        add(lblPerdidos);
        add(lblEmpates);
        add(lblEspecial);
    }

    // =========================================================
    // API pública
    // =========================================================

    /**
     * Actualiza todos los contadores del marcador.
     *
     * @param tiros           Total de tiros jugados.
     * @param ganadosJugador  Victorias del jugador.
     * @param perdidosJugador Derrotas del jugador (= victorias CPU).
     * @param empates         Empates totales.
     * @param especial        En Modo Pepe: vidas restantes.
     *                        En Modo Dios: racha consecutiva del jugador.
     */
    public void actualizar(int tiros, int ganadosJugador,
                           int perdidosJugador, int empates, int especial) {

        lblTiros.setText("🎯 Tiros: " + tiros);
        lblGanados.setText("✅ " + (modoPepe ? "Ganados: " : "Mis victorias: ") + ganadosJugador);
        lblPerdidos.setText((modoPepe ? "❌ Perdidos: " : "🤖 CPU: ") + perdidosJugador);
        lblEmpates.setText("🤝 Empates: " + empates);

        if (modoPepe) {
            StringBuilder vidas = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                vidas.append(i < especial ? "♥ " : "♡ ");
            }
            lblEspecial.setText(vidas.toString().trim());
            lblEspecial.setForeground(especial <= 1
                    ? new Color(255, 60, 60)
                    : new Color(255, 100, 100));
        } else {
            lblEspecial.setText("🔥 Mi racha: " + especial);
            lblEspecial.setForeground(especial >= 10
                    ? new Color(255, 215, 0)
                    : new Color(255, 165, 0));
        }
    }

    /**
     * Reinicia el marcador a sus valores iniciales.
     */
    public void reiniciar() {
        actualizar(0, 0, 0, 0, modoPepe ? 5 : 0);
    }

    // =========================================================
    // Helpers
    // =========================================================

    private JLabel crearLabel(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(color);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        return lbl;
    }
}
