package Director_General_Controladores;

import Conexion_BD.Conexion;

import Director_General_Frames.GenerarReportePóliza;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReportePoliza implements ActionListener {

    private GenerarReportePóliza vista;

    public ControladorReportePoliza(GenerarReportePóliza vista) {
        this.vista = vista;

        this.vista.JBNAplicarFiltros.addActionListener(this);
        this.vista.JBNLimpiarFiltros.addActionListener(this);
        this.vista.JBNRegresar.addActionListener(this);

        cargarReporte();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNAplicarFiltros) {
            cargarReporte();
        } else if (e.getSource() == vista.JBNLimpiarFiltros) {
            limpiarFiltros();
            cargarReporte();
        } else if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporte() {
        DefaultTableModel modelo = (DefaultTableModel) vista.JTBPolizas.getModel();
        modelo.setRowCount(0);

        String sqlBase = "SELECT p.estadoP, pl.nombreP AS tipoPlan, p.fechaInicioP, p.fechaVencimientoP, "
                + "CONCAT(c.nombreC, ' ', c.apellidoC) AS cliente, c.correoC "
                + "FROM poliza p "
                + "JOIN cliente c ON p.idCliente = c.idCliente "
                + "JOIN plan pl ON pl.idPoliza = p.idPoliza ";

        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        StringBuilder order = new StringBuilder();
        ArrayList<String> parametros = new ArrayList<>();

        // -------- FILTRO ESTADO --------
        ArrayList<String> estados = new ArrayList<>();
        if (vista.JCBActiva.isSelected()) estados.add("Activa");
        if (vista.JCBInactiva.isSelected()) estados.add("Inactiva");

        if (!estados.isEmpty()) {
            where.append("AND p.estadoP IN (");
            for (int i = 0; i < estados.size(); i++) {
                where.append("?");
                if (i < estados.size() - 1) where.append(", ");
                parametros.add(estados.get(i));
            }
            where.append(") ");
        }

        // -------- FILTRO PLAN --------
        ArrayList<String> planes = new ArrayList<>();
        if (vista.JCBEsencial.isSelected()) planes.add("Esencial");
        if (vista.JCBProfesional.isSelected()) planes.add("Profesional");
        if (vista.JCBEmpresarial.isSelected()) planes.add("Empresarial");

        if (!planes.isEmpty()) {
            where.append("AND pl.nombreP IN (");
            for (int i = 0; i < planes.size(); i++) {
                where.append("?");
                if (i < planes.size() - 1) where.append(", ");
                parametros.add(planes.get(i));
            }
            where.append(") ");
        }

        // -------- BÚSQUEDA --------
        String textoBusqueda = vista.JTFBuscador.getText().trim();
        String tipoBusqueda = vista.JCBSeleccionador.getSelectedItem().toString();

        if (!textoBusqueda.isEmpty()) {
            if (tipoBusqueda.equalsIgnoreCase("Nombre")) {
                where.append("AND CONCAT(c.nombreC, ' ', c.apellidoC) LIKE ? ");
                parametros.add("%" + textoBusqueda + "%");
            } else if (tipoBusqueda.equalsIgnoreCase("Correo")) {
                where.append("AND c.correoC LIKE ? ");
                parametros.add("%" + textoBusqueda + "%");
            }
        }

        // -------- ORDEN --------
        if (vista.JRBInicioMasActual.isSelected()) {
            order.append("ORDER BY p.fechaInicioP DESC ");
        } else if (vista.JRBInicioMasAntigua.isSelected()) {
            order.append("ORDER BY p.fechaInicioP ASC ");
        } else if (vista.JRBVenceMasPronta.isSelected()) {
            order.append("ORDER BY p.fechaVencimientoP ASC ");
        } else if (vista.JRBVenceMasLejana.isSelected()) {
            order.append("ORDER BY p.fechaVencimientoP DESC ");
        } else {
            String ordenCliente = vista.JCBOrdenCliente.getSelectedItem().toString();

            if (ordenCliente.equalsIgnoreCase("A-Z")) {
                order.append("ORDER BY cliente ASC ");
            } else if (ordenCliente.equalsIgnoreCase("Z-A")) {
                order.append("ORDER BY cliente DESC ");
            } else {
                order.append("ORDER BY p.idPoliza ASC ");
            }
        }

        String sqlFinal = sqlBase + where.toString() + order.toString();
        System.out.println("SQL FINAL: " + sqlFinal);

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlFinal)) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                        rs.getString("estadoP"),
                        rs.getString("tipoPlan"),
                        rs.getDate("fechaInicioP"),
                        rs.getDate("fechaVencimientoP"),
                        rs.getString("cliente"),
                        rs.getString("correoC")
                    });
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte póliza: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

        private void limpiarFiltros() {
            // CheckBox de estado
            vista.JCBActiva.setSelected(false);
            vista.JCBInactiva.setSelected(false);

            // CheckBox de plan
            vista.JCBEsencial.setSelected(false);
            vista.JCBProfesional.setSelected(false);
            vista.JCBEmpresarial.setSelected(false);

            // ButtonGroup de fechas
            vista.JBGFechas.clearSelection();

            // Búsqueda
            vista.JTFBuscador.setText("");
            vista.JCBSeleccionador.setSelectedIndex(0);

            // Orden cliente
            vista.JCBOrdenCliente.setSelectedIndex(0);
        }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}