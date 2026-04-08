package Tecnico_Controladores;

import Conexion_BD.Conexion;
import Tecnico_Frames.CambioPass;
import Tecnico_Frames.Menu_Tecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorCambioPass implements ActionListener {

    private CambioPass vista;
    private int idEmpleado;

    public ControladorCambioPass(CambioPass vista, int idEmpleado) {
        this.vista = vista;
        this.idEmpleado = idEmpleado;

        this.vista.JBNConfirmar.addActionListener(this);
        this.vista.JBNcancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNConfirmar) {
            cambiarPassword();
        } else if (e.getSource() == vista.JBNcancelar) {
            volverAlMenu();
        }
    }

    private void cambiarPassword() {
        String actual = new String(vista.JPWPassActual.getPassword()).trim();
        String nueva = new String(vista.JPWCambio.getPassword()).trim();
        String confirmar = new String(vista.JPWConfirmacion.getPassword()).trim();

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Complete todos los campos.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarPasswordActual(actual)) {
            JOptionPane.showMessageDialog(vista,
                    "La contraseña actual es incorrecta.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nueva.equals(confirmar)) {
            JOptionPane.showMessageDialog(vista,
                    "La nueva contraseña y su confirmación no coinciden.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nueva.length() < 6) {
            JOptionPane.showMessageDialog(vista,
                    "La nueva contraseña debe tener al menos 6 caracteres.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nueva.equals(actual)) {
            JOptionPane.showMessageDialog(vista,
                    "La nueva contraseña no puede ser igual a la actual.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE empleado SET pass = ? WHERE idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nueva);
            ps.setInt(2, idEmpleado);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Contraseña actualizada correctamente.");
                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo actualizar la contraseña.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al cambiar contraseña: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarPasswordActual(String actual) {
        String sql = "SELECT idEmpleado FROM empleado WHERE idEmpleado = ? AND pass = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);
            ps.setString(2, actual);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            System.out.println("Error al validar contraseña actual: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudo validar la contraseña actual.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void volverAlMenu() {
        Menu_Tecnico menu = new Menu_Tecnico();
        new ControladorMenuTecnico(menu, idEmpleado);
        menu.setVisible(true);
        vista.dispose();
    }
}