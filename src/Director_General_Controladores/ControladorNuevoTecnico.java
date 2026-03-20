package Director_General_Controladores;

import Director_General_Controladores.ControladorMenuDirector;
import Conexion_BD.Conexion;
import Director_General_Frames.Menu_Director_General;
import Director_General_Frames.NuevoTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorNuevoTecnico implements ActionListener {
    
    private NuevoTecnico vista;

    public ControladorNuevoTecnico(NuevoTecnico vista) {
        this.vista = vista;
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            guardarTecnico();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void guardarTecnico() {
        String nombres = vista.JTFNombre.getText().trim();
        String apellidos = vista.JTFApellido.getText().trim();
        String telefono = vista.JTFTelefono.getText().trim();
        String correo = vista.JTFCorreo.getText().trim();
        String password = vista.JTFcontraseña.getText().trim();
        String especialidad = vista.JCBEspecialidades.getSelectedItem().toString().trim();
        String rol = "Tecnico";

        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() 
                || correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Llene todos los campos para registrar al técnico.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sqlVerificar = "SELECT idEmpleado FROM empleado WHERE emailEmp = ?";
        String sqlInsertar = "INSERT INTO empleado "
                + "(nombresEmp, apellidosEmp, rolEmp, especialidad, telefonoEmp, emailEmp, pass) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
             PreparedStatement psInsertar = con.prepareStatement(sqlInsertar)) {

            System.out.println("Conexión correcta. Validando nuevo técnico...");

            psVerificar.setString(1, correo);
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(vista,
                            "Ya existe un empleado registrado con ese correo.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            psInsertar.setString(1, nombres);
            psInsertar.setString(2, apellidos);
            psInsertar.setString(3, rol);
            psInsertar.setString(4, especialidad);
            psInsertar.setString(5, telefono);
            psInsertar.setString(6, correo);
            psInsertar.setString(7, password);

            int filas = psInsertar.executeUpdate();

            if (filas > 0) {
                System.out.println("Técnico registrado correctamente.");
                System.out.println("Nombre: " + nombres + " " + apellidos);
                System.out.println("Especialidad: " + especialidad);

                JOptionPane.showMessageDialog(vista,
                        "Técnico registrado correctamente.");

                limpiarCampos();
                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar el técnico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar técnico: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        vista.JTFNombre.setText("");
        vista.JTFApellido.setText("");
        vista.JTFTelefono.setText("");
        vista.JTFCorreo.setText("");
        vista.JTFcontraseña.setText("");
        vista.JCBEspecialidades.setSelectedIndex(0);
    }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}