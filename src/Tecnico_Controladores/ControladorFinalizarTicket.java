package Tecnico_Controladores;

import Conexion_BD.Conexion;
import Tecnico_Frames.FinalizarTicket;
import Tecnico_Frames.MisTickets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;

public class ControladorFinalizarTicket implements ActionListener {

    private FinalizarTicket vista;
    private int idTicket;
    private int idEmpleado;
    private String statusActual = "";

    public ControladorFinalizarTicket(FinalizarTicket vista, int idTicket, int idEmpleado) {
        this.vista = vista;
        this.idTicket = idTicket;
        this.idEmpleado = idEmpleado;

        this.vista.JBNGuardarLS3.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
        this.vista.JCBProceso.addActionListener(this);
        this.vista.JCBFinalizado.addActionListener(this);

        cargarDatosTicket();
        bloquearCamposReferencia();
    }

    private void bloquearCamposReferencia() {
        vista.JTFEmpresa.setEditable(false);
        vista.JTFTipo.setEditable(false);
        vista.JTFConcepto.setEditable(false);
        vista.JTADetalles.setEditable(false);
    }

    private void cargarDatosTicket() {
        String sql = "SELECT "
                + "CONCAT(u.nombresU, ' ', u.apellidosU) AS usuario, "
                + "p.nombreEmpresaP, "
                + "t.modalidadAtencionT, "
                + "t.conceptoT, "
                + "t.descripcionT, "
                + "COALESCE(t.notasTecnico, '') AS notasTecnico, "
                + "t.statusT "
                + "FROM ticket t "
                + "JOIN usuario u ON t.idUsuario = u.idUsuario "
                + "JOIN poliza p ON t.idPoliza = p.idPoliza "
                + "WHERE t.idTicket = ? AND t.idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTicket);
            ps.setInt(2, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String usuario = rs.getString("usuario");
                    String empresa = rs.getString("nombreEmpresaP");

                    vista.JTFEmpresa.setText(usuario + " - " + empresa);
                    vista.JTFTipo.setText(rs.getString("modalidadAtencionT"));
                    vista.JTFConcepto.setText(rs.getString("conceptoT"));
                    vista.JTADetalles.setText(rs.getString("descripcionT"));
                    vista.JTANotas.setText(rs.getString("notasTecnico"));

                    statusActual = rs.getString("statusT");

                    if ("Proceso".equalsIgnoreCase(statusActual) || "En Proceso".equalsIgnoreCase(statusActual)) {
                        vista.JCBProceso.setSelected(true);
                    } else if ("Cerrado".equalsIgnoreCase(statusActual)) {
                        vista.JCBFinalizado.setSelected(true);
                    }

                    System.out.println("Ticket cargado correctamente. ID: " + idTicket);
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "No se encontró el ticket para este técnico.",
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

    private void guardarCambios() {
        String notas = vista.JTANotas.getText().trim();
        String nuevoStatus = statusActual;
        Date fechaCierre = null;

        if (vista.JCBProceso.isSelected() && vista.JCBFinalizado.isSelected()) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione solo una opción: En Proceso o Finalizar Ticket.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (vista.JCBFinalizado.isSelected()) {
            nuevoStatus = "Cerrado";
            fechaCierre = new Date(System.currentTimeMillis());
        } else if (vista.JCBProceso.isSelected()) {
            nuevoStatus = "Proceso";
            fechaCierre = null;
        }

        String sql = "UPDATE ticket "
                + "SET notasTecnico = ?, statusT = ?, fechaCierreT = ? "
                + "WHERE idTicket = ? AND idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, notas);
            ps.setString(2, nuevoStatus);

            if (fechaCierre == null) {
                ps.setNull(3, Types.DATE);
            } else {
                ps.setDate(3, fechaCierre);
            }

            ps.setInt(4, idTicket);
            ps.setInt(5, idEmpleado);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Ticket actualizado correctamente.");
                volverAMisTickets();
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

    private void volverAMisTickets() {
        MisTickets vistaTickets = new MisTickets();
        new ControladorMisTickets(vistaTickets, idEmpleado);
        vistaTickets.setVisible(true);
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardarLS3) {
            guardarCambios();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAMisTickets();
        } else if (e.getSource() == vista.JCBProceso && vista.JCBProceso.isSelected()) {
            vista.JCBFinalizado.setSelected(false);
        } else if (e.getSource() == vista.JCBFinalizado && vista.JCBFinalizado.isSelected()) {
            vista.JCBProceso.setSelected(false);
        }
    }
}