package Director_General_Controladores;

import Conexion_BD.Conexion;
import Director_General_Frames.GenerarReporteporTicket;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReporteTicket implements ActionListener {

    private GenerarReporteporTicket vista;

    private ButtonGroup grupoModalidad;
    private ButtonGroup grupoStatus;

    public ControladorReporteTicket(GenerarReporteporTicket vista) {
        this.vista = vista;

        this.vista.JBNAplicarFiltros.addActionListener(this);
        this.vista.JBNLimpiarFiltros.addActionListener(this);
        this.vista.JBNRegresar.addActionListener(this);

        configurarGrupos();
        cargarEmpresas();
        cargarTecnicos();
        cargarReporteTickets();
    }

    private void configurarGrupos() {
        grupoModalidad = new ButtonGroup();
        grupoModalidad.add(vista.jRadioButton1); // Presencial
        grupoModalidad.add(vista.jRadioButton2); // Remoto
        grupoModalidad.add(vista.jRadioButton3); // Asesoria

        grupoStatus = new ButtonGroup();
        grupoStatus.add(vista.jRadioButton4); // Proceso
        grupoStatus.add(vista.jRadioButton5); // Cerrado
        grupoStatus.add(vista.jRadioButton6); // Asignado
    }

    private void cargarEmpresas() {
        vista.jComboBox1.removeAllItems();
        vista.jComboBox1.addItem("Todas las empresas");

        String sql = "SELECT DISTINCT nombreEmpresaP " +
                     "FROM poliza " +
                     "ORDER BY nombreEmpresaP ASC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vista.jComboBox1.addItem(rs.getString("nombreEmpresaP"));
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar empresas: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar las empresas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

private void cargarTecnicos() {
    vista.jComboBox2.removeAllItems();
    vista.jComboBox2.addItem("Todos los técnicos");
    vista.jComboBox2.addItem("Sin asignar");

    String sql = "SELECT DISTINCT CONCAT(nombresEmp, ' ', apellidosEmp) AS tecnico "
               + "FROM empleado "
               + "WHERE LOWER(TRIM(rolEmp)) = 'tecnico' "
               + "ORDER BY tecnico ASC";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            vista.jComboBox2.addItem(rs.getString("tecnico"));
        }

    } catch (SQLException ex) {
        System.out.println("Error al cargar técnicos: " + ex.getMessage());
        JOptionPane.showMessageDialog(vista,
                "No se pudieron cargar los técnicos.\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

    private void cargarReporteTickets() {
        String[] columnas = {
            "Técnico", "Modalidad", "Fecha creación", "Fecha cierre",
            "Status", "Empresa", "Solicitante", "Descripción"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "COALESCE(CONCAT(e.nombresEmp, ' ', e.apellidosEmp), 'Sin asignar') AS tecnico, " +
            "t.modalidadAtencionT AS modalidad, " +
            "t.fechaCreacionT, " +
            "t.fechaCierreT, " +
            "t.statusT AS status, " +
            "p.nombreEmpresaP AS empresa, " +
            "CONCAT(u.nombresU, ' ', u.apellidosU) AS solicitante, " +
            "t.descripcionT AS descripcion " +
            "FROM ticket t " +
            "JOIN poliza p ON p.idPoliza = t.idPoliza " +
            "JOIN usuario u ON u.idUsuario = t.idUsuario " +
            "LEFT JOIN empleado e ON e.idEmpleado = t.idEmpleado " +
            "WHERE 1=1 "
        );

        String empresaSeleccionada = vista.jComboBox1.getSelectedItem() != null
                ? vista.jComboBox1.getSelectedItem().toString()
                : "Todas las empresas";

        String tecnicoSeleccionado = vista.jComboBox2.getSelectedItem() != null
                ? vista.jComboBox2.getSelectedItem().toString()
                : "Todos los técnicos";

        String textoBusqueda = vista.JTFBuscador4.getText().trim();

        // Filtro por empresa
        if (!empresaSeleccionada.equals("Todas las empresas")) {
            sql.append("AND p.nombreEmpresaP = ? ");
        }

        // Filtro por técnico
        if (!tecnicoSeleccionado.equals("Todos los técnicos")) {
            if (tecnicoSeleccionado.equals("Sin asignar")) {
                sql.append("AND t.idEmpleado IS NULL ");
            } else {
                sql.append("AND CONCAT(e.nombresEmp, ' ', e.apellidosEmp) = ? ");
            }
        }

        // Filtro por modalidad
        String modalidad = obtenerModalidadSeleccionada();
        if (modalidad != null) {
            sql.append("AND t.modalidadAtencionT = ? ");
        }

        // Filtro por status
        String status = obtenerStatusSeleccionado();
        if (status != null) {
            sql.append("AND t.statusT = ? ");
        }

        // Filtro por descripción
        if (!textoBusqueda.isEmpty()) {
            sql.append("AND t.descripcionT LIKE ? ");
        }

        sql.append("ORDER BY t.idTicket DESC");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int indice = 1;

            if (!empresaSeleccionada.equals("Todas las empresas")) {
                ps.setString(indice++, empresaSeleccionada);
            }

            if (!tecnicoSeleccionado.equals("Todos los técnicos")
                    && !tecnicoSeleccionado.equals("Sin asignar")) {
                ps.setString(indice++, tecnicoSeleccionado);
            }

            if (modalidad != null) {
                ps.setString(indice++, modalidad);
            }

            if (status != null) {
                ps.setString(indice++, status);
            }

            if (!textoBusqueda.isEmpty()) {
                ps.setString(indice++, "%" + textoBusqueda + "%");
            }

            System.out.println("SQL REPORTE TICKETS: " + sql);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date fechaCreacion = rs.getDate("fechaCreacionT");
                    Date fechaCierre = rs.getDate("fechaCierreT");

                    Object[] fila = {
                        rs.getString("tecnico"),
                        rs.getString("modalidad"),
                        fechaCreacion != null ? fechaCreacion.toString() : "",
                        fechaCierre != null ? fechaCierre.toString() : "",
                        rs.getString("status"),
                        rs.getString("empresa"),
                        rs.getString("solicitante"),
                        rs.getString("descripcion")
                    };
                    modelo.addRow(fila);
                }
            }

            vista.JBTtickets1.setModel(modelo);

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte de tickets: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerModalidadSeleccionada() {
        if (vista.jRadioButton1.isSelected()) {
            return "Presencial";
        }
        if (vista.jRadioButton2.isSelected()) {
            return "Remoto";
        }
        if (vista.jRadioButton3.isSelected()) {
            return "Asesoria";
        }
        return null;
    }

private String obtenerStatusSeleccionado() {
    if (vista.jRadioButton4.isSelected()) {
        return "Proceso";
    }
    if (vista.jRadioButton5.isSelected()) {
        return "Cerrado";
    }
    if (vista.jRadioButton6.isSelected()) {
        return "Asignado";
    }
    return null;
}

    private void limpiarFiltros() {
        vista.jComboBox1.setSelectedIndex(0);
        vista.jComboBox2.setSelectedIndex(0);
        vista.JTFBuscador4.setText("");

        grupoModalidad.clearSelection();
        grupoStatus.clearSelection();
    }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNAplicarFiltros) {
            cargarReporteTickets();
        } else if (e.getSource() == vista.JBNLimpiarFiltros) {
            limpiarFiltros();
            cargarReporteTickets();
        } else if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }
}