package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.NuevoCliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorNuevoCliente implements ActionListener {

    private NuevoCliente vista;

    public ControladorNuevoCliente(NuevoCliente vista) {
        this.vista = vista;
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            guardarCliente();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void guardarCliente() {
        String nombre = vista.JTFNombre.getText().trim();
        String apellido = vista.JTFApellido.getText().trim();
        String telefono = vista.JTFTelefono.getText().trim();
        String correo = vista.JTFCorreo.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Llene todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sqlVerificar = "SELECT idCliente FROM cliente WHERE correoC = ?";
        String sqlInsertar = "INSERT INTO cliente (nombreC, apellidoC, telefonoC, correoC, fechaAltaC) VALUES (?, ?, ?, ?, CURDATE())";

        try (Connection con = Conexion.getConexion();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
             PreparedStatement psInsertar = con.prepareStatement(sqlInsertar)) {

            System.out.println("Conexión correcta. Validando nuevo cliente...");

            psVerificar.setString(1, correo);
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(vista, "Ya existe un cliente con ese correo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            psInsertar.setString(1, nombre);
            psInsertar.setString(2, apellido);
            psInsertar.setString(3, telefono);
            psInsertar.setString(4, correo);

            int filas = psInsertar.executeUpdate();

            if (filas > 0) {
                System.out.println("Cliente registrado correctamente: " + nombre);
                JOptionPane.showMessageDialog(vista, "Cliente registrado correctamente.");
                limpiarCampos();
                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo registrar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar cliente: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista, "Hubo un error con la BD: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        vista.JTFNombre.setText("");
        vista.JTFApellido.setText("");
        vista.JTFTelefono.setText("");
        vista.JTFCorreo.setText("");
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}