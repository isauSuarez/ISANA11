package Supervisor_Tecnico_Controladores;

import Conexion_BD.Conexion;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.ModificarTicket;
import Supervisor_Tecnico_Frames.ModificarTicket2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControladorModificarTicket implements ActionListener, ItemListener {

    private ModificarTicket vista;

    private List<UsuarioItem> listaUsuarios = new ArrayList<>();
    private List<UsuarioItem> usuariosFiltrados = new ArrayList<>();

    private List<EmpresaItem> listaEmpresas = new ArrayList<>();
    private List<EmpresaItem> empresasFiltradas = new ArrayList<>();

    private List<TicketItem> ticketsActuales = new ArrayList<>();

    private Integer idUsuarioSeleccionado = null;
    private Integer idPolizaSeleccionada = null;
    private Integer idTicketSeleccionado = null;

    private boolean actualizandoCombos = false;

    public ControladorModificarTicket(ModificarTicket vista) {
        this.vista = vista;

        this.vista.JBNSiguienteLS3.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        this.vista.JCBUsuario.addItemListener(this);
        this.vista.JCBEmpresa.addItemListener(this);
        this.vista.JCBTicket.addItemListener(this);

        prepararCombos();
        cargarDatosBase();
        activarFiltros();
    }

    private static class UsuarioItem {
        private int idUsuario;
        private int idPoliza;
        private String nombreCompleto;
        private String empresa;

        public UsuarioItem(int idUsuario, int idPoliza, String nombres, String apellidos, String empresa) {
            this.idUsuario = idUsuario;
            this.idPoliza = idPoliza;
            this.nombreCompleto = nombres + " " + apellidos;
            this.empresa = empresa;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public int getIdPoliza() {
            return idPoliza;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getEmpresa() {
            return empresa;
        }
    }

    private static class EmpresaItem {
        private int idPoliza;
        private String nombreEmpresa;

        public EmpresaItem(int idPoliza, String nombreEmpresa) {
            this.idPoliza = idPoliza;
            this.nombreEmpresa = nombreEmpresa;
        }

        public int getIdPoliza() {
            return idPoliza;
        }

        public String getNombreEmpresa() {
            return nombreEmpresa;
        }
    }

    private static class TicketItem {
        private int idTicket;
        private String concepto;

        public TicketItem(int idTicket, String concepto) {
            this.idTicket = idTicket;
            this.concepto = concepto;
        }

        public int getIdTicket() {
            return idTicket;
        }

        public String getConcepto() {
            return concepto;
        }
    }

    private void prepararCombos() {
        actualizandoCombos = true;

        vista.JCBUsuario.removeAllItems();
        vista.JCBUsuario.addItem("Seleccione usuario");

        vista.JCBEmpresa.removeAllItems();
        vista.JCBEmpresa.addItem("Seleccione empresa");

        vista.JCBTicket.removeAllItems();
        vista.JCBTicket.addItem("Seleccione ticket");

        actualizandoCombos = false;
    }

    private void cargarDatosBase() {
        String sql = "SELECT u.idUsuario, u.idPoliza, u.nombresU, u.apellidosU, p.nombreEmpresaP " +
                     "FROM usuario u " +
                     "JOIN poliza p ON u.idPoliza = p.idPoliza " +
                     "WHERE p.estadoP = 'Activa' " +
                     "ORDER BY u.nombresU, u.apellidosU, p.nombreEmpresaP";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            listaUsuarios.clear();

            while (rs.next()) {
                listaUsuarios.add(new UsuarioItem(
                        rs.getInt("idUsuario"),
                        rs.getInt("idPoliza"),
                        rs.getString("nombresU"),
                        rs.getString("apellidosU"),
                        rs.getString("nombreEmpresaP")
                ));
            }

            cargarEmpresasUnicas();
            filtrarUsuarios();
            filtrarEmpresas();

            System.out.println("Usuarios y empresas cargados correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar datos base: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los usuarios y empresas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEmpresasUnicas() {
        Map<Integer, EmpresaItem> mapaEmpresas = new LinkedHashMap<>();

        for (UsuarioItem usuario : listaUsuarios) {
            if (!mapaEmpresas.containsKey(usuario.getIdPoliza())) {
                mapaEmpresas.put(usuario.getIdPoliza(),
                        new EmpresaItem(usuario.getIdPoliza(), usuario.getEmpresa()));
            }
        }

        listaEmpresas.clear();
        listaEmpresas.addAll(mapaEmpresas.values());
    }

    private void activarFiltros() {
        vista.JTFUsuario.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }
        });

        vista.JTFEmpresa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarEmpresas();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarEmpresas();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarEmpresas();
            }
        });
    }

    private void filtrarUsuarios() {
        String texto = vista.JTFUsuario.getText().trim().toLowerCase();

        actualizandoCombos = true;

        vista.JCBUsuario.removeAllItems();
        vista.JCBUsuario.addItem("Seleccione usuario");

        usuariosFiltrados.clear();

        for (UsuarioItem usuario : listaUsuarios) {
            String visible = usuario.getNombreCompleto().toLowerCase();

            if (texto.isEmpty() || visible.contains(texto)) {
                usuariosFiltrados.add(usuario);
                vista.JCBUsuario.addItem(usuario.getNombreCompleto());
            }
        }

        actualizandoCombos = false;

        if (!texto.isEmpty() && vista.JCBUsuario.getItemCount() > 1) {
            vista.JCBUsuario.showPopup();
        }
    }

    private void filtrarEmpresas() {
        String texto = vista.JTFEmpresa.getText().trim().toLowerCase();

        actualizandoCombos = true;

        vista.JCBEmpresa.removeAllItems();
        vista.JCBEmpresa.addItem("Seleccione empresa");

        empresasFiltradas.clear();

        for (EmpresaItem empresa : listaEmpresas) {
            String visible = empresa.getNombreEmpresa().toLowerCase();

            if (texto.isEmpty() || visible.contains(texto)) {
                empresasFiltradas.add(empresa);
                vista.JCBEmpresa.addItem(empresa.getNombreEmpresa());
            }
        }

        actualizandoCombos = false;

        if (!texto.isEmpty() && vista.JCBEmpresa.getItemCount() > 1) {
            vista.JCBEmpresa.showPopup();
        }
    }

    private void cargarEmpresasDelUsuario(int idUsuario) {
        String sql = "SELECT DISTINCT p.idPoliza, p.nombreEmpresaP " +
                     "FROM usuario u " +
                     "JOIN poliza p ON u.idPoliza = p.idPoliza " +
                     "WHERE u.idUsuario = ? " +
                     "ORDER BY p.nombreEmpresaP";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                actualizandoCombos = true;

                vista.JCBEmpresa.removeAllItems();
                vista.JCBEmpresa.addItem("Seleccione empresa");

                empresasFiltradas.clear();

                while (rs.next()) {
                    EmpresaItem item = new EmpresaItem(
                            rs.getInt("idPoliza"),
                            rs.getString("nombreEmpresaP")
                    );
                    empresasFiltradas.add(item);
                    vista.JCBEmpresa.addItem(item.getNombreEmpresa());
                }

                actualizandoCombos = false;
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar empresa del usuario: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudo cargar la empresa del usuario.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarUsuariosDeEmpresa(int idPoliza) {
        String sql = "SELECT idUsuario, nombresU, apellidosU " +
                     "FROM usuario " +
                     "WHERE idPoliza = ? " +
                     "ORDER BY nombresU, apellidosU";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                actualizandoCombos = true;

                vista.JCBUsuario.removeAllItems();
                vista.JCBUsuario.addItem("Seleccione usuario");

                usuariosFiltrados.clear();

                while (rs.next()) {
                    UsuarioItem item = new UsuarioItem(
                            rs.getInt("idUsuario"),
                            idPoliza,
                            rs.getString("nombresU"),
                            rs.getString("apellidosU"),
                            ""
                    );
                    usuariosFiltrados.add(item);
                    vista.JCBUsuario.addItem(item.getNombreCompleto());
                }

                actualizandoCombos = false;
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar usuarios de la empresa: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los usuarios de la empresa.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTicketsPorUsuario(int idUsuario) {
        String sql = "SELECT idTicket, conceptoT " +
                     "FROM ticket " +
                     "WHERE idUsuario = ? " +
                     "ORDER BY fechaCreacionT DESC, idTicket DESC";

        cargarTickets(sql, idUsuario, true);
    }

    private void cargarTicketsPorEmpresa(int idPoliza) {
        String sql = "SELECT idTicket, conceptoT " +
                     "FROM ticket " +
                     "WHERE idPoliza = ? " +
                     "ORDER BY fechaCreacionT DESC, idTicket DESC";

        cargarTickets(sql, idPoliza, false);
    }

    private void cargarTickets(String sql, int id, boolean porUsuario) {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                actualizandoCombos = true;

                vista.JCBTicket.removeAllItems();
                vista.JCBTicket.addItem("Seleccione ticket");

                ticketsActuales.clear();
                idTicketSeleccionado = null;

                while (rs.next()) {
                    TicketItem item = new TicketItem(
                            rs.getInt("idTicket"),
                            rs.getString("conceptoT")
                    );
                    ticketsActuales.add(item);
                    vista.JCBTicket.addItem(item.getIdTicket() + " - " + item.getConcepto());
                }

                actualizandoCombos = false;

                if (ticketsActuales.isEmpty()) {
                    JOptionPane.showMessageDialog(vista,
                            porUsuario
                                    ? "Este usuario no tiene tickets registrados."
                                    : "Esta empresa no tiene tickets registrados.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar tickets: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los tickets.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (actualizandoCombos || e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        if (e.getSource() == vista.JCBUsuario) {
            int indice = vista.JCBUsuario.getSelectedIndex();

            if (indice > 0) {
                UsuarioItem usuario = usuariosFiltrados.get(indice - 1);
                idUsuarioSeleccionado = usuario.getIdUsuario();
                idPolizaSeleccionada = usuario.getIdPoliza();

                cargarEmpresasDelUsuario(idUsuarioSeleccionado);
                cargarTicketsPorUsuario(idUsuarioSeleccionado);

                System.out.println("Usuario seleccionado: " + usuario.getNombreCompleto());
            }
        }

        if (e.getSource() == vista.JCBEmpresa) {
            int indice = vista.JCBEmpresa.getSelectedIndex();

            if (indice > 0) {
                EmpresaItem empresa = empresasFiltradas.get(indice - 1);
                idPolizaSeleccionada = empresa.getIdPoliza();

                cargarUsuariosDeEmpresa(idPolizaSeleccionada);
                cargarTicketsPorEmpresa(idPolizaSeleccionada);

                System.out.println("Empresa seleccionada: " + empresa.getNombreEmpresa());
            }
        }

        if (e.getSource() == vista.JCBTicket) {
            int indice = vista.JCBTicket.getSelectedIndex();

            if (indice > 0) {
                TicketItem ticket = ticketsActuales.get(indice - 1);
                idTicketSeleccionado = ticket.getIdTicket();

                System.out.println("Ticket seleccionado: " + idTicketSeleccionado);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteLS3) {
            continuarEdicion();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

        private void continuarEdicion() {
            if (idTicketSeleccionado == null) {
                JOptionPane.showMessageDialog(vista,
                        "Seleccione un ticket válido.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ModificarTicket2 vista2 = new ModificarTicket2();
            new ControladorModificarTicket2(vista2, idTicketSeleccionado);
            vista2.setVisible(true);
            vista.dispose();
        }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}