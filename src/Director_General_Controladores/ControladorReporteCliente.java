package Director_General_Controladores;

import Conexion_BD.Conexion;
import Director_General_Frames.GenerarReporteporCliente;
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

public class ControladorReporteCliente implements ActionListener {

    private GenerarReporteporCliente vista;

    public ControladorReporteCliente(GenerarReporteporCliente vista) {
        this.vista = vista;

        this.vista.JBNAplicarFiltros2.addActionListener(this);
        this.vista.JBNLimpiarFiltros2.addActionListener(this);
        this.vista.JBNRegresar.addActionListener(this);

        cargarReporteClientes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNAplicarFiltros2) {
            cargarReporteClientes();
        } else if (e.getSource() == vista.JBNLimpiarFiltros2) {
            limpiarFiltros();
            cargarReporteClientes();
        } else if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporteClientes() {
        String[] columnas = {
            "Estado de la Póliza",
            "Nombre Cliente",
            "Empresa",
            "Solicitante de servicio",
            "Tiempo de póliza act.",
            "Correo del Cliente",
            "Teléfono del Cliente"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sqlBase =
            "SELECT " +
            "p.estadoP AS estadoPoliza, " +
            "CONCAT(c.nombreC, ' ', c.apellidoC) AS cliente, " +
            "p.nombreEmpresaP AS empresa, " +
            "CONCAT(u.nombresU, ' ', u.apellidosU) AS solicitante, " +
            "CASE " +
            "   WHEN TIMESTAMPDIFF(YEAR, p.fechaInicioP, CURDATE()) > 0 THEN " +
            "       CONCAT( " +
            "           TIMESTAMPDIFF(YEAR, p.fechaInicioP, CURDATE()), ' año(s) ', " +
            "           MOD(TIMESTAMPDIFF(MONTH, p.fechaInicioP, CURDATE()), 12), ' mes(es)' " +
            "       ) " +
            "   ELSE " +
            "       CONCAT( " +
            "           TIMESTAMPDIFF(MONTH, p.fechaInicioP, CURDATE()), ' mes(es) ', " +
            "           DATEDIFF( " +
            "               CURDATE(), " +
            "               DATE_ADD(p.fechaInicioP, INTERVAL TIMESTAMPDIFF(MONTH, p.fechaInicioP, CURDATE()) MONTH) " +
            "           ), ' día(s)' " +
            "       ) " +
            "END AS tiempoPoliza, " +
            "DATEDIFF(CURDATE(), p.fechaInicioP) AS diasServicio, " +
            "c.correoC AS correoCliente, " +
            "c.telefonoC AS telefonoCliente " +
            "FROM cliente c " +
            "JOIN usuario u ON u.idCliente = c.idCliente " +
            "JOIN poliza p ON p.idPoliza = u.idPoliza ";

        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        StringBuilder order = new StringBuilder();
        ArrayList<String> parametros = new ArrayList<>();

        // -------- FILTRO ESTADO PÓLIZA --------
        ArrayList<String> estados = new ArrayList<>();
        if (vista.JCBActiva2.isSelected()) estados.add("Activa");
        if (vista.JCBInactiva2.isSelected()) estados.add("Inactiva");

        if (!estados.isEmpty()) {
            where.append("AND p.estadoP IN (");
            for (int i = 0; i < estados.size(); i++) {
                where.append("?");
                if (i < estados.size() - 1) {
                    where.append(", ");
                }
                parametros.add(estados.get(i));
            }
            where.append(") ");
        }

        // -------- BÚSQUEDA --------
        String textoBusqueda = vista.JTFBuscador2.getText().trim();
        String tipoBusqueda = vista.JCBSeleccionador2.getSelectedItem().toString();

        if (!textoBusqueda.isEmpty()
                && !tipoBusqueda.equalsIgnoreCase("Tipo de Busqueda")
                && !tipoBusqueda.equalsIgnoreCase("Tipo de Búsqueda")) {

            switch (tipoBusqueda) {
                case "Nombre del Cliente":
                    where.append("AND CONCAT(c.nombreC, ' ', c.apellidoC) LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;

                case "Empresa":
                    where.append("AND p.nombreEmpresaP LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;

                case "Solicitante de servicio":
                    where.append("AND CONCAT(u.nombresU, ' ', u.apellidosU) LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;

                case "Correo del Cliente":
                    where.append("AND c.correoC LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;
            }
        }

        // -------- ORDEN --------
        if (vista.JRBTiempoMayor.isSelected()) {
            order.append("ORDER BY diasServicio DESC ");
        } else if (vista.JRBTiempoMenor.isSelected()) {
            order.append("ORDER BY diasServicio ASC ");
        }

        String sqlFinal = sqlBase + where.toString() + order.toString();
        System.out.println("SQL REPORTE CLIENTE: " + sqlFinal);

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlFinal)) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getString("estadoPoliza"),
                        rs.getString("cliente"),
                        rs.getString("empresa"),
                        rs.getString("solicitante"),
                        rs.getString("tiempoPoliza"),
                        rs.getString("correoCliente"),
                        rs.getString("telefonoCliente")
                    };
                    modelo.addRow(fila);
                }
            }

            vista.JSPTabla2.setModel(modelo);
            System.out.println("Reporte por cliente cargado correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte por cliente: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFiltros() {
        vista.JCBActiva2.setSelected(false);
        vista.JCBInactiva2.setSelected(false);

        vista.JBGFechas2.clearSelection();

        vista.JTFBuscador2.setText("");
        vista.JCBSeleccionador2.setSelectedIndex(0);
       
    }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}