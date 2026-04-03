package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
import Ejecutivo_Vinculacion_Frames.Nuevo_Usuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorNuevoUsuario2 implements ActionListener {

    private NuevoUsuario2 vista;
    private int idCliente;
    private int idPoliza;
    private String textoVisible;

    public ControladorNuevoUsuario2(NuevoUsuario2 vista, int idCliente, int idPoliza, String textoVisible) {
        this.vista = vista;
        this.idCliente = idCliente;
        this.idPoliza = idPoliza;
        this.textoVisible = textoVisible;

        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        System.out.println("Cliente/Empresa seleccionados: " + textoVisible);
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("ID Póliza: " + idPoliza);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            registrarUsuario();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAPaso1();
        }
    }

    private void registrarUsuario() {
        String nombre = vista.JTFNombre.getText().trim();
        String apellido = vista.JTFApelido.getText().trim();
        String locacion = vista.JTFLocacion.getText().trim();
        String correo = vista.JTFCorreo.getText().trim();
        String telefono = vista.JTFTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || locacion.isEmpty()
                || correo.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Complete todos los campos.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (empresaYaTieneTresUsuarios()) {
            JOptionPane.showMessageDialog(vista,
                    "Esta empresa ya tiene el máximo de 3 usuarios autorizados.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (correoYaExiste(correo)) {
            JOptionPane.showMessageDialog(vista,
                    "Ya existe un usuario registrado con ese correo.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO usuario "
                + "(idCliente, idPoliza, nombresU, apellidosU, locacionU, correoU, telefonoU) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexión correcta. Registrando usuario...");

            ps.setInt(1, idCliente);
            ps.setInt(2, idPoliza);
            ps.setString(3, nombre);
            ps.setString(4, apellido);
            ps.setString(5, locacion);
            ps.setString(6, correo);
            ps.setString(7, telefono);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Usuario registrado correctamente.");

                System.out.println("Usuario registrado:");
                System.out.println("Nombre: " + nombre + " " + apellido);
                System.out.println("Locación: " + locacion);
                System.out.println("Correo: " + correo);
                System.out.println("ID Cliente: " + idCliente);
                System.out.println("ID Póliza: " + idPoliza);

                limpiarCampos();
                volverAPaso1();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar el usuario.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar usuario: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean empresaYaTieneTresUsuarios() {
        String sql = "SELECT COUNT(*) AS total FROM usuario WHERE idPoliza = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") >= 3;
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al validar usuarios por póliza: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Error al validar el límite de usuarios.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private boolean correoYaExiste(String correo) {
        String sql = "SELECT idUsuario FROM usuario WHERE correoU = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            System.out.println("Error al validar correo: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Error al validar el correo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private void limpiarCampos() {
        vista.JTFNombre.setText("");
        vista.JTFApelido.setText("");
        vista.JTFLocacion.setText("");
        vista.JTFCorreo.setText("");
        vista.JTFTelefono.setText("");
    }

    private void volverAPaso1() {
        Nuevo_Usuario vistaPaso1 = new Nuevo_Usuario();
        new ControladorNuevoUsuario(vistaPaso1);
        vistaPaso1.setVisible(true);
        vista.dispose();
    }
}