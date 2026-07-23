package juegos.dados;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Dado gráfico escalable dibujado con Graphics2D.
 */
public class DadoPanel extends JPanel {
    private int valor = 1;
    private int desplazamiento;

    public DadoPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(78, 78));
        setMinimumSize(new Dimension(46, 46));
    }

    public void setValor(int valor) {
        this.valor = Math.max(1, Math.min(6, valor));
        repaint();
    }

    public void setDesplazamiento(int desplazamiento) {
        this.desplazamiento = desplazamiento;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int lado = Math.max(34, Math.min(getWidth(), getHeight()) - 16);
        int x = (getWidth() - lado) / 2;
        int y = (getHeight() - lado) / 2 + desplazamiento;
        int arco = Math.max(12, lado / 5);

        g.setColor(new Color(42, 91, 58, 65));
        g.fillRoundRect(x + 3, y + 7, lado, lado, arco, arco);
        g.setColor(new Color(255, 253, 244));
        g.fillRoundRect(x, y, lado, lado, arco, arco);
        g.setColor(new Color(232, 222, 201));
        g.drawRoundRect(x, y, lado, lado, arco, arco);

        int margen = lado / 4;
        int centroX = x + lado / 2;
        int centroY = y + lado / 2;
        int izquierda = x + margen;
        int derecha = x + lado - margen;
        int arriba = y + margen;
        int abajo = y + lado - margen;
        int radio = Math.max(4, lado / 13);

        g.setColor(new Color(52, 72, 61));
        if (valor != 1) {
            punto(g, izquierda, arriba, radio);
            punto(g, derecha, abajo, radio);
        }
        if (valor >= 4) {
            punto(g, derecha, arriba, radio);
            punto(g, izquierda, abajo, radio);
        }
        if (valor == 6) {
            punto(g, izquierda, centroY, radio);
            punto(g, derecha, centroY, radio);
        }
        if (valor == 1 || valor == 3 || valor == 5) {
            punto(g, centroX, centroY, radio);
        }
        g.dispose();
    }

    private void punto(Graphics2D g, int x, int y, int radio) {
        g.fillOval(x - radio, y - radio, radio * 2, radio * 2);
    }
}
