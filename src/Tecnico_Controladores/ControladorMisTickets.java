package Tecnico_Controladores;

import Conexion_BD.Conexion;
import Tecnico_Frames.Menu_Tecnico;
import Tecnico_Frames.MisTickets;
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
import Tecnico_Frames.FinalizarTicket;
import Tecnico_Controladores.ControladorFinalizarTicket;


public class ControladorMisTickets implements ActionListener, ItemListener {

    private MisTickets vista;
    private int idEmpleado;

    private List<TicketItem> ticketsFiltrados = new ArrayList<>();
    private Integer idTicketSeleccionado = null;
    private boolean actualizandoCombos = false;

    public ControladorMisTickets(MisTickets vista, int idEmpleado) {
        this.vista = vista;
        this.idEmpleado = idEmpleado;

        this.vista.JBNSiguienteLS4.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        this.vista.jComboBox1.addItemListener(this);   // Filtros adicionales
        this.vista.JCBTicketsR.addItemListener(this);  // Tickets

        this.vista.JRBPresencial2.addActionListener(this);
        this.vista.JRBRemoto.addActionListener(this);
        this.vista.JRBAsesoria.addActionListener(this);

        this.vista.JRBAsignado.addActionListener(this);
        this.vista.JRBProceso.addActionListener(this);
        this.vista.JRBCerrados.addActionListener(this);

        cargarNombreTecnico();
        cargarFiltrosAdicionales();
        cargarTicketsFiltrados();
    }

    // Constructor adicional por compatibilidad
    public ControladorMisTickets(MisTickets vista) {
        this(vista, 0);
    }

            private static class TicketItem {
                private int idTicket;
                private String concepto;
                private String fechaCreacion;

                public TicketItem(int idTicket, String concepto, String fechaCreacion) {
                    this.idTicket = idTicket;
                    this.concepto = concepto;
                    this.fechaCreacion = fechaCreacion;
                }

                public int getIdTicket() {
                    return idTicket;
                }

                public String getConcepto() {
                    return concepto;
                }

                public String getFechaCreacion() {
                    return fechaCreacion;
                }

                public String getTextoVisible() {
                    return fechaCreacion + " - " + concepto;
                }
            }

    private void cargarNombreTecnico() {
        if (idEmpleado == 0) {
            vista.JLBNombreTec.setText("Sin técnico");
            return;
        }

        String sql = "SELECT nombresEmp, apellidosEmp FROM empleado WHERE idEmpleado = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombresEmp") + " " + rs.getString("apellidosEmp");
                    vista.JLBNombreTec.setText(nombre);
                } else {
                    vista.JLBNombreTec.setText("Sin técnico");
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar nombre del técnico: " + ex.getMessage());
            vista.JLBNombreTec.setText("Sin técnico");
        }
    }

    private void cargarFiltrosAdicionales() {
        actualizandoCombos = true;

        vista.jComboBox1.removeAllItems();
        vista.jComboBox1.addItem("Sin filtro adicional");
        vista.jComboBox1.addItem("Más reciente primero");
        vista.jComboBox1.addItem("Más antiguo primero");

        actualizandoCombos = false;
    }

    private List<String> obtenerModalidadesSeleccionadas() {
        List<String> modalidades = new ArrayList<>();

        if (vista.JRBPresencial2.isSelected()) {
            modalidades.add("Presencial");
        }
        if (vista.JRBRemoto.isSelected()) {
            modalidades.add("Remoto");
        }
        if (vista.JRBAsesoria.isSelected()) {
            modalidades.add("Asesoria");
        }

        return modalidades;
    }

    private List<String> obtenerStatusSeleccionados() {
        List<String> status = new ArrayList<>();

        if (vista.JRBAsignado.isSelected()) {
            status.add("Asignado");
        }
        if (vista.JRBProceso.isSelected()) {
            status.add("Proceso");
        }
        if (vista.JRBCerrados.isSelected()) {
            status.add("Cerrado");
        }

        return status;
    }

    private void cargarTicketsFiltrados() {
        System.out.println("Técnico actual: " + idEmpleado);

        actualizandoCombos = true;
        vista.JCBTicketsR.removeAllItems();
        vista.JCBTicketsR.addItem("Seleccione ticket");
        ticketsFiltrados.clear();
        idTicketSeleccionado = null;
        actualizandoCombos = false;

        if (idEmpleado == 0) {
            JOptionPane.showMessageDialog(vista,
                    "No se detectó un técnico válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> modalidades = obtenerModalidadesSeleccionadas();
        List<String> status = obtenerStatusSeleccionados();

        StringBuilder sql = new StringBuilder(
                "SELECT idTicket, conceptoT, DATE_FORMAT(fechaCreacionT, '%Y-%m-%d') AS fechaCreacionT " +
                "FROM ticket " +
                "WHERE idEmpleado = ? "
        );

        if (!modalidades.isEmpty()) {
            sql.append("AND modalidadAtencionT IN (");
            for (int i = 0; i < modalidades.size(); i++) {
                sql.append("?");
                if (i < modalidades.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
        }

        if (!status.isEmpty()) {
            sql.append("AND statusT IN (");
            for (int i = 0; i < status.size(); i++) {
                sql.append("?");
                if (i < status.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
        }

        String orden = "Sin filtro adicional";
        if (vista.jComboBox1.getSelectedItem() != null) {
            orden = vista.jComboBox1.getSelectedItem().toString();
        }

        if ("Más antiguo primero".equals(orden)) {
            sql.append("ORDER BY fechaCreacionT ASC, idTicket ASC");
        } else {
            sql.append("ORDER BY fechaCreacionT DESC, idTicket DESC");
        }

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int indice = 1;
            ps.setInt(indice++, idEmpleado);

            for (String modalidad : modalidades) {
                ps.setString(indice++, modalidad);
            }

            for (String st : status) {
                ps.setString(indice++, st);
            }

            try (ResultSet rs = ps.executeQuery()) {
                actualizandoCombos = true;

            while (rs.next()) {
                TicketItem item = new TicketItem(
                        rs.getInt("idTicket"),
                        rs.getString("conceptoT"),
                        rs.getString("fechaCreacionT")
                );
                ticketsFiltrados.add(item);
                vista.JCBTicketsR.addItem(item.getTextoVisible());
            }

                actualizandoCombos = false;

                System.out.println("Tickets encontrados: " + ticketsFiltrados.size());
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar tickets filtrados: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar los tickets: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (actualizandoCombos || e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        if (e.getSource() == vista.jComboBox1) {
            cargarTicketsFiltrados();
        }

        if (e.getSource() == vista.JCBTicketsR) {
            int indice = vista.JCBTicketsR.getSelectedIndex();

            if (indice > 0) {
                TicketItem ticket = ticketsFiltrados.get(indice - 1);
                idTicketSeleccionado = ticket.getIdTicket();
                System.out.println("Ticket seleccionado: " + idTicketSeleccionado);
            } else {
                idTicketSeleccionado = null;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteLS4) {
            continuar();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        } else {
            cargarTicketsFiltrados();
        }
    }

            private void continuar() {
                if (idTicketSeleccionado == null) {
                    JOptionPane.showMessageDialog(vista,
                            "Seleccione un ticket válido.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                FinalizarTicket vista2 = new FinalizarTicket();
                new ControladorFinalizarTicket(vista2, idTicketSeleccionado, idEmpleado);
                vista2.setVisible(true);
                vista.dispose();
            }

    private void volverAlMenu() {
        Menu_Tecnico menu = new Menu_Tecnico();
        new ControladorMenuTecnico(menu, idEmpleado);
        menu.setVisible(true);
        vista.dispose();
    }
}