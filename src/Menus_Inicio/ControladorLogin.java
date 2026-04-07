package Menus_Inicio;

/**
 *
 */

import Director_General_Controladores.ControladorMenuDirector;
import Tecnico_Controladores.ControladorMenuTecnico;
import Supervisor_Tecnico_Controladores.ControladorMenuSupervisor;
import Ejecutivo_Vinculacion_Controladores.ControladorMenuEjecutivo;
import Menus_Inicio.Inicio_Sesion;
import Director_General_Frames.Menu_Director_General;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Tecnico_Frames.Menu_Tecnico;
import Conexion_BD.Conexion;
import Menus_Inicio.SesionEmpleado;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ControladorLogin implements ActionListener {
    
    private Inicio_Sesion vistaLogin;

    public ControladorLogin(Inicio_Sesion vistaLogin) {
        this.vistaLogin = vistaLogin;
        this.vistaLogin.btnAcceder.addActionListener(this);
    }

        public class SesionEmpleado {
    
    private static int idEmpleado;
    private static String nombreCompleto;
    private static String rol;

    public static void iniciarSesion(int id, String nombres, String apellidos, String rolEmp) {
        idEmpleado = id;
        nombreCompleto = nombres + " " + apellidos;
        rol = rolEmp;
    }

    public static int getIdEmpleado() {
        return idEmpleado;
    }

    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    public static String getRol() {
        return rol;
    }

    public static void cerrarSesion() {
        idEmpleado = 0;
        nombreCompleto = null;
        rol = null;
    }
}
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaLogin.btnAcceder) {
            validarAcceso();
        }
    }

    private void validarAcceso() {
        String correo = vistaLogin.txtUsuario.getText().trim();
        String password = new String(vistaLogin.pwdContrasena.getPassword()).trim();

        if (correo.isEmpty() || correo.equals("Usuario") || password.isEmpty()) {
            JOptionPane.showMessageDialog(vistaLogin, "Favor de llenar todos los campos.");
            return;
        }

        String sql = "SELECT idEmpleado, nombresEmp, apellidosEmp, rolEmp "
                   + "FROM empleado "
                   + "WHERE emailEmp = ? AND `pass` = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexion correcta. Validando acceso...");

            ps.setString(1, correo);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                         int idEmpleado = rs.getInt("idEmpleado");
                    String nombre = rs.getString("nombresEmp");
                    String apellido = rs.getString("apellidosEmp");
                    String rol = rs.getString("rolEmp");
                          
                        SesionEmpleado.iniciarSesion(idEmpleado, nombre, apellido, rol);

                    System.out.println("Login correcto.");
                    System.out.println("Empleado: " + nombre + apellido + " | Rol: " + rol);
                    System.out.println("Sesion guardada. ID actual: " + SesionEmpleado.getIdEmpleado());

                    JOptionPane.showMessageDialog(vistaLogin, "¡Bienvenido, " + nombre + "!");

                    switch (rol.toLowerCase()) {
                        case "director":
                            Menu_Director_General menuDirector = new Menu_Director_General();
                            new ControladorMenuDirector(menuDirector);
                            menuDirector.setVisible(true);
                            break;

                        case "supervisor":
                            MenuSupervisorTecnico menuSupervisor = new MenuSupervisorTecnico();
                            new ControladorMenuSupervisor(menuSupervisor);
                            menuSupervisor.setVisible(true);
                            break;

                        case "ejecutivo":
                            MenuEjecutivoVinculacion2 menuEjecutivo = new MenuEjecutivoVinculacion2();
                            new ControladorMenuEjecutivo(menuEjecutivo);
                            menuEjecutivo.setVisible(true);
                            break;

                        case "tecnico":
                            Menu_Tecnico menuTec = new Menu_Tecnico();
                            new ControladorMenuTecnico(menuTec, idEmpleado);
                            menuTec.setVisible(true);
                            break;

                        default:
                            System.out.println("Rol no reconocido: " + rol);
                            JOptionPane.showMessageDialog(vistaLogin, "Rol no reconocido.");
                            return;
                    }

                    vistaLogin.dispose();

                } else {
                    System.out.println("Login incorrecto: usuario o contraseña inválidos.");
                    JOptionPane.showMessageDialog(vistaLogin, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                    vistaLogin.pwdContrasena.setText("");
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error con la base de datos: " + ex.getMessage());
            JOptionPane.showMessageDialog(vistaLogin, "Hubo un error con la DB: " + ex.getMessage());
        }
    }
}