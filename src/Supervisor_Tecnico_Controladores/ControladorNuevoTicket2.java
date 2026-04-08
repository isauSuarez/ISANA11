package Supervisor_Tecnico_Controladores;

import Conexion_BD.Conexion;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.NuevoTicket;
import Supervisor_Tecnico_Frames.NuevoTicket2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;

public class ControladorNuevoTicket2 implements ActionListener {

    private NuevoTicket2 vista;
    private int idUsuario;
    private int idPoliza;
    private String nombreUsuario;
    private String nombreEmpresa;
    private String modalidadSeleccionada;

    public ControladorNuevoTicket2(NuevoTicket2 vista, int idUsuario, int idPoliza,
                                   String nombreUsuario, String nombreEmpresa,
                                   String modalidadSeleccionada) {
        this.vista = vista;
        this.idUsuario = idUsuario;
        this.idPoliza = idPoliza;
        this.nombreUsuario = nombreUsuario;
        this.nombreEmpresa = nombreEmpresa;
        this.modalidadSeleccionada = modalidadSeleccionada;

        this.vista.JBNguardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        cargarDatosIniciales();
        cargarTecnicos();
    }

    private static class TecnicoItem {
        private int idEmpleado;
        private String nombreCompleto;

        public TecnicoItem(int idEmpleado, String nombreCompleto) {
            this.idEmpleado = idEmpleado;
            this.nombreCompleto = nombreCompleto;
        }

        public int getIdEmpleado() {
            return idEmpleado;
        }

        @Override
        public String toString() {
            return nombreCompleto;
        }
    }

    private void cargarDatosIniciales() {
        vista.JTFEmpresa.setText(nombreUsuario + " - " + nombreEmpresa);
        vista.JTFTipo.setText(modalidadSeleccionada);

        vista.JTFEmpresa.setEnabled(false);
        vista.JTFTipo.setEnabled(false);
    }

    private void cargarTecnicos() {
        String sql = "SELECT idEmpleado, CONCAT(nombresEmp, ' ', apellidosEmp) AS tecnico " +
                     "FROM empleado " +
                     "WHERE rolEmp = 'Tecnico' " +
                     "ORDER BY nombresEmp, apellidosEmp";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            vista.JCBtecnico.removeAllItems();
            vista.JCBtecnico.addItem("Sin asignar");

            while (rs.next()) {
                TecnicoItem item = new TecnicoItem(
                        rs.getInt("idEmpleado"),
                        rs.getString("tecnico")
                );
                vista.JCBtecnico.addItem(item);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNguardar) {
            registrarTicket();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAPaso1();
        }
    }

    private void registrarTicket() {
        String concepto = vista.JTFdescripcion.getText().trim();
        String descripcion = vista.JTADescripcion2.getText().trim();

        if (concepto.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Complete el concepto y la descripción del ticket.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object seleccionado = vista.JCBtecnico.getSelectedItem();
        Integer idEmpleado = null;

        if (seleccionado instanceof TecnicoItem) {
            idEmpleado = ((TecnicoItem) seleccionado).getIdEmpleado();
        }
        
                if (!(seleccionado instanceof TecnicoItem)) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un técnico para asignar el ticket.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO ticket "
                + "(idPoliza, idUsuario, idEmpleado, conceptoT, descripcionT, statusT, modalidadAtencionT, fechaCreacionT) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexión correcta. Registrando ticket...");

            ps.setInt(1, idPoliza);
            ps.setInt(2, idUsuario);

            if (idEmpleado == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, idEmpleado);
            }

            ps.setString(4, concepto);
            ps.setString(5, descripcion);
            ps.setString(6, "Asignado");
            ps.setString(7, modalidadSeleccionada);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Ticket registrado correctamente.");
                System.out.println("Usuario: " + nombreUsuario);
                System.out.println("Empresa: " + nombreEmpresa);
                System.out.println("Modalidad: " + modalidadSeleccionada);
                System.out.println("Concepto: " + concepto);

                JOptionPane.showMessageDialog(vista,
                        "Ticket registrado correctamente.");

                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar el ticket.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar ticket: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void volverAPaso1() {
        NuevoTicket vistaPaso1 = new NuevoTicket();
        new ControladorNuevoTicket(vistaPaso1);
        vistaPaso1.setVisible(true);
        vista.dispose();
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}