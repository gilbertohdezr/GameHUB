package juegos.adivina;

import interfaces.MiniJuego;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * ============================================================
 * CLASE AdivinaPanel
 * ============================================================
 *
 * Vista del minijuego "Adivina el Número", en formato de
 * panel de control (dashboard) con tres columnas:
 *
 * - Izquierda: dificultad, intentos restantes y tiempo.
 * - Centro: entrada del jugador, mensaje de resultado e
 *   historial de intentos.
 * - Derecha: puntuación, récord y pistas.
 *
 * Esta clase implementa la interfaz {@link MiniJuego} para que
 * el GameHUB pueda administrarla de manera uniforme junto con
 * el resto de los minijuegos.
 *
 * @author Abimael
 * @version 4.0
 * ============================================================
 */
public class AdivinaPanel extends JPanel implements MiniJuego {

    /**
     * Texto de ejemplo mostrado cuando el campo está vacío.
     */
    private static final String PLACEHOLDER = "Escribe un número...";

    /**
     * Color de acento principal (botones, franja superior).
     */
    private static final Color ACENTO = new Color(47, 93, 255);

    /**
     * Color de fondo general de la pantalla.
     */
    private static final Color FONDO_PANTALLA = new Color(240, 242, 247);

    /**
     * Color de texto principal (títulos, valores destacados).
     */
    private static final Color TEXTO_PRINCIPAL = new Color(30, 32, 40);

    /**
     * Color de texto de apoyo (etiquetas secundarias).
     */
    private static final Color TEXTO_APOYO = new Color(130, 134, 145);

    /**
     * Color usado para categorías cercanas al número secreto.
     */
    private static final Color COLOR_CERCA = new Color(30, 150, 90);

    /**
     * Color usado para la categoría "Cerca".
     */
    private static final Color COLOR_TIBIO = new Color(205, 140, 20);

    /**
     * Color usado para la categoría "Lejos".
     */
    private static final Color COLOR_LEJOS = new Color(130, 134, 145);

    /**
     * Color usado cuando el intento fue demasiado alto.
     */
    private static final Color COLOR_ALTO = new Color(210, 60, 60);

    /**
     * Color usado cuando el intento fue demasiado bajo.
     */
    private static final Color COLOR_BAJO = new Color(45, 100, 210);

    /**
     * Fondo de la tarjeta de puntuación.
     */
    private static final Color FONDO_PUNTUACION = new Color(240, 232, 252);

    /**
     * Acento de la tarjeta de puntuación.
     */
    private static final Color ACENTO_PUNTUACION = new Color(130, 70, 210);

    /**
     * Fondo de la tarjeta de récord.
     */
    private static final Color FONDO_RECORD = new Color(224, 247, 233);

    /**
     * Acento de la tarjeta de récord.
     */
    private static final Color ACENTO_RECORD = new Color(30, 150, 90);

    /**
     * Fondo de la tarjeta de pista.
     */
    private static final Color FONDO_PISTA = new Color(221, 235, 255);

    /**
     * Acento de la tarjeta de pista.
     */
    private static final Color ACENTO_PISTA = new Color(40, 100, 220);

    /**
     * Formato de hora usado en el historial de intentos.
     */
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Lógica del juego (modelo).
     */
    private final AdivinaModelo modelo;

    /**
     * Colores asociados a cada fila del historial, en el mismo
     * orden en que se agregan a la tabla.
     */
    private final List<Color> coloresHistorial;

    /**
     * Puntos indicadores del carrusel de pistas.
     */
    private final List<JLabel> puntosPista;

    // --- Columna izquierda ---------------------------------------------

    private JComboBox<Dificultad> comboDificultad;

    private JLabel lblRango;

    private JLabel lblIntentosPermitidos;

    private JLabel lblIntentosContador;

    private IndicadorIntentos indicadorIntentos;

    private JLabel lblTiempo;

    // --- Columna central --------------------------------------------

    private JTextField txtNumero;

    private Border bordeCampoNormal;

    private JButton btnAdivinar;

    private JPanel tarjetaMensaje;

    private JLabel lblTituloMensaje;

    private JLabel lblDescripcionMensaje;

    private DefaultTableModel modeloHistorial;

    private JTable tablaHistorial;

    // --- Columna derecha --------------------------------------------

    private JLabel lblPuntuacion;

    private JLabel lblMejorPuntuacion;

    private JLabel lblRecordIntentos;

    private JLabel lblRecordTiempo;

    private JLabel lblPistaTexto;

    // --- Estado del juego ---------------------------------------------

    private Timer temporizadorJuego;

    private int segundosTranscurridos;

    private int puntuacionTotal;

    private int mejorPuntuacion;

    private int recordIntentos = Integer.MAX_VALUE;

    private int recordSegundos;

    private String[] pistasActuales = {"", "", ""};

    private int indicePista;

    private boolean mostrandoPlaceholder = true;

    /**
     * Constructor.
     */
    public AdivinaPanel() {

        modelo = new AdivinaModelo();

        coloresHistorial = new ArrayList<>();

        puntosPista = new ArrayList<>();

        inicializarComponentes();

        temporizadorJuego = new Timer(1000, e -> {

            segundosTranscurridos++;

            lblTiempo.setText(formatearTiempo(segundosTranscurridos));

        });

        iniciarNuevaRonda();

    }

