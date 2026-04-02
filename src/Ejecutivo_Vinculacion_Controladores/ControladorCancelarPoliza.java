package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.CancelarPolizaN;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorCancelarPoliza implements ActionListener {

    private CancelarPolizaN vista;
    private Integer idPolizaCargada = null;
    private String clienteCargado = "";
    private String empresaCargada = "";
    private String planCargado = "";
    private String estadoCargado = "";

    public ControladorCancelarPoliza(CancelarPolizaN vista) {
        this.vista = vista;

        this.vista.JBNConfirmar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
        this.vista.JTFNumPoliza.addActionListener(this); // Enter en el campo
    }
    
        private void buscarPoliza() {
        String textoId = vista.JTFNumPoliza.getText().trim();

        if (textoId.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Ingrese el número de póliza.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPoliza;

        try {
            idPoliza = Integer.parseInt(textoId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista,
                    "El número de póliza debe ser numérico.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sqlBuscar = "SELECT " +
                "CONCAT(c.nombreC, ' ', c.apellidoC) AS cliente, " +
                "p.nombreEmpresaP, " +
                "COALESCE(pl.nombreP, 'Sin plan') AS planActual, " +
                "p.estadoP " +
                "FROM poliza p " +
                "JOIN cliente c ON p.idCliente = c.idCliente " +
                "LEFT JOIN plan pl ON pl.idPoliza = p.idPoliza " +
                "WHERE p.idPoliza = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement psBuscar = con.prepareStatement(sqlBuscar)) {

            psBuscar.setInt(1, idPoliza);

            try (ResultSet rs = psBuscar.executeQuery()) {
                if (rs.next()) {
                    idPolizaCargada = idPoliza;
                    clienteCargado = rs.getString("cliente");
                    empresaCargada = rs.getString("nombreEmpresaP");
                    planCargado = rs.getString("planActual");
                    estadoCargado = rs.getString("estadoP");

                    vista.JTAinfo.setText(
                            "Dueño: " + clienteCargado + "\n" +
                            "Empresa: " + empresaCargada + "\n" +
                            "Plan actual: " + planCargado + "\n" +
                            "Estado actual: " + estadoCargado
                    );

                    System.out.println("Póliza encontrada: " + idPolizaCargada);
                } else {
                    limpiarBusqueda();
                    JOptionPane.showMessageDialog(vista,
                            "No existe una póliza con ese número.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al buscar póliza: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void confirmarCancelacion() {
    if (idPolizaCargada == null) {
        JOptionPane.showMessageDialog(vista,
                "Primero busque una póliza válida presionando Enter.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if ("Inactiva".equalsIgnoreCase(estadoCargado)) {
        JOptionPane.showMessageDialog(vista,
                "Esta póliza ya se encuentra inactiva.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int opcion = JOptionPane.showConfirmDialog(
            vista,
            "¿Está completamente seguro de cancelar la póliza " + idPolizaCargada + "?",
            "Confirmar Cancelación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (opcion != JOptionPane.YES_OPTION) {
        return;
    }

    String sqlCancelar = "UPDATE poliza SET estadoP = 'Inactiva' WHERE idPoliza = ?";

    try (Connection con = Conexion.getConexion();
         PreparedStatement psCancelar = con.prepareStatement(sqlCancelar)) {

        psCancelar.setInt(1, idPolizaCargada);

        int filas = psCancelar.executeUpdate();

        if (filas > 0) {
            estadoCargado = "Inactiva";

            vista.JTAinfo.setText(
                    "Dueño: " + clienteCargado + "\n" +
                    "Empresa: " + empresaCargada + "\n" +
                    "Plan actual: " + planCargado + "\n" +
                    "Estado actual: " + estadoCargado
            );

            JOptionPane.showMessageDialog(vista,
                    "La póliza fue cancelada correctamente.");

            System.out.println("Póliza cancelada: " + idPolizaCargada);
            volverAlMenu();
        } else {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo cancelar la póliza.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        System.out.println("Error al cancelar póliza: " + ex.getMessage());
        JOptionPane.showMessageDialog(vista,
                "Hubo un error con la BD: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void limpiarBusqueda() {
    idPolizaCargada = null;
    clienteCargado = "";
    empresaCargada = "";
    planCargado = "";
    estadoCargado = "";
    vista.JTAinfo.setText("");
}

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == vista.JTFNumPoliza) {
                buscarPoliza();
            } else if (e.getSource() == vista.JBNConfirmar) {
                confirmarCancelacion();
            } else if (e.getSource() == vista.JBNCancelar) {
                volverAlMenu();
            }
        }

   

    private void volverAlMenu() {
        Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2 menu =
                    new Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2();
            new ControladorMenuEjecutivo(menu);
            menu.setVisible(true);
            vista.dispose();
    }
}