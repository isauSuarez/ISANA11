package Supervisor_Tecnico_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.NuevoTicket;
import Supervisor_Tecnico_Frames.NuevoTicket2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControladorNuevoTicket implements ActionListener {

    private NuevoTicket vista;
    private ButtonGroup grupoModalidad;

    private List<UsuarioItem> listaUsuarios = new ArrayList<>();
    private List<UsuarioItem> usuariosFiltrados = new ArrayList<>();

    public ControladorNuevoTicket(NuevoTicket vista) {
        this.vista = vista;

        this.vista.JBNSiguienteJLS3.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        agruparRadios();
        cargarUsuarios();
        activarFiltroTiempoReal();
    }

    private static class UsuarioItem {
        private int idUsuario;
        private int idPoliza;
        private String nombreUsuario;
        private String nombreEmpresa;
        private String textoVisible;

        public UsuarioItem(int idUsuario, int idPoliza, String nombres, String apellidos, String empresa) {
            this.idUsuario = idUsuario;
            this.idPoliza = idPoliza;
            this.nombreUsuario = nombres + " " + apellidos;
            this.nombreEmpresa = empresa;
            this.textoVisible = this.nombreUsuario + " - " + this.nombreEmpresa;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public int getIdPoliza() {
            return idPoliza;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public String getNombreEmpresa() {
            return nombreEmpresa;
        }

        public String getTextoVisible() {
            return textoVisible;
        }
    }

    private void agruparRadios() {
        grupoModalidad = new ButtonGroup();
        grupoModalidad.add(vista.JRBPresencial);
        grupoModalidad.add(vista.JRBRemoto);
        grupoModalidad.add(vista.JRBAsesoria);
    }

    private void cargarUsuarios() {
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

            filtrarUsuarios();
            System.out.println("Usuarios cargados correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar usuarios: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los usuarios.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activarFiltroTiempoReal() {
        vista.JTFusuario.getDocument().addDocumentListener(new DocumentListener() {
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
    }

    private void filtrarUsuarios() {
        String texto = vista.JTFusuario.getText().trim().toLowerCase();

        vista.JCBUsuario.removeAllItems();
        vista.JCBUsuario.addItem("Seleccione usuario");

        usuariosFiltrados.clear();

        for (UsuarioItem usuario : listaUsuarios) {
            String visible = usuario.getTextoVisible().toLowerCase();

            if (texto.isEmpty() || visible.startsWith(texto) || visible.contains(texto)) {
                usuariosFiltrados.add(usuario);
                vista.JCBUsuario.addItem(usuario.getTextoVisible());
            }
        }

        if (!texto.isEmpty() && vista.JCBUsuario.getItemCount() > 1) {
            vista.JCBUsuario.showPopup();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteJLS3) {
            irASegundoPaso();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void irASegundoPaso() {
        int indiceSeleccionado = vista.JCBUsuario.getSelectedIndex();

        if (indiceSeleccionado <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un usuario válido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String modalidadSeleccionada = obtenerModalidadSeleccionada();

        if (modalidadSeleccionada == null) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione la modalidad del servicio.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioItem usuario = usuariosFiltrados.get(indiceSeleccionado - 1);

        int idUsuario = usuario.getIdUsuario();
        int idPoliza = usuario.getIdPoliza();
        String nombreUsuario = usuario.getNombreUsuario();
        String nombreEmpresa = usuario.getNombreEmpresa();

        System.out.println("Usuario seleccionado: " + nombreUsuario);
        System.out.println("Empresa: " + nombreEmpresa);
        System.out.println("ID Usuario: " + idUsuario);
        System.out.println("ID Póliza: " + idPoliza);
        System.out.println("Modalidad: " + modalidadSeleccionada);

        NuevoTicket2 vista2 = new NuevoTicket2();
        new ControladorNuevoTicket2(vista2, idUsuario, idPoliza, nombreUsuario, nombreEmpresa, modalidadSeleccionada);
        vista2.setVisible(true);
        vista.dispose();
    }

    private String obtenerModalidadSeleccionada() {
        if (vista.JRBPresencial.isSelected()) {
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

        private void volverAlMenu() {
            MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
            new ControladorMenuSupervisor(menu);
            menu.setVisible(true);
            vista.dispose();
        }
}