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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReportePoliza implements ActionListener {

    private GenerarReportePóliza vista;

    public ControladorReportePoliza(GenerarReportePóliza vista) {
        this.vista = vista;
        this.vista.JBNRegresar.addActionListener(this);
        cargarReportePolizas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

private void cargarReportePolizas() {
    String[] columnas = {"Estado", "Plan", "Fecha Inicio", "Fecha Vencimiento", "Nombre Cliente", "Correo"};

    DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    String sql = "SELECT p.estadoP," +
               "pl.nombreP AS tipoPlan," +
               "p.fechaInicioP," +
               "p.fechaVencimientoP," +
               "CONCAT(c.nombreC,' ', c.apellidoC) AS cliente," +
               "c.correoC " +
                "FROM poliza p " +
                "JOIN cliente c ON p.idCliente = c.idCliente " +
                "JOIN plan pl ON pl.idPoliza = p.idPoliza " +
                "ORDER BY p.idPoliza;";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] fila = {
                rs.getString("estadoP"),
                rs.getString("tipoPlan"),
                rs.getDate("fechaInicioP"),
                rs.getDate("fechaVencimientoP"),
                rs.getString("cliente"),
                rs.getString("correoC")
            };
            modelo.addRow(fila);
        }

        vista.JTBPolizas.setModel(modelo);

    } catch (SQLException ex) {
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