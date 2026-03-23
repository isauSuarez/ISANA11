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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReporteCliente implements ActionListener {

    private GenerarReporteporCliente vista;

    public ControladorReporteCliente(GenerarReporteporCliente vista) {
        this.vista = vista;
        this.vista.JBNRegresar.addActionListener(this);
        cargarReporteClientes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporteClientes() {
        String[] columnas = {"Nombre Cliente", "Solicitante", "Empresa", "Modalidad Atencion", "Status", "Correo", "Telefono"};

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

String sql = "SELECT CONCAT(c.nombreC, ' ', c.apellidoC) AS cliente, " +
             "p.nombreEmpresaP AS empresa, " +
             "CONCAT(u.nombresU, ' ', u.apellidosU) AS solicitante, " +
             "t.modalidadAtencionT AS modalidad, " +
             "t.statusT AS status, " +
             "c.correoC, " +
             "c.telefonoC " +
             "FROM cliente c " +
             "JOIN usuario u ON u.idCliente = c.idCliente " +
             "JOIN ticket t ON t.idUsuario = u.idUsuario " +
             "JOIN poliza p ON p.idPoliza = t.idPoliza " +
             "ORDER BY p.idPoliza, t.idTicket";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Conexion correcta. Cargando reporte de clientes...");

            while (rs.next()) {
Object[] fila = {
    rs.getString("cliente"),
    rs.getString("solicitante"),
    rs.getString("empresa"),
    rs.getString("modalidad"),
    rs.getString("status"),
    rs.getString("correoC"),
    rs.getString("telefonoC")
};
                modelo.addRow(fila);
            }

            vista.JTBClientes.setModel(modelo);
            System.out.println("Reporte de clientes cargado correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte de clientes: " + ex.getMessage());
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