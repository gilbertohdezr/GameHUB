package juegos.dados;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Interfaz Swing nativa del Juego de Dados.
 */
public class PanelDados extends JPanel {
    private static final Color CIELO = new Color(128, 213, 238);
    private static final Color VERDE = new Color(91, 177, 103);
    private static final Color VERDE_OSCURO = new Color(55, 123, 76);
    private static final Color CREMA = new Color(255, 250, 240);
    private static final Color AMARILLO = new Color(255, 198, 63);
    private static final Color TINTA = new Color(51, 70, 58);
    private static final int[] COSTOS = {10, 25, 50, 100, 250, 500};

    private final DadosGame juego;
    private final Runnable volverAlMenu;
    private final DadoPanel[] dados = new DadoPanel[5];
    private final Map<DadosGame.Reto, JToggleButton> botonesReto = new LinkedHashMap<>();
    private final ButtonGroup grupoCostos = new ButtonGroup();
    private final ButtonGroup grupoRetos = new ButtonGroup();
    private final JPanel historial = new JPanel();
    private final JComboBox<String> opciones = new JComboBox<>();
    private final JLabel puntos = etiqueta("", 18, Font.BOLD);
    private final JLabel nivel = etiqueta("", 14, Font.BOLD);
    private final JLabel aciertos = etiqueta("0", 23, Font.BOLD);
    private final JLabel racha = etiqueta("0", 23, Font.BOLD);
    private final JLabel record = etiqueta("0", 23, Font.BOLD);
    private final JLabel resultadoTitulo = etiqueta("Elige un reto", 22, Font.BOLD);
    private final JLabel resultadoDetalle = etiqueta("Todo listo para jugar", 13, Font.PLAIN);
    private final JButton lanzar = botonPrincipal("🎲  AGITAR EL JUEGO");
    private int costoSeleccionado;
    private DadosGame.Reto retoSeleccionado;
    private Timer animacion;

    public PanelDados(DadosGame juego, Runnable volverAlMenu) {
        this.juego = juego;
        this.volverAlMenu = volverAlMenu;
        construirInterfaz();
        actualizarDesdeJuego();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(16, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));
        setBackground(CIELO);
        add(crearCabecera(), BorderLayout.NORTH);

