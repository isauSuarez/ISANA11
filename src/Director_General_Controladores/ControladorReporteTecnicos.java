package Director_General_Controladores;

import Conexion_BD.Conexion;
import Director_General_Frames.GenerarReporteporTécnico;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReporteTecnicos implements ActionListener {

    private GenerarReporteporTécnico vista;

    public ControladorReporteTecnicos(GenerarReporteporTécnico vista) {
        this.vista = vista;
        this.vista.JBNRegresar.addActionListener(this);
        cargarReporteTecnicos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporteTecnicos() {
        String[] columnas = {
            "Técnico", "Modalidad", "Fecha Creación", "Fecha Cierre",
            "Status", "Empresa", "Solicitante", "Descripción"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT " +
                     "CONCAT(e.nombresEmp, ' ', e.apellidosEmp) AS tecnico, " +
                     "t.modalidadAtencionT, " +
                     "t.fechaCreacionT, " +
                     "t.fechaCierreT, " +
                     "t.statusT, " +
                     "p.nombreEmpresaP, " +
                     "CONCAT(u.nombresU, ' ', u.apellidosU) AS solicitante, " +
                     "t.descripcionT " +
                     "FROM ticket t " +
                     "JOIN empleado e ON t.idEmpleado = e.idEmpleado " +
                     "JOIN usuario u ON t.idUsuario = u.idUsuario " +
                     "JOIN poliza p ON p.idPoliza = t.idPoliza " +
                     "WHERE e.rolEmp = 'Tecnico' " +
                     "ORDER BY t.fechaCreacionT DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Conexion correcta. Cargando reporte por tecnico...");

            while (rs.next()) {
                Object[] fila = {
                    rs.getString("tecnico"),
                    rs.getString("modalidadAtencionT"),
                    rs.getDate("fechaCreacionT"),
                    rs.getDate("fechaCierreT"),
                    rs.getString("statusT"),
                    rs.getString("nombreEmpresaP"),
                    rs.getString("solicitante"),
                    rs.getString("descripcionT")
                };
                modelo.addRow(fila);
            }

            vista.JTBTecnicos.setModel(modelo);
            System.out.println("Reporte por tecnico cargado correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte por tecnico: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}
