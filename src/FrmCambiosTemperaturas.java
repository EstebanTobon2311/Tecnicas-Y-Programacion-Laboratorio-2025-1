import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import datechooser.beans.DateChooserCombo;
import entidades.CambioGrado;
import servicios.CambioGradoServicio;

public class FrmCambiosTemperaturas extends JFrame {

    private JComboBox cmbCiudad;
    private DateChooserCombo dccDesde, dccHasta;
    private JTabbedPane tpCiudades;
    private JPanel pnlGrafica;
    private JPanel pnlEstadisticas;

    private List<String> ciudades;
    private List<CambioGrado> datos;

    private DateChooserCombo dccFechaBusqueda;
    private JLabel lblCiudadMasCaliente, lblCiudadMenosCaliente;

    public FrmCambiosTemperaturas() {
        setTitle("Temperaturas por Ciudad");
        setSize(800, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Barra de herramientas con botones
        JToolBar tb = new JToolBar();

        JButton btnGraficar = new JButton();
        btnGraficar.setIcon(new ImageIcon(getClass().getResource("/iconos/Grafica.png")));
        btnGraficar.setToolTipText("Gráfica Ciudad vs Fecha");
        btnGraficar.addActionListener(this::btnGraficarClick);
        tb.add(btnGraficar);

        JButton btnCalcularEstadisticas = new JButton();
        btnCalcularEstadisticas.setIcon(new ImageIcon(getClass().getResource("/iconos/Datos.png")));
        btnCalcularEstadisticas.setToolTipText("Estadísticas de la Ciudad seleccionada");
        btnCalcularEstadisticas.addActionListener(this::btnCalcularEstadisticasClick);
        tb.add(btnCalcularEstadisticas);

        // Panel principal
        JPanel pnlPrincipal = new JPanel(new BorderLayout());

        // Panel de datos
        JPanel pnlDatosProceso = new JPanel(new GridBagLayout());
        pnlDatosProceso.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes

        // Componentes
        JLabel lblCiudad = new JLabel("Ciudad:");
        cmbCiudad = new JComboBox();

        dccDesde = new DateChooserCombo();
        dccHasta = new DateChooserCombo();

        JLabel lblFechaBusqueda = new JLabel("Fecha:");
        dccFechaBusqueda = new DateChooserCombo();

        JButton btnBuscarExtremos = new JButton("Buscar Extremos");
        btnBuscarExtremos.addActionListener(this::btnBuscarExtremosClick);

        lblCiudadMasCaliente = new JLabel("Más calurosa: ");
        lblCiudadMenosCaliente = new JLabel("Menos calurosa: ");

        // Posicionamiento con GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlDatosProceso.add(lblCiudad, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        pnlDatosProceso.add(cmbCiudad, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        pnlDatosProceso.add(dccDesde, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        pnlDatosProceso.add(dccHasta, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        pnlDatosProceso.add(lblFechaBusqueda, gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        pnlDatosProceso.add(dccFechaBusqueda, gbc);

        gbc.gridx = 6;
        gbc.gridy = 0;
        pnlDatosProceso.add(btnBuscarExtremos, gbc);

        gbc.gridwidth = 7;
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlDatosProceso.add(lblCiudadMasCaliente, gbc);

        gbc.gridy = 2;
        pnlDatosProceso.add(lblCiudadMenosCaliente, gbc);

        // Pestañas (gráfica y estadísticas)
        tpCiudades = new JTabbedPane();
        pnlGrafica = new JPanel(new BorderLayout());
        pnlEstadisticas = new JPanel();
        tpCiudades.addTab("Gráfica", new JScrollPane(pnlGrafica));
        tpCiudades.addTab("Estadísticas", pnlEstadisticas);

        // Composición final
        pnlPrincipal.add(pnlDatosProceso, BorderLayout.NORTH);
        pnlPrincipal.add(tpCiudades, BorderLayout.CENTER);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(pnlPrincipal, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        String nombreArchivo = System.getProperty("user.dir") + "/src/datos/Temperaturas.csv";
        datos = CambioGradoServicio.getDatos(nombreArchivo);
        ciudades = CambioGradoServicio.getCiudades(datos);

        DefaultComboBoxModel dcm = new DefaultComboBoxModel(ciudades.toArray());
        cmbCiudad.setModel(dcm);
    }

    private void btnGraficarClick(ActionEvent evt) {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            tpCiudades.setSelectedIndex(0);

            var datosFiltrados = CambioGradoServicio.filtrar(ciudad, desde, hasta, datos);
            var cambiosPorFecha = CambioGradoServicio.extraer(datosFiltrados);

            var fechas = cambiosPorFecha.getX();
            var cambios = cambiosPorFecha.getY();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = 0; i < fechas.size(); i++) {
                String fechaStr = fechas.get(i).toString();
                dataset.addValue(cambios.get(i), "Cambio", fechaStr);
            }

            JFreeChart graficador = ChartFactory.createBarChart(
                    "Cambio de Temperatura de " + ciudad + " por fecha",
                    "Fecha", "Cambio", dataset);

            ChartPanel pnlGraficador = new ChartPanel(graficador);
            pnlGraficador.setPreferredSize(new Dimension(600, 400));

            pnlGrafica.removeAll();
            pnlGrafica.add(pnlGraficador, BorderLayout.CENTER);
            pnlGrafica.revalidate();
            pnlGrafica.repaint();
        }
    }

    private void btnCalcularEstadisticasClick(ActionEvent evt) {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            tpCiudades.setSelectedIndex(1);
            pnlEstadisticas.removeAll();
            pnlEstadisticas.setLayout(new GridBagLayout());

            int fila = 0;
            var estadisticas = CambioGradoServicio.getEstadisticas(ciudad, desde, hasta, datos);
            for (var entry : estadisticas.entrySet()) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = fila;
                pnlEstadisticas.add(new JLabel(entry.getKey()), gbc);

                gbc.gridx = 1;
                pnlEstadisticas.add(new JLabel(String.format("%.2f", entry.getValue())), gbc);

                fila++;
            }
            pnlEstadisticas.revalidate();
            pnlEstadisticas.repaint();
        }
    }

    private void btnBuscarExtremosClick(ActionEvent evt) {
        if (dccFechaBusqueda.getSelectedDate() == null) {
            lblCiudadMasCaliente.setText("Más calurosa: No seleccionada");
            lblCiudadMenosCaliente.setText("Menos calurosa: No seleccionada");
            return;
        }

        LocalDate fechaSeleccionada = dccFechaBusqueda.getSelectedDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        String masCaliente = CambioGradoServicio.getCiudadMasCalurosaPorFecha(datos, fechaSeleccionada);
        String menosCaliente = CambioGradoServicio.getCiudadMenosCalurosaPorFecha(datos, fechaSeleccionada);

        if ("No disponible".equals(masCaliente) || "No disponible".equals(menosCaliente)) {
            lblCiudadMasCaliente.setText("Más calurosa: No hay registros para esa fecha");
            lblCiudadMenosCaliente.setText("Menos calurosa: No hay registros para esa fecha");
        } else {
            lblCiudadMasCaliente.setText("Más calurosa: " + masCaliente);
            lblCiudadMenosCaliente.setText("Menos calurosa: " + menosCaliente);
        }
    }
}