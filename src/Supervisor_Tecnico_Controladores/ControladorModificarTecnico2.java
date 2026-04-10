package Supervisor_Tecnico_Controladores;

import Conexion_BD.Conexion;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.ModificarTecnico2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ControladorModificarTecnico2 implements ActionListener, ItemListener {

    private ModificarTecnico2 vista;
    private List<TecnicoItem> listaTecnicos = new ArrayList<>();
    private Integer idTecnicoSeleccionado = null;

    public ControladorModificarTecnico2(ModificarTecnico2 vista) {
        this.vista = vista;

        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
        this.vista.JCBTecnico.addItemListener(this);

        cargarTecnicos();
    }

    private static class TecnicoItem {
        private int idEmpleado;
        private String nombreCompleto;

        public TecnicoItem(int idEmpleado, String nombres, String apellidos) {
            this.idEmpleado = idEmpleado;
            this.nombreCompleto = nombres + " " + apellidos;
        }

        public int getIdEmpleado() {
            return idEmpleado;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }
    }

    private void cargarTecnicos() {
        String sql = "SELECT idEmpleado, nombresEmp, apellidosEmp " +
                     "FROM empleado " +
                     "WHERE rolEmp = 'Tecnico' " +
                     "ORDER BY nombresEmp, apellidosEmp";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            listaTecnicos.clear();
            vista.JCBTecnico.removeAllItems();
            vista.JCBTecnico.addItem("Seleccione técnico");

            while (rs.next()) {
                TecnicoItem item = new TecnicoItem(
                        rs.getInt("idEmpleado"),
                        rs.getString("nombresEmp"),
                        rs.getString("apellidosEmp")
                );
                listaTecnicos.add(item);
                vista.JCBTecnico.addItem(item.getNombreCompleto());
            }

            System.out.println("Técnicos cargados correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar técnicos: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los técnicos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosTecnico(int idEmpleado) {
        String sql = "SELECT nombresEmp, apellidosEmp, telefonoEmp, emailEmp, especialidad " +
                     "FROM empleado " +
                     "WHERE idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vista.JTFNombre.setText(rs.getString("nombresEmp"));
                    vista.JTFApellido.setText(rs.getString("apellidosEmp"));
                    vista.JTFTelefono.setText(rs.getString("telefonoEmp"));
                    vista.JTFCorreo.setText(rs.getString("emailEmp"));

                    String especialidad = rs.getString("especialidad");
                    if (especialidad != null) {
                        vista.JCBEspecialidades.setSelectedItem(especialidad);
                    }

                    System.out.println("Técnico cargado correctamente. ID: " + idEmpleado);
                } else {
                    limpiarCampos();
                    JOptionPane.showMessageDialog(vista,
                            "No se encontró información del técnico.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar datos del técnico: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar los datos del técnico.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        if (idTecnicoSeleccionado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione primero un técnico.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = vista.JTFNombre.getText().trim();
        String apellido = vista.JTFApellido.getText().trim();
        String telefono = vista.JTFTelefono.getText().trim();
        String correo = vista.JTFCorreo.getText().trim();
        String especialidad = vista.JCBEspecialidades.getSelectedItem().toString();

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
                    "Error de formato, asegúrate que el correo contenga '@' y un dominio.",
                    "Correo inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE empleado " +
                     "SET nombresEmp = ?, apellidosEmp = ?, telefonoEmp = ?, emailEmp = ?, especialidad = ? " +
                     "WHERE idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setString(4, correo);
            ps.setString(5, especialidad);
            ps.setInt(6, idTecnicoSeleccionado);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Técnico actualizado correctamente.");
                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo actualizar el técnico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al actualizar técnico: " + ex.getMessage());
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
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == vista.JCBTecnico && e.getStateChange() == ItemEvent.SELECTED) {
            int indice = vista.JCBTecnico.getSelectedIndex();

            if (indice > 0) {
                TecnicoItem tecnico = listaTecnicos.get(indice - 1);
                idTecnicoSeleccionado = tecnico.getIdEmpleado();
                cargarDatosTecnico(idTecnicoSeleccionado);
            } else {
                idTecnicoSeleccionado = null;
                limpiarCampos();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            guardarCambios();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}