    /**
     * Construye la estructura general: franja de acento y el
     * contenido principal con las tres columnas.
     */
    private void inicializarComponentes() {

        setLayout(new java.awt.BorderLayout());

        setBackground(FONDO_PANTALLA);

        JPanel franja = new JPanel();

        franja.setBackground(ACENTO);

        franja.setPreferredSize(new Dimension(10, 6));

        add(franja, java.awt.BorderLayout.NORTH);

        JPanel contenido = new JPanel();

        contenido.setOpaque(false);

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        contenido.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        contenido.add(crearEncabezado());

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearFilaColumnas());

        JScrollPane scroll = new JScrollPane(contenido);

        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getViewport().setBackground(FONDO_PANTALLA);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, java.awt.BorderLayout.CENTER);

    }

    /**
     * Crea el bloque de título y subtítulo, alineado a la
     * izquierda como en el resto del tablero.
     *
     * @return Panel del encabezado.
     */
    private JPanel crearEncabezado() {

        JPanel panel = new JPanel(new java.awt.BorderLayout());

        panel.setOpaque(false);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textos = new JPanel();

        textos.setOpaque(false);

        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Adivina el Número");

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));

        lblTitulo.setForeground(TEXTO_PRINCIPAL);

        JLabel lblSubtitulo = new JLabel();

        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lblSubtitulo.setForeground(TEXTO_APOYO);

        lblSubtitulo.setName("subtitulo");

        textos.add(lblTitulo);

        textos.add(Box.createVerticalStrut(3));

        textos.add(lblSubtitulo);

        JButton botonReiniciar = new JButton("🔄  Reiniciar partida");

        botonReiniciar.setFont(new Font("SansSerif", Font.PLAIN, 13));

        botonReiniciar.setForeground(new Color(60, 60, 60));

        botonReiniciar.setBackground(new Color(238, 238, 242));

        botonReiniciar.setOpaque(true);

        botonReiniciar.setBorderPainted(false);

        botonReiniciar.setFocusPainted(false);

        botonReiniciar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        botonReiniciar.addActionListener(e -> reiniciar());

        JPanel envolturaBoton = new JPanel();

        envolturaBoton.setOpaque(false);

        envolturaBoton.setLayout(new java.awt.GridBagLayout());

        envolturaBoton.add(botonReiniciar);

        panel.add(textos, java.awt.BorderLayout.WEST);

        panel.add(envolturaBoton, java.awt.BorderLayout.EAST);

        expandirAncho(panel);

        return panel;

    }

    /**
     * Crea la fila con las tres columnas del tablero, con
     * anchos proporcionales similares al diseño de referencia.
     *
     * @return Panel con las tres columnas.
     */
    private JPanel crearFilaColumnas() {

        JPanel fila = new JPanel(new GridBagLayout());

        fila.setOpaque(false);

        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;

        c.fill = GridBagConstraints.BOTH;

        c.anchor = GridBagConstraints.NORTH;

        c.weighty = 1;

        c.gridx = 0;

        c.weightx = 0.24;

        c.insets = new Insets(0, 0, 0, 12);

        JPanel columnaIzquierda = crearColumnaIzquierda();

        columnaIzquierda.setPreferredSize(new Dimension(148, 10));

        fila.add(columnaIzquierda, c);

        c.gridx = 1;

        c.weightx = 0.50;

        JPanel columnaCentro = crearColumnaCentro();

        columnaCentro.setPreferredSize(new Dimension(300, 10));

        fila.add(columnaCentro, c);

        c.gridx = 2;

        c.weightx = 0.26;

        c.insets = new Insets(0, 0, 0, 0);

        JPanel columnaDerecha = crearColumnaDerecha();

        columnaDerecha.setPreferredSize(new Dimension(160, 10));

        fila.add(columnaDerecha, c);

        return fila;

    }

    // =====================================================================
    // COLUMNA IZQUIERDA: Dificultad, Intentos, Tiempo
    // =====================================================================

    /**
     * Crea la columna izquierda del tablero.
     *
     * @return Panel de la columna izquierda.
     */
    private JPanel crearColumnaIzquierda() {

        JPanel columna = new JPanel();

        columna.setOpaque(false);

        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        columna.add(crearTarjetaDificultad());

        columna.add(Box.createVerticalStrut(12));

        columna.add(crearTarjetaIntentos());

        columna.add(Box.createVerticalStrut(12));

        columna.add(crearTarjetaTiempo());

        return columna;

    }

    /**
     * Crea la tarjeta con el selector de dificultad.
     *
     * @return Tarjeta de dificultad.
     */
    private TarjetaRedondeada crearTarjetaDificultad() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));

        tarjeta.add(crearEtiquetaSeccion("Dificultad"));

        tarjeta.add(Box.createVerticalStrut(8));

        comboDificultad = new JComboBox<>(Dificultad.values());

        comboDificultad.setSelectedItem(Dificultad.NORMAL);

        comboDificultad.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboDificultad.setFont(new Font("SansSerif", Font.PLAIN, 14));

        comboDificultad.addActionListener(e -> cambiarDificultad());

        expandirAncho(comboDificultad);

        tarjeta.add(comboDificultad);

        tarjeta.add(Box.createVerticalStrut(12));

        lblRango = crearTextoApoyo("Rango: 1 - 100");

        lblIntentosPermitidos = crearTextoApoyo("Intentos permitidos: 10");

        tarjeta.add(lblRango);

        tarjeta.add(Box.createVerticalStrut(4));

        tarjeta.add(lblIntentosPermitidos);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea la tarjeta con el contador de intentos y su
     * indicador visual.
     *
     * @return Tarjeta de intentos.
     */
    private TarjetaRedondeada crearTarjetaIntentos() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));

        tarjeta.add(crearEtiquetaSeccion("Intentos"));

        tarjeta.add(Box.createVerticalStrut(8));

        lblIntentosContador = new JLabel("0 de 0");

        lblIntentosContador.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblIntentosContador.setFont(new Font("SansSerif", Font.BOLD, 19));

        lblIntentosContador.setForeground(TEXTO_PRINCIPAL);

        tarjeta.add(lblIntentosContador);

        tarjeta.add(Box.createVerticalStrut(10));

        indicadorIntentos = new IndicadorIntentos();

        indicadorIntentos.setAlignmentX(Component.LEFT_ALIGNMENT);

        expandirAncho(indicadorIntentos);

        tarjeta.add(indicadorIntentos);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea la tarjeta con el cronómetro de la partida.
     *
     * @return Tarjeta de tiempo.
     */
    private TarjetaRedondeada crearTarjetaTiempo() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));

        tarjeta.add(crearEtiquetaSeccion("🕐  Tiempo"));

        tarjeta.add(Box.createVerticalStrut(8));

        lblTiempo = new JLabel("00:00");

        lblTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTiempo.setFont(new Font("SansSerif", Font.BOLD, 19));

        lblTiempo.setForeground(TEXTO_PRINCIPAL);

        tarjeta.add(lblTiempo);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    // =====================================================================
    // COLUMNA CENTRAL: Entrada, mensaje, historial
    // =====================================================================

    /**
     * Crea la columna central del tablero.
     *
     * @return Panel de la columna central.
     */
    private JPanel crearColumnaCentro() {

        JPanel columna = new JPanel();

        columna.setOpaque(false);

        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        columna.add(crearTarjetaEntrada());

        columna.add(Box.createVerticalStrut(12));

        columna.add(crearTarjetaHistorial());

        return columna;

    }

    /**
     * Crea la tarjeta con el campo de entrada, el botón de
     * adivinar y el mensaje de resultado.
     *
     * @return Tarjeta de entrada.
     */
    private TarjetaRedondeada crearTarjetaEntrada() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel filaEntrada = new JPanel();

        filaEntrada.setOpaque(false);

        filaEntrada.setLayout(new java.awt.BorderLayout(10, 0));

        filaEntrada.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNumero = new JTextField();

        txtNumero.setFont(new Font("SansSerif", Font.PLAIN, 15));

        bordeCampoNormal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 224, 230), 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12));

        txtNumero.setBorder(bordeCampoNormal);

        mostrarPlaceholder();

        txtNumero.addActionListener(e -> manejarIntento());

        txtNumero.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                if (mostrandoPlaceholder) {

                    txtNumero.setText("");

                    txtNumero.setForeground(TEXTO_PRINCIPAL);

                    mostrandoPlaceholder = false;

                    actualizarEstadoBoton();

                }

            }

            @Override
            public void focusLost(FocusEvent e) {

                if (txtNumero.getText().trim().isEmpty()) {

                    mostrarPlaceholder();

                    actualizarEstadoBoton();

                }

            }

        });

        txtNumero.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {

                actualizarEstadoBoton();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                actualizarEstadoBoton();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

                actualizarEstadoBoton();

            }

        });

        btnAdivinar = new JButton("Adivinar  ➤");

        btnAdivinar.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnAdivinar.setForeground(Color.WHITE);

        btnAdivinar.setBackground(ACENTO);

        btnAdivinar.setOpaque(true);

        btnAdivinar.setBorderPainted(false);

        btnAdivinar.setFocusPainted(false);

        btnAdivinar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        btnAdivinar.addActionListener(e -> manejarIntento());

        filaEntrada.add(txtNumero, java.awt.BorderLayout.CENTER);

        filaEntrada.add(btnAdivinar, java.awt.BorderLayout.EAST);

        expandirAncho(filaEntrada);

        tarjeta.add(filaEntrada);

        tarjeta.add(Box.createVerticalStrut(16));

        tarjeta.add(crearTarjetaMensajeInterna());

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea el recuadro de color donde se muestra el resultado
     * o pista del último intento.
     *
     * @return Panel del mensaje de resultado.
     */
    private JPanel crearTarjetaMensajeInterna() {

        tarjetaMensaje = new JPanel();

        tarjetaMensaje.setLayout(new BoxLayout(tarjetaMensaje, BoxLayout.Y_AXIS));

        tarjetaMensaje.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        tarjetaMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTituloMensaje = new JLabel("👋  ¡Comienza a jugar!");

        lblTituloMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTituloMensaje.setFont(new Font("SansSerif", Font.BOLD, 15));

        lblDescripcionMensaje = new JLabel(" ");

        lblDescripcionMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDescripcionMensaje.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lblDescripcionMensaje.setForeground(TEXTO_APOYO);

        tarjetaMensaje.add(lblTituloMensaje);

        tarjetaMensaje.add(Box.createVerticalStrut(6));

        tarjetaMensaje.add(lblDescripcionMensaje);

        expandirAncho(tarjetaMensaje);

        return tarjetaMensaje;

    }

    /**
     * Crea la tarjeta con el historial de intentos realizados.
     *
     * @return Tarjeta de historial.
     */
    private TarjetaRedondeada crearTarjetaHistorial() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(Color.WHITE);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        tarjeta.add(crearEtiquetaSeccion("Historial de intentos"));

        tarjeta.add(Box.createVerticalStrut(10));

        String[] columnas = {"#", "Número", "Resultado", "Fecha"};

        modeloHistorial = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {

                return false;

            }

        };

        tablaHistorial = new JTable(modeloHistorial);

        tablaHistorial.setRowHeight(26);

        tablaHistorial.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tablaHistorial.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        tablaHistorial.getTableHeader().setForeground(TEXTO_APOYO);

        tablaHistorial.getTableHeader().setReorderingAllowed(false);

        tablaHistorial.setShowVerticalLines(false);

        tablaHistorial.setGridColor(new Color(238, 238, 242));

        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(30);

        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(70);

        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(90);

        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(80);

        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                    boolean foco, int fila, int columna) {

                Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);

                if (fila < coloresHistorial.size()) {

                    c.setForeground(coloresHistorial.get(fila));

                }

                setFont(getFont().deriveFont(Font.BOLD));

                return c;

            }

        });

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);

        scrollTabla.setPreferredSize(new Dimension(100, 130));

        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(238, 238, 242)));

        expandirAncho(scrollTabla);

        tarjeta.add(scrollTabla);

        tarjeta.add(Box.createVerticalStrut(10));

        JLabel lblNota = crearTextoApoyo("⭐  Usa las pistas y menos intentos para ganar más puntos");

        lblNota.setFont(new Font("SansSerif", Font.ITALIC, 12));

        tarjeta.add(lblNota);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    // =====================================================================
    // COLUMNA DERECHA: Puntuación, Récord, Pista
    // =====================================================================

    /**
     * Crea la columna derecha del tablero.
     *
     * @return Panel de la columna derecha.
     */
    private JPanel crearColumnaDerecha() {

        JPanel columna = new JPanel();

        columna.setOpaque(false);

        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        columna.add(crearTarjetaPuntuacion());

        columna.add(Box.createVerticalStrut(12));

        columna.add(crearTarjetaRecord());

        columna.add(Box.createVerticalStrut(12));

        columna.add(crearTarjetaPista());

        return columna;

    }

    /**
     * Crea la tarjeta de puntuación acumulada.
     *
     * @return Tarjeta de puntuación.
     */
    private TarjetaRedondeada crearTarjetaPuntuacion() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(FONDO_PUNTUACION);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tarjeta.add(crearEncabezadoTarjetaColor("🏆", "Puntuación", ACENTO_PUNTUACION));

        tarjeta.add(Box.createVerticalStrut(8));

        lblPuntuacion = new JLabel("0");

        lblPuntuacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPuntuacion.setFont(new Font("SansSerif", Font.BOLD, 22));

        lblPuntuacion.setForeground(TEXTO_PRINCIPAL);

        tarjeta.add(lblPuntuacion);

        tarjeta.add(Box.createVerticalStrut(4));

        lblMejorPuntuacion = new JLabel("Mejor: 0");

        lblMejorPuntuacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblMejorPuntuacion.setFont(new Font("SansSerif", Font.PLAIN, 12));

        lblMejorPuntuacion.setForeground(ACENTO_PUNTUACION);

        tarjeta.add(lblMejorPuntuacion);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea la tarjeta de récord de la sesión.
     *
     * @return Tarjeta de récord.
     */
    private TarjetaRedondeada crearTarjetaRecord() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(FONDO_RECORD);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tarjeta.add(crearEncabezadoTarjetaColor("⚡", "Récord", ACENTO_RECORD));

        tarjeta.add(Box.createVerticalStrut(8));

        lblRecordIntentos = new JLabel("Sin récord aún");

        lblRecordIntentos.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRecordIntentos.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblRecordIntentos.setForeground(TEXTO_PRINCIPAL);

        tarjeta.add(lblRecordIntentos);

        tarjeta.add(Box.createVerticalStrut(4));

        lblRecordTiempo = new JLabel(" ");

        lblRecordTiempo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRecordTiempo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        lblRecordTiempo.setForeground(ACENTO_RECORD);

        tarjeta.add(lblRecordTiempo);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea la tarjeta de pistas con su carrusel de puntos.
     *
     * @return Tarjeta de pista.
     */
    private TarjetaRedondeada crearTarjetaPista() {

        TarjetaRedondeada tarjeta = new TarjetaRedondeada(FONDO_PISTA);

        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tarjeta.add(crearEncabezadoTarjetaColor("💡", "Pista", ACENTO_PISTA));

        tarjeta.add(Box.createVerticalStrut(10));

        lblPistaTexto = new JLabel("Escribe un número para desbloquear pistas");

        lblPistaTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPistaTexto.setFont(new Font("SansSerif", Font.PLAIN, 14));

        lblPistaTexto.setForeground(TEXTO_PRINCIPAL);

        lblPistaTexto.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(lblPistaTexto);

        tarjeta.add(Box.createVerticalStrut(12));

        JPanel filaDots = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));

        filaDots.setOpaque(false);

        filaDots.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < 3; i++) {

            int indice = i;

            JLabel punto = new JLabel("●");

            punto.setFont(new Font("SansSerif", Font.PLAIN, 12));

            punto.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    indicePista = indice;

                    actualizarPistaVisible();

                }

            });

            puntosPista.add(punto);

            filaDots.add(punto);

        }

        tarjeta.add(filaDots);

        expandirAncho(tarjeta);

        return tarjeta;

    }

    /**
     * Crea el encabezado (icono + título) de una tarjeta de
     * color, centrado como en el diseño de referencia.
     *
     * @param icono       Emoji representativo.
     * @param titulo      Texto del título.
     * @param colorAcento Color del texto del título.
     *
     * @return Panel con el encabezado.
     */
    private JPanel crearEncabezadoTarjetaColor(String icono, String titulo, Color colorAcento) {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblIcono = new JLabel(icono);

        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 26));

        JLabel lblTitulo = new JLabel(titulo);

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblTitulo.setForeground(colorAcento);

        panel.add(lblIcono);

        panel.add(Box.createVerticalStrut(4));

        panel.add(lblTitulo);

        return panel;

    }

    // =====================================================================
    // UTILIDADES DE CONSTRUCCIÓN
    // =====================================================================

    /**
     * Crea una etiqueta de encabezado de sección (bold, oscura),
     * alineada a la izquierda.
     *
     * @param texto Texto de la etiqueta.
     *
     * @return Etiqueta configurada.
     */
    private JLabel crearEtiquetaSeccion(String texto) {

        JLabel lbl = new JLabel(texto);

        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));

        lbl.setForeground(TEXTO_PRINCIPAL);

        return lbl;

    }

    /**
     * Crea una etiqueta de texto de apoyo (gris, alineada a la
     * izquierda).
     *
     * @param texto Texto de la etiqueta.
     *
     * @return Etiqueta configurada.
     */
    private JLabel crearTextoApoyo(String texto) {

        JLabel lbl = new JLabel(texto);

        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lbl.setForeground(TEXTO_APOYO);

        return lbl;

    }

    /**
     * Hace que un componente administrado por un
     * {@code BoxLayout} vertical ocupe todo el ancho disponible
     * de su columna, conservando su alto preferido.
     *
     * @param componente Componente a expandir.
     */
    private void expandirAncho(JComponent componente) {

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension preferido = componente.getPreferredSize();

        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferido.height));

    }

    // =====================================================================
    // LÓGICA DE JUEGO
    // =====================================================================

    /**
     * Atiende el cambio de nivel de dificultad seleccionado en
     * el combo: inicia una nueva partida con la configuración
     * elegida.
     */
    private void cambiarDificultad() {

        Dificultad seleccionada = (Dificultad) comboDificultad.getSelectedItem();

        modelo.reiniciar(seleccionada);

        iniciarNuevaRonda();

    }

    /**
     * Atiende el intento del jugador: valida la entrada, la
     * envía al modelo y actualiza toda la vista según el
     * resultado.
     */
    private void manejarIntento() {

        if (modelo.isJuegoTerminado() || mostrandoPlaceholder) {

            return;

        }

        String texto = txtNumero.getText().trim();

        int numero;

        try {

            numero = Integer.parseInt(texto);

        } catch (NumberFormatException ex) {

            actualizarTarjetaMensaje("⚠️", "Número inválido", "Escribe solo dígitos, sin letras ni símbolos.",
                    new Color(253, 226, 226), COLOR_ALTO);

            marcarErrorEntrada();

            return;

        }

        Dificultad dificultad = modelo.getDificultad();

        if (numero < dificultad.getMin() || numero > dificultad.getMax()) {

            actualizarTarjetaMensaje("⚠️", "Fuera de rango",
                    "El número debe estar entre " + dificultad.getMin() + " y " + dificultad.getMax() + ".",
                    new Color(253, 226, 226), COLOR_ALTO);

            marcarErrorEntrada();

            return;

        }

        int distancia = modelo.getDistanciaAbsoluta(numero);

        int rango = dificultad.getMax() - dificultad.getMin();

        double proporcionLejania = rango == 0 ? 0 : Math.min(1, (double) distancia / rango);

        Resultado resultado = modelo.procesarIntento(numero);

        CategoriaCercania categoria = categorizarCercania(resultado, proporcionLejania);

        registrarFilaHistorial(numero, categoria);

        switch (resultado) {

            case ACIERTO:

                actualizarTarjetaMensaje("🎉", "¡Correcto!",
                        "El número secreto era " + modelo.getNumeroSecreto() + ".",
                        new Color(224, 247, 233), COLOR_CERCA);

                registrarVictoria(dificultad);

                finalizarPartida();

                break;

            case MAYOR:

                actualizarTarjetaMensaje(categoria.icono, categoria.etiqueta,
                        "El número que ingresaste es menor al número secreto. Intenta con uno más alto.",
                        mezclar(categoria.color, Color.WHITE, 0.82), categoria.color);

                break;

            case MENOR:

                actualizarTarjetaMensaje(categoria.icono, categoria.etiqueta,
                        "El número que ingresaste es mayor al número secreto. Intenta con uno más bajo.",
                        mezclar(categoria.color, Color.WHITE, 0.82), categoria.color);

                break;

            case SIN_INTENTOS:

                actualizarTarjetaMensaje("😔", "Sin intentos",
                        "Se acabaron tus intentos. El número era " + modelo.getNumeroSecreto() + ".",
                        new Color(253, 226, 226), COLOR_ALTO);

                finalizarPartida();

                break;

            default:

                break;

        }

        actualizarIntentos();

        actualizarBloqueoDificultad();

        txtNumero.setText("");

        txtNumero.requestFocusInWindow();

    }

    /**
     * Determina la categoría de cercanía de un intento, usada
     * tanto en el mensaje principal como en el historial, para
     * que ambos siempre coincidan.
     *
     * @param resultado         Resultado del intento.
     * @param proporcionLejania Proporción de distancia (0 a 1).
     *
     * @return Categoría de cercanía correspondiente.
     */
    private CategoriaCercania categorizarCercania(Resultado resultado, double proporcionLejania) {

        if (resultado == Resultado.ACIERTO) {

            return new CategoriaCercania("¡Acierto!", COLOR_CERCA, "🎉");

        }

        if (proporcionLejania <= 0.05) {

            return new CategoriaCercania("Muy cerca", COLOR_CERCA, "😊");

        }

        if (proporcionLejania <= 0.15) {

            return new CategoriaCercania("Cerca", COLOR_TIBIO, "🙂");

        }

        if (proporcionLejania <= 0.35) {

            return new CategoriaCercania("Lejos", COLOR_LEJOS, "😐");

        }

        if (resultado == Resultado.MENOR) {

            return new CategoriaCercania("Muy alto", COLOR_ALTO, "🔥");

        }

        return new CategoriaCercania("Muy bajo", COLOR_BAJO, "🧊");

    }

    /**
     * Interpola linealmente entre dos colores.
     *
     * @param origen  Color en t = 0.
     * @param destino Color en t = 1.
     * @param t       Proporción entre 0 y 1.
     *
     * @return Color interpolado.
     */
    private static Color mezclar(Color origen, Color destino, double t) {

        double proporcion = Math.max(0, Math.min(1, t));

        int r = (int) Math.round(origen.getRed() + (destino.getRed() - origen.getRed()) * proporcion);

        int g = (int) Math.round(origen.getGreen() + (destino.getGreen() - origen.getGreen()) * proporcion);

        int b = (int) Math.round(origen.getBlue() + (destino.getBlue() - origen.getBlue()) * proporcion);

        return new Color(r, g, b);

    }

    /**
     * Registra una fila en la tabla de historial para el
     * intento recién realizado, usando la misma categoría de
     * cercanía mostrada en el mensaje principal.
     *
     * @param numero    Número propuesto.
     * @param categoria Categoría de cercanía ya calculada.
     */
    private void registrarFilaHistorial(int numero, CategoriaCercania categoria) {

        int indiceFila = modeloHistorial.getRowCount() + 1;

        String hora = LocalTime.now().format(FORMATO_HORA);

        modeloHistorial.addRow(new Object[]{indiceFila, numero, categoria.etiqueta, hora});

        coloresHistorial.add(categoria.color);

        int ultimaFila = tablaHistorial.getRowCount() - 1;

        tablaHistorial.scrollRectToVisible(tablaHistorial.getCellRect(ultimaFila, 0, true));

    }

    /**
     * Registra la puntuación y el récord al ganar una ronda.
     *
     * @param dificultad Dificultad de la ronda ganada.
     */
    private void registrarVictoria(Dificultad dificultad) {

        int intentosUsados = modelo.getIntentosUsados();

        int puntosRonda = Math.max(50, 1000 - (intentosUsados - 1) * 70 - segundosTranscurridos * 2);

        puntuacionTotal += puntosRonda;

        if (puntosRonda > mejorPuntuacion) {

            mejorPuntuacion = puntosRonda;

        }

        boolean esNuevoRecord = intentosUsados < recordIntentos
                || (intentosUsados == recordIntentos && segundosTranscurridos < recordSegundos);

        if (esNuevoRecord) {

            recordIntentos = intentosUsados;

            recordSegundos = segundosTranscurridos;

        }

        actualizarPuntuacion();

        actualizarRecord();

    }

    /**
     * Detiene el cronómetro y deshabilita la entrada al
     * finalizar la partida.
     */
    private void finalizarPartida() {

        temporizadorJuego.stop();

        txtNumero.setEnabled(false);

        btnAdivinar.setEnabled(false);

    }

    /**
     * Resalta brevemente el campo de entrada (borde rojo y una
     * pequeña sacudida) para indicar una entrada inválida.
     */
    private void marcarErrorEntrada() {

        txtNumero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ALTO, 2, true),
                BorderFactory.createEmptyBorder(8, 11, 8, 11)));

        Timer restaurarBorde = new Timer(650, e -> txtNumero.setBorder(bordeCampoNormal));

        restaurarBorde.setRepeats(false);

        restaurarBorde.start();

        sacudir(txtNumero);

    }

    /**
     * Aplica una pequeña animación de sacudida horizontal a un
     * componente, útil para señalar un error de validación.
     *
     * @param componente Componente a sacudir.
     */
    private void sacudir(JComponent componente) {

        Point posicionOriginal = componente.getLocation();

        int[] desplazamientos = {6, -6, 4, -4, 2, -2, 0};

        int[] paso = {0};

        Timer temporizador = new Timer(30, null);

        temporizador.addActionListener(e -> {

            if (paso[0] >= desplazamientos.length) {

                componente.setLocation(posicionOriginal);

                ((Timer) e.getSource()).stop();

                return;

            }

            componente.setLocation(posicionOriginal.x + desplazamientos[paso[0]], posicionOriginal.y);

            paso[0]++;

        });

        temporizador.start();

    }

    /**
     * Actualiza el recuadro de mensaje con un nuevo icono,
     * título, descripción y colores.
     *
     * @param icono       Emoji representativo.
     * @param titulo      Título breve del resultado.
     * @param descripcion Descripción detallada.
     * @param fondo       Color de fondo del recuadro.
     * @param colorTexto  Color del título.
     */
    private void actualizarTarjetaMensaje(String icono, String titulo, String descripcion, Color fondo,
            Color colorTexto) {

        tarjetaMensaje.setBackground(fondo);

        lblTituloMensaje.setText(icono + "  " + titulo);

        lblTituloMensaje.setForeground(colorTexto);

        lblDescripcionMensaje.setText("<html><div style='text-align:center;width:220px'>"
                + descripcion + "</div></html>");

    }

    /**
     * Actualiza la etiqueta de intentos restantes y el
     * indicador visual asociado.
     */
    private void actualizarIntentos() {

        lblIntentosContador.setText(
                modelo.getIntentosUsados() + " de " + modelo.getDificultad().getMaxIntentos());

        indicadorIntentos.actualizar(modelo.getIntentosUsados(), modelo.getDificultad().getMaxIntentos());

    }

    /**
     * Actualiza las etiquetas de puntuación acumulada y mejor
     * puntaje.
     */
    private void actualizarPuntuacion() {

        lblPuntuacion.setText(String.valueOf(puntuacionTotal));

        lblMejorPuntuacion.setText("Mejor: " + mejorPuntuacion);

    }

    /**
     * Actualiza las etiquetas de récord de intentos y tiempo.
     */
    private void actualizarRecord() {

        if (recordIntentos == Integer.MAX_VALUE) {

            lblRecordIntentos.setText("Sin récord aún");

            lblRecordTiempo.setText(" ");

        } else {

            lblRecordIntentos.setText(recordIntentos + " intentos");

            lblRecordTiempo.setText(formatearTiempo(recordSegundos));

        }

    }

    /**
     * Habilita o deshabilita el selector de dificultad según si
     * hay una partida en curso.
     */
    private void actualizarBloqueoDificultad() {

        boolean partidaEnCurso = modelo.getIntentosUsados() > 0 && !modelo.isJuegoTerminado();

        comboDificultad.setEnabled(!partidaEnCurso);

        comboDificultad.setToolTipText(
                partidaEnCurso ? "Termina la partida actual para cambiar de dificultad" : null);

    }

    /**
     * Habilita o deshabilita el botón "Adivinar" según si hay
     * una entrada real y la partida sigue en curso.
     */
    private void actualizarEstadoBoton() {

        boolean vacio = mostrandoPlaceholder || txtNumero.getText().trim().isEmpty();

        btnAdivinar.setEnabled(!vacio && !modelo.isJuegoTerminado());

    }

    /**
     * Muestra el texto de ejemplo (placeholder) en el campo de
     * entrada.
     */
    private void mostrarPlaceholder() {

        txtNumero.setText(PLACEHOLDER);

        txtNumero.setForeground(TEXTO_APOYO);

        mostrandoPlaceholder = true;

    }

    /**
     * Genera tres pistas sencillas sobre el número secreto.
     *
     * @param secreto    Número secreto de la ronda.
     * @param dificultad Dificultad vigente.
     *
     * @return Arreglo con tres pistas.
     */
    private String[] generarPistas(int secreto, Dificultad dificultad) {

        String pistaParidad = "El número es " + (secreto % 2 == 0 ? "par" : "impar") + ".";

        int puntoMedio = (dificultad.getMin() + dificultad.getMax()) / 2;

        String pistaMitad = secreto > puntoMedio
                ? "El número está en la mitad superior del rango."
                : "El número está en la mitad inferior del rango.";

        int sumaDigitos = 0;

        int valor = Math.abs(secreto);

        while (valor > 0) {

            sumaDigitos += valor % 10;

            valor /= 10;

        }

        String pistaDigitos = "La suma de sus dígitos es " + sumaDigitos + ".";

        return new String[]{pistaParidad, pistaMitad, pistaDigitos};

    }

    /**
     * Muestra la pista vigente según {@link #indicePista} y
     * actualiza el color de los puntos indicadores.
     */
    private void actualizarPistaVisible() {

        lblPistaTexto.setText(pistasActuales[indicePista]);

        for (int i = 0; i < puntosPista.size(); i++) {

            puntosPista.get(i).setForeground(i == indicePista ? ACENTO_PISTA : new Color(255, 255, 255));

        }

    }

    /**
     * Da formato "mm:ss" a una cantidad de segundos.
     *
     * @param segundos Segundos transcurridos.
     *
     * @return Texto con el formato "mm:ss".
     */
    private String formatearTiempo(int segundos) {

        return String.format("%02d:%02d", segundos / 60, segundos % 60);

    }

    /**
     * Prepara toda la vista para el inicio de una nueva ronda,
     * conservando la puntuación y el récord acumulados de la
     * sesión.
     */
    private void iniciarNuevaRonda() {

        Dificultad dificultad = modelo.getDificultad();

        lblRango.setText("Rango: " + dificultad.getMin() + " - " + dificultad.getMax());

        lblIntentosPermitidos.setText("Intentos permitidos: " + dificultad.getMaxIntentos());

        actualizarSubtitulo(dificultad);

        txtNumero.setEnabled(true);

        txtNumero.setBorder(bordeCampoNormal);

        mostrarPlaceholder();

        actualizarTarjetaMensaje("👋", "¡Comienza a jugar!",
                "Escribe un número y presiona Adivinar para recibir tu primera pista.",
                Color.WHITE, TEXTO_PRINCIPAL);

        modeloHistorial.setRowCount(0);

        coloresHistorial.clear();

        segundosTranscurridos = 0;

        lblTiempo.setText(formatearTiempo(0));

        temporizadorJuego.restart();

        pistasActuales = generarPistas(modelo.getNumeroSecreto(), dificultad);

        indicePista = 0;

        actualizarPistaVisible();

        actualizarIntentos();

        actualizarPuntuacion();

        actualizarRecord();

        actualizarEstadoBoton();

        actualizarBloqueoDificultad();

    }

    /**
     * Actualiza el subtítulo del encabezado según el rango de
     * la dificultad vigente.
     *
     * @param dificultad Dificultad vigente.
     */
    private void actualizarSubtitulo(Dificultad dificultad) {

        for (java.awt.Component comp : getComponents()) {

            buscarYActualizarSubtitulo(comp, dificultad);

        }

    }

    /**
     * Recorre recursivamente el árbol de componentes buscando
     * la etiqueta de subtítulo (identificada por su nombre) y
     * actualiza su texto.
     *
     * @param componente Componente a inspeccionar.
     * @param dificultad Dificultad vigente.
     */
    private void buscarYActualizarSubtitulo(java.awt.Component componente, Dificultad dificultad) {

        if (componente instanceof JLabel && "subtitulo".equals(componente.getName())) {

            ((JLabel) componente).setText(
                    "Piensa en un número entre " + dificultad.getMin() + " y " + dificultad.getMax()
                            + " y adivina cuál es");

            return;

        }

        if (componente instanceof java.awt.Container) {

            for (java.awt.Component hijo : ((java.awt.Container) componente).getComponents()) {

                buscarYActualizarSubtitulo(hijo, dificultad);

            }

        }

    }

    /**
     * @return Nombre del minijuego.
     */
    @Override
    public String getNombre() {

        return "Adivina el Número";

    }

    /**
     * @return Panel principal del minijuego.
     */
    @Override
    public JPanel getPanel() {

        return this;

    }

    /**
     * Reinicia el estado del minijuego para comenzar una nueva
     * partida, conservando la puntuación y el récord de la
     * sesión.
     */
    @Override
    public void reiniciar() {

        modelo.reiniciar();

        iniciarNuevaRonda();

    }

    /**
     * ============================================================
     * CLASE INTERNA CategoriaCercania
     * ============================================================
     *
     * Agrupa la etiqueta, el color y el icono correspondientes a
     * una categoría de cercanía (Muy cerca, Cerca, Lejos, Muy
     * alto, Muy bajo, ¡Acierto!), calculada una sola vez y
     * reutilizada tanto en el mensaje principal como en el
     * historial, para que nunca queden en desacuerdo.
     * ============================================================
     */
    private static final class CategoriaCercania {

        private final String etiqueta;

        private final Color color;

        private final String icono;

        CategoriaCercania(String etiqueta, Color color, String icono) {

            this.etiqueta = etiqueta;

            this.color = color;

            this.icono = icono;

        }

    }

    /**
     * ============================================================
     * CLASE INTERNA TarjetaRedondeada
     * ============================================================
     *
     * Panel con esquinas redondeadas y una sombra suave, usado
     * para cada bloque del tablero (blanco o de color).
     * ============================================================
     */
    private static class TarjetaRedondeada extends JPanel {

        private static final int RADIO = 18;

        private final Color colorFondo;

        TarjetaRedondeada(Color colorFondo) {

            this.colorFondo = colorFondo;

            setOpaque(false);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        }

        @Override
        public void setBackground(Color bg) {

            super.setBackground(bg);

            repaint();

        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int ancho = getWidth() - 4;

            int alto = getHeight() - 4;

            for (int i = 4; i >= 1; i--) {

                g2.setColor(new Color(20, 20, 30, 8));

                g2.fill(new RoundRectangle2D.Double(i, i + 2, ancho, alto, RADIO, RADIO));

            }

            Color fondo = getBackground() != null ? getBackground() : colorFondo;

            g2.setColor(fondo);

            g2.fill(new RoundRectangle2D.Double(0, 0, ancho, alto, RADIO, RADIO));

            g2.dispose();

            super.paintComponent(g);

        }

    }

    /**
     * ============================================================
     * CLASE INTERNA IndicadorIntentos
     * ============================================================
     *
     * Componente que muestra los intentos usados mediante una
     * fila de puntos y una barra de progreso, para reforzar la
     * información con dos canales visuales (no solo color).
     * ============================================================
     */
    private static class IndicadorIntentos extends JComponent {

        private int usados;

        private int total = 1;

        IndicadorIntentos() {

            setPreferredSize(new Dimension(220, 30));

            setOpaque(false);

        }

        void actualizar(int usados, int total) {

            this.usados = usados;

            this.total = Math.max(1, total);

            revalidate();

            repaint();

        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro = 10;

            int espacio = 6;

            int x = 0;

            for (int i = 0; i < total; i++) {

                g2.setColor(i < usados ? COLOR_CERCA : new Color(224, 224, 230));

                g2.fillOval(x, 0, diametro, diametro);

                x += diametro + espacio;

            }

            int ancho = getWidth();

            int altoBarra = 8;

            int y = diametro + 8;

            g2.setColor(new Color(228, 228, 232));

            g2.fill(new RoundRectangle2D.Double(0, y, ancho, altoBarra, altoBarra, altoBarra));

            double proporcion = (double) usados / total;

            int anchoRelleno = (int) Math.round(ancho * proporcion);

            if (anchoRelleno > altoBarra) {

                g2.setColor(COLOR_CERCA);

                g2.fill(new RoundRectangle2D.Double(0, y, anchoRelleno, altoBarra, altoBarra, altoBarra));

            }

            g2.dispose();

        }

    }

}
