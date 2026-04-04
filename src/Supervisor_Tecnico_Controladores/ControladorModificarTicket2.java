package Supervisor_Tecnico_Controladores;

import Conexion_BD.Conexion;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.ModificarTicket;
import Supervisor_Tecnico_Frames.ModificarTicket2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;

public class ControladorModificarTicket2 implements ActionListener {

    private ModificarTicket2 vista;
    private int idTicket;

    private Integer idEmpleadoActual = null;
    private String statusActual = "";
    private Date fechaCierreActual = null;

    private ButtonGroup grupoModalidad;

    public ControladorModificarTicket2(ModificarTicket2 vista, int idTicket) {
        this.vista = vista;
        this.idTicket = idTicket;

        this.vista.JBNSiguienteJLS3.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        agruparRadios();
        cargarTecnicos();
        cargarDatosTicket();
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

    private void agruparRadios() {
        grupoModalidad = new ButtonGroup();
        grupoModalidad.add(vista.JRBPresencial1);
        grupoModalidad.add(vista.JRBRemoto);
        grupoModalidad.add(vista.JRBAsesoria);
    }

    private void cargarTecnicos() {
        String sql = "SELECT idEmpleado, CONCAT(nombresEmp, ' ', apellidosEmp) AS tecnico " +
                     "FROM empleado " +
                     "WHERE rolEmp = 'Tecnico' " +
                     "ORDER BY nombresEmp, apellidosEmp";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            vista.JCBTecnico.removeAllItems();
            vista.JCBTecnico.addItem("Sin asignar");

            while (rs.next()) {
                TecnicoItem item = new TecnicoItem(
                        rs.getInt("idEmpleado"),
                        rs.getString("tecnico")
                );
                vista.JCBTecnico.addItem(item);
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar técnicos: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los técnicos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosTicket() {
        String sql = "SELECT t.idEmpleado, t.conceptoT, t.descripcionT, t.statusT, " +
                     "t.modalidadAtencionT, t.fechaCierreT, " +
                     "CONCAT(u.nombresU, ' ', u.apellidosU) AS usuario, " +
                     "p.nombreEmpresaP " +
                     "FROM ticket t " +
                     "JOIN usuario u ON t.idUsuario = u.idUsuario " +
                     "JOIN poliza p ON t.idPoliza = p.idPoliza " +
                     "WHERE t.idTicket = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTicket);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idEmpleadoActual = (Integer) rs.getObject("idEmpleado");
                    statusActual = rs.getString("statusT");
                    fechaCierreActual = rs.getDate("fechaCierreT");

                    String usuario = rs.getString("usuario");
                    String empresa = rs.getString("nombreEmpresaP");

                    vista.JTFEmpresa.setText(empresa + " + " + usuario);
                    vista.JTFEmpresa.setEditable(false);

                    vista.JTFConcepto.setText(rs.getString("conceptoT"));
                    vista.JTADescripcion.setText(rs.getString("descripcionT"));

                    marcarModalidad(rs.getString("modalidadAtencionT"));
                    seleccionarTecnicoActual(idEmpleadoActual);

                    vista.JCBFinalizado.setSelected(
                            "Cerrado".equalsIgnoreCase(statusActual) || fechaCierreActual != null
                    );

                    System.out.println("Ticket cargado correctamente. ID: " + idTicket);
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "No se encontró información del ticket.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar ticket: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el ticket: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void marcarModalidad(String modalidad) {
        grupoModalidad.clearSelection();

        if (modalidad == null) {
            return;
        }

        switch (modalidad.toLowerCase()) {
            case "presencial":
                vista.JRBPresencial1.setSelected(true);
                break;
            case "remoto":
                vista.JRBRemoto.setSelected(true);
                break;
            case "asesoria":
                vista.JRBAsesoria.setSelected(true);
                break;
        }
    }

    private void seleccionarTecnicoActual(Integer idEmpleado) {
        if (idEmpleado == null) {
            vista.JCBTecnico.setSelectedIndex(0);
            return;
        }

        for (int i = 1; i < vista.JCBTecnico.getItemCount(); i++) {
            Object item = vista.JCBTecnico.getItemAt(i);
            if (item instanceof TecnicoItem) {
                TecnicoItem tecnico = (TecnicoItem) item;
                if (tecnico.getIdEmpleado() == idEmpleado) {
                    vista.JCBTecnico.setSelectedIndex(i);
                    return;
                }
            }
        }

        vista.JCBTecnico.setSelectedIndex(0);
    }

    private String obtenerModalidadSeleccionada() {
        if (vista.JRBPresencial1.isSelected()) {
            return "Presencial";
        }
        if (vista.JRBRemoto.isSelected()) {
            return "Remoto";
        }
        if (vista.JRBAsesoria.isSelected()) {
            return "Asesoria";
        }
        return null;
    }

    private void guardarCambios() {
        String concepto = vista.JTFConcepto.getText().trim();
        String descripcion = vista.JTADescripcion.getText().trim();
        String modalidad = obtenerModalidadSeleccionada();

        if (concepto.isEmpty() || descripcion.isEmpty() || modalidad == null) {
            JOptionPane.showMessageDialog(vista,
                    "Complete concepto, descripción y modalidad.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer idEmpleadoNuevo = null;
        Object seleccionado = vista.JCBTecnico.getSelectedItem();

        if (seleccionado instanceof TecnicoItem) {
            idEmpleadoNuevo = ((TecnicoItem) seleccionado).getIdEmpleado();
        }

        String nuevoStatus = statusActual;
        Date nuevaFechaCierre = fechaCierreActual;

        if (vista.JCBFinalizado.isSelected()) {
            nuevoStatus = "Cerrado";
            if (nuevaFechaCierre == null) {
                nuevaFechaCierre = new Date(System.currentTimeMillis());
            }
        } else {
            if ("Cerrado".equalsIgnoreCase(statusActual)) {
                nuevoStatus = "Abierto";
                nuevaFechaCierre = null;
            }
        }

        String sql = "UPDATE ticket " +
                     "SET idEmpleado = ?, conceptoT = ?, descripcionT = ?, " +
                     "modalidadAtencionT = ?, statusT = ?, fechaCierreT = ? " +
                     "WHERE idTicket = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (idEmpleadoNuevo == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idEmpleadoNuevo);
            }

            ps.setString(2, concepto);
            ps.setString(3, descripcion);
            ps.setString(4, modalidad);
            ps.setString(5, nuevoStatus);

            if (nuevaFechaCierre == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, nuevaFechaCierre);
            }

            ps.setInt(7, idTicket);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Ticket actualizado correctamente.");
                volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo actualizar el ticket.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al actualizar ticket: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteJLS3) {
            guardarCambios();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAPaso1();
        }
    }

    private void volverAPaso1() {
        ModificarTicket vistaPaso1 = new ModificarTicket();
        new ControladorModificarTicket(vistaPaso1);
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