        JPanel columnas = new JPanel(new GridBagLayout());
        columnas.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 14);

        gbc.gridx = 0;
        gbc.weightx = .25;
        JPanel panelRetos = crearPanelRetos();
        panelRetos.setMinimumSize(new Dimension(235, 520));
        columnas.add(panelRetos, gbc);

        gbc.gridx = 1;
        gbc.weightx = .55;
        JPanel zonaCentral = crearZonaCentral();
        zonaCentral.setMinimumSize(new Dimension(350, 520));
        columnas.add(zonaCentral, gbc);

        gbc.gridx = 2;
        gbc.weightx = .25;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel panelPartida = crearPanelPartida();
        panelPartida.setMinimumSize(new Dimension(230, 520));
        columnas.add(panelPartida, gbc);
        add(columnas, BorderLayout.CENTER);
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(14, 0));
        cabecera.setOpaque(false);

        RoundPanel marca = new RoundPanel(CREMA, 24);
        marca.setLayout(new BorderLayout(12, 0));
        marca.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 20));
        JLabel icono = etiqueta("🎲", 34, Font.PLAIN);
        marca.add(icono, BorderLayout.WEST);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel pequeno = etiqueta("RETO DE DADOS", 10, Font.BOLD);
        pequeno.setForeground(new Color(69, 149, 91));
        JLabel titulo = etiqueta("JUEGO DE DADOS", 25, Font.BOLD);
        textos.add(pequeno);
        textos.add(titulo);
        marca.add(textos, BorderLayout.CENTER);
        cabecera.add(marca, BorderLayout.WEST);

        JPanel estado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        estado.setOpaque(false);
        estado.add(tarjetaCabecera("NIVEL", nivel));
        estado.add(tarjetaCabecera("TUS PUNTOS", puntos));

        JButton ayuda = botonClaro("¿Cómo jugar?");
        ayuda.addActionListener(e -> mostrarAyuda());
        estado.add(ayuda);
        if (volverAlMenu != null) {
            JButton volver = botonClaro("← Menú");
            volver.addActionListener(e -> volverAlMenu.run());
            estado.add(volver);
        }
        cabecera.add(estado, BorderLayout.EAST);
        return cabecera;
    }

    private JPanel tarjetaCabecera(String rotulo, JLabel valor) {
        RoundPanel panel = new RoundPanel(new Color(255, 250, 240, 238), 19);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        panel.setPreferredSize(new Dimension(135, 62));
        JLabel nombre = etiqueta(rotulo, 9, Font.BOLD);
        nombre.setForeground(new Color(93, 125, 101));
        panel.add(nombre);
        panel.add(valor);
        return panel;
    }

    private JPanel crearPanelRetos() {
        RoundPanel panel = tarjeta("1", "Elige tu reto");
        panel.setLayout(new BorderLayout(0, 8));
        panel.add(tituloTarjeta("1", "Elige tu reto"), BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.add(subtitulo("PUNTOS PARA JUGAR"));

        JPanel costos = new JPanel(new GridLayout(2, 3, 8, 8));
        costos.setOpaque(false);
        for (int costo : COSTOS) {
            JToggleButton boton = new JToggleButton("★ " + costo);
            estilizarSelector(boton);
            boton.addActionListener(e -> costoSeleccionado = costo);
            grupoCostos.add(boton);
            costos.add(boton);
        }
        contenido.add(costos);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(subtitulo("¿QUÉ CREES QUE SALDRÁ?"));

        JPanel listaRetos = new JPanel();
        listaRetos.setOpaque(false);
        listaRetos.setLayout(new BoxLayout(listaRetos, BoxLayout.Y_AXIS));
        for (DadosGame.Reto reto : DadosGame.Reto.values()) {
            JToggleButton boton = new JToggleButton(reto.getEtiqueta() + "     +" + reto.getMultiplicador() + "×");
            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            estilizarSelector(boton);
            boton.addActionListener(e -> seleccionarReto(reto));
            grupoRetos.add(boton);
            botonesReto.put(reto, boton);
            listaRetos.add(boton);
            listaRetos.add(Box.createVerticalStrut(5));
        }
        JScrollPane scroll = new JScrollPane(listaRetos);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(220, 245));
        contenido.add(scroll);
        contenido.add(Box.createVerticalStrut(8));
        opciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        opciones.setFont(new Font("SansSerif", Font.BOLD, 13));
        opciones.setEnabled(false);
        contenido.add(opciones);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearZonaCentral() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 10));
        contenedor.setOpaque(false);
        contenedor.add(tituloTarjeta("2", "Agita el juego de dados"), BorderLayout.NORTH);

        RoundPanel campo = new RoundPanel(CIELO, 34);
        campo.setLayout(new GridBagLayout());
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 247, 218), 5, true),
                BorderFactory.createEmptyBorder(20, 18, 18, 18)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel pradera = new JPanel(new GridBagLayout());
        pradera.setBackground(VERDE);
        JPanel filaDados = new JPanel(new GridLayout(1, 5, 7, 0));
        filaDados.setOpaque(false);
        for (int i = 0; i < dados.length; i++) {
            dados[i] = new DadoPanel();
            dados[i].setValor(i + 1);
            filaDados.add(dados[i]);
        }
        GridBagConstraints dadosGbc = new GridBagConstraints();
        dadosGbc.weightx = 1;
        dadosGbc.fill = GridBagConstraints.HORIZONTAL;
        dadosGbc.insets = new Insets(40, 12, 20, 12);
        pradera.add(filaDados, dadosGbc);
        campo.add(pradera, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        RoundPanel resultado = new RoundPanel(new Color(255, 250, 225), 20);
        resultado.setLayout(new BoxLayout(resultado, BoxLayout.Y_AXIS));
        resultado.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        resultadoTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultadoDetalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultado.add(resultadoTitulo);
        resultado.add(resultadoDetalle);
        campo.add(resultado, gbc);
        contenedor.add(campo, BorderLayout.CENTER);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        lanzar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lanzar.addActionListener(e -> iniciarLanzamiento());
        JButton reiniciar = botonClaro("Volver a empezar");
        reiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        reiniciar.addActionListener(e -> reiniciarPartida());
        acciones.add(lanzar);
        acciones.add(Box.createVerticalStrut(7));
        acciones.add(reiniciar);
        contenedor.add(acciones, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearPanelPartida() {
        RoundPanel panel = tarjeta("3", "Tu partida");
        panel.setLayout(new BorderLayout(0, 10));
        panel.add(tituloTarjeta("3", "Tu partida"), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 13));
        cuerpo.setOpaque(false);
        JPanel estadisticas = new JPanel(new GridLayout(1, 3, 6, 0));
        estadisticas.setOpaque(false);
        estadisticas.add(tarjetaEstadistica(aciertos, "Aciertos"));
        estadisticas.add(tarjetaEstadistica(racha, "Racha"));
        estadisticas.add(tarjetaEstadistica(record, "Récord"));
        cuerpo.add(estadisticas, BorderLayout.NORTH);

        JPanel historialContenedor = new JPanel(new BorderLayout(0, 8));
        historialContenedor.setOpaque(false);
        historialContenedor.add(subtitulo("ÚLTIMAS TIRADAS"), BorderLayout.NORTH);
        historial.setOpaque(false);
        historial.setLayout(new BoxLayout(historial, BoxLayout.Y_AXIS));
        JLabel vacio = etiqueta("Tus resultados aparecerán aquí.", 11, Font.PLAIN);
        vacio.setForeground(new Color(110, 137, 116));
        historial.add(vacio);
        JScrollPane scroll = new JScrollPane(historial);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        historialContenedor.add(scroll, BorderLayout.CENTER);
        cuerpo.add(historialContenedor, BorderLayout.CENTER);
        panel.add(cuerpo, BorderLayout.CENTER);
        return panel;
    }

    private void seleccionarReto(DadosGame.Reto reto) {
        retoSeleccionado = reto;
        opciones.removeAllItems();
        opciones.setEnabled(true);
        switch (reto) {
            case PARIDAD -> {
                opciones.addItem("PAR");
                opciones.addItem("IMPAR");
            }
            case RANGO -> {
                opciones.addItem("MAYOR");
                opciones.addItem("MENOR");
            }
            case EXACTO -> {
                for (int suma = 5; suma <= 30; suma++) opciones.addItem(String.valueOf(suma));
                opciones.setSelectedItem("18");
            }
            default -> {
                opciones.addItem("Sin configuración adicional");
                opciones.setEnabled(false);
            }
        }
    }

    private void iniciarLanzamiento() {
        if (costoSeleccionado == 0) {
            mostrarError("Selecciona cuántos puntos quieres usar.");
            return;
        }
        if (retoSeleccionado == null) {
            mostrarError("Selecciona un reto.");
            return;
        }
        if (costoSeleccionado > juego.getPuntos()) {
            mostrarError("No tienes puntos suficientes.");
            return;
        }
        if (animacion != null && animacion.isRunning()) return;

        lanzar.setEnabled(false);
        resultadoTitulo.setText("Agitando…");
        resultadoDetalle.setText("¡Los dados están bailando!");
        final long inicio = System.currentTimeMillis();
        animacion = new Timer(75, null);
        animacion.addActionListener(e -> {
            long transcurrido = System.currentTimeMillis() - inicio;
            int[] temporales = juego.lanzarDados();
            for (int i = 0; i < dados.length; i++) {
                dados[i].setValor(temporales[i]);
                dados[i].setDesplazamiento((i % 2 == 0 ? -1 : 1) * (int) (Math.random() * 8));
            }
            if (transcurrido >= 1250) {
                animacion.stop();
                finalizarLanzamiento();
            } else if (transcurrido > 900) {
                animacion.setDelay(140);
            }
        });
        animacion.start();
    }

    private void finalizarLanzamiento() {
        int[] definitivos = juego.lanzarDados();
        for (int i = 0; i < dados.length; i++) {
            dados[i].setValor(definitivos[i]);
            dados[i].setDesplazamiento(0);
        }
        String opcion = opciones.isEnabled()
                ? String.valueOf(opciones.getSelectedItem())
                : "";
        try {
            DadosGame.ResultadoTirada resultado =
                    juego.evaluar(definitivos, costoSeleccionado, retoSeleccionado, opcion);
            resultadoTitulo.setText(resultado.combinacion().getEtiqueta());
            resultadoDetalle.setText("Suma " + resultado.suma() + " · "
                    + (resultado.acierto()
                    ? "¡Sumaste " + resultado.premio() + " puntos!"
                    : "Esta vez no coincidió"));
            agregarHistorial(resultado);
            actualizarDesdeJuego();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } finally {
            lanzar.setEnabled(true);
        }
    }

    private void agregarHistorial(DadosGame.ResultadoTirada tirada) {
        if (historial.getComponentCount() == 1
                && historial.getComponent(0) instanceof JLabel) {
            historial.removeAll();
        }
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 225, 215)));
        StringBuilder valores = new StringBuilder(tirada.acierto() ? "★  " : "🎲  ");
        for (int dado : tirada.dados()) valores.append(dado).append(" · ");
        JLabel detalle = etiqueta(
                "<html>" + valores.substring(0, valores.length() - 3)
                        + "<br><small>" + tirada.combinacion().getEtiqueta() + " · "
                        + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        + "</small></html>", 11, Font.PLAIN);
        JLabel cambio = etiqueta((tirada.cambio() > 0 ? "+" : "") + tirada.cambio(), 11, Font.BOLD);
        cambio.setForeground(tirada.acierto() ? new Color(55, 144, 78) : new Color(190, 86, 67));
        fila.add(detalle, BorderLayout.CENTER);
        fila.add(cambio, BorderLayout.EAST);
        historial.add(fila, 0);
        while (historial.getComponentCount() > 10) {
            historial.remove(historial.getComponentCount() - 1);
        }
        historial.revalidate();
        historial.repaint();
    }

    public void actualizarDesdeJuego() {
        puntos.setText("★ " + juego.getPuntos());
        nivel.setText(juego.getNivel() + " · " + juego.getNombreNivel());
        aciertos.setText(String.valueOf(juego.getAciertos()));
        racha.setText(String.valueOf(juego.getRacha()));
        record.setText(String.valueOf(juego.getRecord()));
    }

    private void reiniciarPartida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Quieres reiniciar los puntos, estadísticas e historial?",
                "Volver a empezar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;
        if (animacion != null) animacion.stop();
        juego.reiniciar();
        grupoCostos.clearSelection();
        grupoRetos.clearSelection();
        costoSeleccionado = 0;
        retoSeleccionado = null;
        opciones.removeAllItems();
        opciones.setEnabled(false);
        historial.removeAll();
        historial.add(etiqueta("Tus resultados aparecerán aquí.", 11, Font.PLAIN));
        resultadoTitulo.setText("Elige un reto");
        resultadoDetalle.setText("Todo listo para jugar");
        lanzar.setEnabled(true);
        historial.revalidate();
        historial.repaint();
    }

    private void mostrarAyuda() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(owner, "Cómo jugar", true);
        dialogo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(CREMA);
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));
        contenido.add(etiqueta("Cómo jugar", 26, Font.BOLD), BorderLayout.NORTH);
        JLabel texto = new JLabel("<html><body style='width:430px'>"
                + "1. Selecciona los puntos que quieres usar.<br><br>"
                + "2. Elige un reto y configúralo cuando sea necesario.<br><br>"
                + "3. Pulsa <b>AGITAR EL JUEGO</b>. Los cinco dados se animarán.<br><br>"
                + "Si aciertas, recibes los puntos multiplicados por el valor del reto. "
                + "La escalera puede ser 1–5 o 2–6. Súper seis requiere cinco seises."
                + "</body></html>");
        texto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        texto.setForeground(TINTA);
        contenido.add(texto, BorderLayout.CENTER);
        JButton cerrar = botonPrincipal("ENTENDIDO");
        cerrar.addActionListener(e -> dialogo.dispose());
        contenido.add(cerrar, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Falta una selección",
                JOptionPane.WARNING_MESSAGE);
    }

    private RoundPanel tarjeta(String numero, String titulo) {
        RoundPanel panel = new RoundPanel(new Color(255, 250, 240, 242), 26);
        panel.setBorder(BorderFactory.createEmptyBorder(17, 17, 17, 17));
        return panel;
    }

    private JPanel tituloTarjeta(String numero, String texto) {
        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        titulo.setOpaque(false);
        JLabel circulo = etiqueta(numero, 16, Font.BOLD);
        circulo.setForeground(Color.WHITE);
        circulo.setOpaque(true);
        circulo.setBackground(new Color(71, 158, 91));
        circulo.setHorizontalAlignment(SwingConstants.CENTER);
        circulo.setPreferredSize(new Dimension(34, 34));
        titulo.add(circulo);
        titulo.add(etiqueta(texto, 20, Font.BOLD));
        return titulo;
    }

    private JPanel tarjetaEstadistica(JLabel valor, String texto) {
        RoundPanel panel = new RoundPanel(new Color(255, 255, 255, 220), 16);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
        valor.setForeground(new Color(224, 126, 44));
        valor.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nombre = etiqueta(texto, 9, Font.PLAIN);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(valor);
        panel.add(nombre);
        return panel;
    }

    private JLabel subtitulo(String texto) {
        JLabel etiqueta = etiqueta(texto, 10, Font.BOLD);
        etiqueta.setForeground(new Color(91, 125, 100));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(4, 0, 7, 0));
        return etiqueta;
    }

    private static JLabel etiqueta(String texto, int tamano, int estilo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", estilo, tamano));
        etiqueta.setForeground(TINTA);
        return etiqueta;
    }

    private void estilizarSelector(JToggleButton boton) {
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(TINTA);
        boton.setBackground(new Color(255, 250, 232));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 214, 188), 1, true),
                BorderFactory.createEmptyBorder(6, 7, 6, 7)));
    }

    private static JButton botonClaro(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(TINTA);
        boton.setBackground(CREMA);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(9, 13, 9, 13)));
        return boton;
    }

    private static JButton botonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setForeground(new Color(89, 65, 24));
        boton.setBackground(AMARILLO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 232, 152), 3, true),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        return boton;
    }

    private static class RoundPanel extends JPanel {
        private final Color color;
        private final int radio;

        RoundPanel(Color color, int radio) {
            this.color = color;
            this.radio = radio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
