package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.EditarPoliza;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorEditarPoliza implements ActionListener {

    private EditarPoliza vista;

    public ControladorEditarPoliza(EditarPoliza vista) {
        this.vista = vista;

        // Botón Buscar
        this.vista.JBNGuardarCliente.addActionListener(this);
         this.vista.JBNGuardar.addActionListener(this);
         this.vista.JBNCancelar.addActionListener(this);
    }

@Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == vista.JBNGuardarCliente) {
        buscarPoliza();
    } else if (e.getSource() == vista.JBNGuardar) {
        guardarCambios();
    } else if (e.getSource() == vista.JBNCancelar) {
        cancelarEdicion();
    }
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

        String sql = "SELECT nombreEmpresaP, direccionServicioP, telefonoP, correoP " +
                     "FROM poliza " +
                     "WHERE idPoliza = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexión correcta. Buscando póliza...");

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vista.JTFEmpresa.setText(rs.getString("nombreEmpresaP"));
                    vista.JTFDireccion.setText(rs.getString("direccionServicioP"));
                    vista.JTFTelefono.setText(rs.getString("telefonoP"));
                    vista.JTFCorreo.setText(rs.getString("correoP"));

                    System.out.println("Póliza encontrada: " + idPoliza);
                } else {
                    limpiarCampos();
                    JOptionPane.showMessageDialog(vista,
                            "No existe una póliza con ese número.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);

                    System.out.println("No se encontró la póliza: " + idPoliza);
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

    private void guardarCambios() {
    String textoId = vista.JTFNumPoliza.getText().trim();
    String empresa = vista.JTFEmpresa.getText().trim();
    String direccion = vista.JTFDireccion.getText().trim();
    String telefono = vista.JTFTelefono.getText().trim();
    String correo = vista.JTFCorreo.getText().trim();

    if (textoId.isEmpty() || empresa.isEmpty() || direccion.isEmpty() 
            || telefono.isEmpty() || correo.isEmpty()) {
        JOptionPane.showMessageDialog(vista,
                "Complete todos los campos antes de guardar.",
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

    String sql = "UPDATE poliza " +
                 "SET nombreEmpresaP = ?, direccionServicioP = ?, telefonoP = ?, correoP = ? " +
                 "WHERE idPoliza = ?";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        System.out.println("Conexión correcta. Guardando cambios de póliza...");

        ps.setString(1, empresa);
        ps.setString(2, direccion);
        ps.setString(3, telefono);
        ps.setString(4, correo);
        ps.setInt(5, idPoliza);

        int filas = ps.executeUpdate();

        if (filas > 0) {
            JOptionPane.showMessageDialog(vista,
                    "Póliza actualizada correctamente.");
            System.out.println("Póliza actualizada: " + idPoliza);
        } else {
            JOptionPane.showMessageDialog(vista,
                    "No se encontró la póliza para actualizar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }

    } catch (SQLException ex) {
        System.out.println("Error al actualizar póliza: " + ex.getMessage());
        JOptionPane.showMessageDialog(vista,
                "Hubo un error con la BD: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void cancelarEdicion() {
    Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2 menu = 
            new Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2();
    new ControladorMenuEjecutivo(menu);
    menu.setVisible(true);
    vista.dispose();
}
    
    
    private void limpiarCampos() {
        vista.JTFEmpresa.setText("");
        vista.JTFDireccion.setText("");
        vista.JTFTelefono.setText("");
        vista.JTFCorreo.setText("");
    }
}