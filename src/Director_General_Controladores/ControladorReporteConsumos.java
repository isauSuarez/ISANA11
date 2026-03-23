package Director_General_Controladores;

import Conexion_BD.Conexion;
import Director_General_Frames.ConsultarConsumos;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReporteConsumos implements ActionListener {

    private ConsultarConsumos vista;

    public ControladorReporteConsumos(ConsultarConsumos vista) {
        this.vista = vista;
        this.vista.JBNRegresar.addActionListener(this);
        cargarReporteConsumos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporteConsumos() {
    String[] columnas = {
        "Cliente", "Empresa", "Tipo de Plan",
        "Consumo Pres.", "Restante Pres.",
        "Consumo Rem", "Restante Rem",
        "Consumo Ase", "Restante Ase"
    };

    DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    String sql = "SELECT " +
                 "CONCAT(c.nombreC, ' ', c.apellidoC) AS cliente, " +
                 "p.nombreEmpresaP, " +
                 "pl.nombreP AS tipoPlan, " +
                 "SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'PRESENCIAL' THEN 1 ELSE 0 END) AS consumidoPres, " +
                 "GREATEST(0, pl.maxPres - SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'PRESENCIAL' THEN 1 ELSE 0 END)) AS restantePres, " +
                 "SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'REMOTO' THEN 1 ELSE 0 END) AS consumidoRem, " +
                 "GREATEST(0, pl.maxRem - SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'REMOTO' THEN 1 ELSE 0 END)) AS restanteRem, " +
                 "SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'ASESORIA' THEN 1 ELSE 0 END) AS consumidoAse, " +
                 "GREATEST(0, pl.maxAse - SUM(CASE WHEN t.statusT = 'CERRADO' AND t.modalidadAtencionT = 'ASESORIA' THEN 1 ELSE 0 END)) AS restanteAse " +
                 "FROM poliza p " +
                 "JOIN cliente c ON p.idCliente = c.idCliente " +
                 "JOIN plan pl ON pl.idPoliza = p.idPoliza " +
                 "LEFT JOIN ticket t ON t.idPoliza = p.idPoliza " +
                 "WHERE p.estadoP = 'ACTIVA' " +
                 "GROUP BY p.idPoliza, c.nombreC, c.apellidoC, p.nombreEmpresaP, pl.nombreP, pl.maxPres, pl.maxRem, pl.maxAse";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        System.out.println("Conexion correcta. Cargando reporte de consumos...");

        while (rs.next()) {
            Object[] fila = {
                rs.getString("cliente"),
                rs.getString("nombreEmpresaP"),
                rs.getString("tipoPlan"),
                rs.getInt("consumidoPres"),
                rs.getInt("restantePres"),
                rs.getInt("consumidoRem"),
                rs.getInt("restanteRem"),
                rs.getInt("consumidoAse"),
                rs.getInt("restanteAse")
            };
            modelo.addRow(fila);
        }

        vista.JTBConsumos.setModel(modelo);
        System.out.println("Reporte de consumos cargado correctamente.");

    } catch (SQLException ex) {
        System.out.println("Error al cargar reporte de consumos: " + ex.getMessage());
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