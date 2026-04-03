package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.ModificarCliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.Color;

public class ControladorModificarCliente implements ActionListener {

    private ModificarCliente vista;
    private Integer idClienteConsultado = null;

    public ControladorModificarCliente(ModificarCliente vista) {
        this.vista = vista;

        this.vista.JBNBuscar.addActionListener(this);
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNBuscar) {
            buscarCliente();
        } else if (e.getSource() == vista.JBNGuardar) {
            guardarCambios();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }
    
            private void restaurarCamposBusqueda() {
            vista.JTFCorreoBusqueda.setText("Correo registrado");
            vista.JTFCorreoBusqueda.setForeground(Color.GRAY);

            vista.JTFTelefonoBusqueda.setText("Telefono");
            vista.JTFTelefonoBusqueda.setForeground(Color.GRAY);
        }

        private void buscarCliente() {
            String correoBusqueda = vista.JTFCorreoBusqueda.getText().trim();
            String telefonoBusqueda = vista.JTFTelefonoBusqueda.getText().trim();

            if (correoBusqueda.isEmpty() && telefonoBusqueda.isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Ingrese un correo o un teléfono para buscar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql;
            boolean buscarPorCorreoYTelefono = !correoBusqueda.isEmpty() && !telefonoBusqueda.isEmpty();

            if (buscarPorCorreoYTelefono) {
                sql = "SELECT idCliente, nombreC, apellidoC, telefonoC, correoC " +
                      "FROM cliente " +
                      "WHERE correoC = ? OR telefonoC = ?";
            } else if (!correoBusqueda.isEmpty()) {
                sql = "SELECT idCliente, nombreC, apellidoC, telefonoC, correoC " +
                      "FROM cliente " +
                      "WHERE correoC = ?";
            } else {
                sql = "SELECT idCliente, nombreC, apellidoC, telefonoC, correoC " +
                      "FROM cliente " +
                      "WHERE telefonoC = ?";
            }

            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                if (buscarPorCorreoYTelefono) {
                    ps.setString(1, correoBusqueda);
                    ps.setString(2, telefonoBusqueda);
                } else if (!correoBusqueda.isEmpty()) {
                    ps.setString(1, correoBusqueda);
                } else {
                    ps.setString(1, telefonoBusqueda);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idClienteConsultado = rs.getInt("idCliente");

                        vista.JTFnombre.setText(rs.getString("nombreC"));
                        vista.JTFApellido.setText(rs.getString("apellidoC"));
                        vista.JTFTelefono.setText(rs.getString("telefonoC"));
                        vista.JTFCorreo.setText(rs.getString("correoC"));

                        restaurarCamposBusqueda();

                        System.out.println("Cliente encontrado. ID: " + idClienteConsultado);
                    } else {
                        idClienteConsultado = null;
                        limpiarCamposEdicion();

                        restaurarCamposBusqueda();

                        JOptionPane.showMessageDialog(vista,
                                "No se encontró un cliente con esos datos.",
                                "Aviso",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                System.out.println("Error al buscar cliente: " + ex.getMessage());
                JOptionPane.showMessageDialog(vista,
                        "Hubo un error con la BD: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

            private void guardarCambios() {
                if (idClienteConsultado == null) {
                    JOptionPane.showMessageDialog(vista,
                            "Primero busque un cliente válido.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String nombre = vista.JTFnombre.getText().trim();
                String apellido = vista.JTFApellido.getText().trim();
                String telefono = vista.JTFTelefono.getText().trim();
                String correo = vista.JTFCorreo.getText().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                    JOptionPane.showMessageDialog(vista,
                            "Complete todos los campos.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (telefono.length() != 10) {
                    JOptionPane.showMessageDialog(vista,
                            "El teléfono debe tener 10 dígitos.",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!correo.contains("@") || !correo.contains(".")) {
                    JOptionPane.showMessageDialog(vista,
                            "Correo inválido. Asegúrate de incluir '@' y dominio.",
                            "Error de formato",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sqlVerificar = "SELECT idCliente FROM cliente " +
                                      "WHERE (correoC = ? OR telefonoC = ?) AND idCliente <> ?";

                String sqlActualizar = "UPDATE cliente " +
                                       "SET nombreC = ?, apellidoC = ?, telefonoC = ?, correoC = ? " +
                                       "WHERE idCliente = ?";

                try (Connection con = Conexion.getConexion();
                     PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
                     PreparedStatement psActualizar = con.prepareStatement(sqlActualizar)) {

                    psVerificar.setString(1, correo);
                    psVerificar.setString(2, telefono);
                    psVerificar.setInt(3, idClienteConsultado);

                    try (ResultSet rs = psVerificar.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(vista,
                                    "Ya existe otro cliente con ese correo o teléfono.",
                                    "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    psActualizar.setString(1, nombre);
                    psActualizar.setString(2, apellido);
                    psActualizar.setString(3, telefono);
                    psActualizar.setString(4, correo);
                    psActualizar.setInt(5, idClienteConsultado);

                    int filas = psActualizar.executeUpdate();

                    if (filas > 0) {
                        JOptionPane.showMessageDialog(vista,
                                "Cliente actualizado correctamente.");
                        volverAlMenu();
                    } else {
                        JOptionPane.showMessageDialog(vista,
                                "No había cambios por guardar. El cliente conserva la misma información.",
                                "Aviso",
                                JOptionPane.INFORMATION_MESSAGE);
                        volverAlMenu();
                    }

                } catch (SQLException ex) {
                    System.out.println("Error al actualizar cliente: " + ex.getMessage());
                    JOptionPane.showMessageDialog(vista,
                            "Hubo un error con la BD: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

    private void limpiarCamposEdicion() {
        vista.JTFnombre.setText("");
        vista.JTFApellido.setText("");
        vista.JTFTelefonoBusqueda.setText("");
        vista.JTFCorreo.setText("");
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}