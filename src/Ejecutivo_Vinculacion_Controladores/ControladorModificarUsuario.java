package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.ModificarUsuario;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControladorModificarUsuario implements ActionListener, ItemListener {

    private ModificarUsuario vista;

    private List<EmpresaItem> listaEmpresas = new ArrayList<>();
    private List<EmpresaItem> empresasFiltradas = new ArrayList<>();

    private List<UsuarioItem> listaUsuarios = new ArrayList<>();
    private List<UsuarioItem> usuariosFiltrados = new ArrayList<>();

    private Integer idPolizaSeleccionada = null;
    private Integer idUsuarioSeleccionado = null;
    private boolean usuarioConsultado = false;

    public ControladorModificarUsuario(ModificarUsuario vista) {
        this.vista = vista;

        this.vista.JBTguardar.addActionListener(this);
        this.vista.JBTcancelar.addActionListener(this);

        this.vista.JCBempresa.addItemListener(this);
        this.vista.JCBcliente.addItemListener(this);

        prepararCombos();
        activarFiltros();
        cargarEmpresas();
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

    private static class UsuarioItem {
        private int idUsuario;
        private String nombreCompleto;

        public UsuarioItem(int idUsuario, String nombres, String apellidos) {
            this.idUsuario = idUsuario;
            this.nombreCompleto = nombres + " " + apellidos;
        }

        public int getIdUsuario() {
            return idUsuario;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }
    }

    private void prepararCombos() {
        vista.JCBempresa.removeAllItems();
        vista.JCBempresa.addItem("Seleccione empresa");

        vista.JCBcliente.removeAllItems();
        vista.JCBcliente.addItem("Seleccione usuario");
    }

    private void activarFiltros() {
        vista.JTFempresa.getDocument().addDocumentListener(new DocumentListener() {
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

        vista.JTFcliente.getDocument().addDocumentListener(new DocumentListener() {
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

    private void cargarEmpresas() {
        String sql = "SELECT DISTINCT p.idPoliza, p.nombreEmpresaP " +
                     "FROM poliza p " +
                     "JOIN usuario u ON u.idPoliza = p.idPoliza " +
                     "ORDER BY p.nombreEmpresaP";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            listaEmpresas.clear();

            while (rs.next()) {
                listaEmpresas.add(new EmpresaItem(
                        rs.getInt("idPoliza"),
                        rs.getString("nombreEmpresaP")
                ));
            }

            filtrarEmpresas();
            System.out.println("Empresas cargadas correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar empresas: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar las empresas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

        private void filtrarEmpresas() {
            String texto = vista.JTFempresa.getText().trim().toLowerCase();

            vista.JCBempresa.removeAllItems();
            vista.JCBempresa.addItem("Seleccione empresa");

            empresasFiltradas.clear();
            limpiarUsuariosYCampos();

            if (texto.isEmpty()) {
                return;
            }

            for (EmpresaItem empresa : listaEmpresas) {
                String nombre = empresa.getNombreEmpresa().toLowerCase();

                if (nombre.contains(texto)) {
                    empresasFiltradas.add(empresa);
                    vista.JCBempresa.addItem(empresa.getNombreEmpresa());
                }
            }

            if (vista.JCBempresa.getItemCount() > 1) {
                vista.JCBempresa.showPopup();
            }
        }

    private void cargarUsuariosPorEmpresa(int idPoliza) {
        String sql = "SELECT idUsuario, nombresU, apellidosU " +
                     "FROM usuario " +
                     "WHERE idPoliza = ? " +
                     "ORDER BY nombresU, apellidosU";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                listaUsuarios.clear();

                while (rs.next()) {
                    listaUsuarios.add(new UsuarioItem(
                            rs.getInt("idUsuario"),
                            rs.getString("nombresU"),
                            rs.getString("apellidosU")
                    ));
                }

                filtrarUsuarios();
                System.out.println("Usuarios cargados para la empresa.");

            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar usuarios: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los usuarios de la empresa.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

        private void filtrarUsuarios() {
            String texto = vista.JTFcliente.getText().trim().toLowerCase();

            vista.JCBcliente.removeAllItems();
            vista.JCBcliente.addItem("Seleccione usuario");

            usuariosFiltrados.clear();
            idUsuarioSeleccionado = null;
            usuarioConsultado = false;
            limpiarCamposUsuario();

            for (UsuarioItem usuario : listaUsuarios) {
                String nombre = usuario.getNombreCompleto().toLowerCase();

                if (texto.isEmpty() || nombre.contains(texto)) {
                    usuariosFiltrados.add(usuario);
                    vista.JCBcliente.addItem(usuario.getNombreCompleto());
                }
            }

            if (!texto.isEmpty() && vista.JCBcliente.getItemCount() > 1) {
                vista.JCBcliente.showPopup();
            }
        }

    private void cargarDatosUsuario(int idUsuario) {
        String sql = "SELECT nombresU, apellidosU, locacionU, correoU, telefonoU " +
                     "FROM usuario " +
                     "WHERE idUsuario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vista.JTFnombre.setText(rs.getString("nombresU"));
                    vista.JTFapellido.setText(rs.getString("apellidosU"));
                    vista.JTFlocacion.setText(rs.getString("locacionU"));
                    vista.JTFcorreo.setText(rs.getString("correoU"));
                    vista.JTFtelefono.setText(rs.getString("telefonoU"));

                    usuarioConsultado = true;
                    System.out.println("Usuario consultado correctamente. ID: " + idUsuario);
                } else {
                    usuarioConsultado = false;
                    limpiarCamposUsuario();
                    JOptionPane.showMessageDialog(vista,
                            "No se encontró información del usuario.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al cargar datos del usuario: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar los datos del usuario.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        if (!usuarioConsultado || idUsuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Primero consulte un usuario válido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = vista.JTFnombre.getText().trim();
        String apellido = vista.JTFapellido.getText().trim();
        String locacion = vista.JTFlocacion.getText().trim();
        String correo = vista.JTFcorreo.getText().trim();
        String telefono = vista.JTFtelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || locacion.isEmpty()
                || correo.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Complete todos los campos antes de guardar.",
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

        String sql = "UPDATE usuario " +
                     "SET nombresU = ?, apellidosU = ?, locacionU = ?, correoU = ?, telefonoU = ? " +
                     "WHERE idUsuario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, locacion);
            ps.setString(4, correo);
            ps.setString(5, telefono);
            ps.setInt(6, idUsuarioSeleccionado);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(vista,
                        "Usuario actualizado correctamente.");
                System.out.println("Usuario actualizado. ID: " + idUsuarioSeleccionado);

                if (idPolizaSeleccionada != null) {
                    cargarUsuariosPorEmpresa(idPolizaSeleccionada);
                }
                  volverAlMenu();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo actualizar el usuario.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al actualizar usuario: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposUsuario() {
        vista.JTFnombre.setText("");
        vista.JTFapellido.setText("");
        vista.JTFlocacion.setText("");
        vista.JTFcorreo.setText("");
        vista.JTFtelefono.setText("");
    }

    private void limpiarUsuariosYCampos() {
        idPolizaSeleccionada = null;
        idUsuarioSeleccionado = null;
        usuarioConsultado = false;

        listaUsuarios.clear();
        usuariosFiltrados.clear();

        vista.JCBcliente.removeAllItems();
        vista.JCBcliente.addItem("Seleccione usuario");

        limpiarCamposUsuario();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBTguardar) {
            guardarCambios();
        } else if (e.getSource() == vista.JBTcancelar) {
            volverAlMenu();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        if (e.getSource() == vista.JCBempresa) {
            int indiceEmpresa = vista.JCBempresa.getSelectedIndex();

            if (indiceEmpresa > 0) {
                EmpresaItem empresa = empresasFiltradas.get(indiceEmpresa - 1);
                idPolizaSeleccionada = empresa.getIdPoliza();

                vista.JTFcliente.setText(""); // limpia filtro viejo
                cargarUsuariosPorEmpresa(idPolizaSeleccionada);
            } else {
                limpiarUsuariosYCampos();
            }
        }

        if (e.getSource() == vista.JCBcliente) {
            int indiceUsuario = vista.JCBcliente.getSelectedIndex();

            if (indiceUsuario > 0) {
                UsuarioItem usuario = usuariosFiltrados.get(indiceUsuario - 1);
                idUsuarioSeleccionado = usuario.getIdUsuario();
                cargarDatosUsuario(idUsuarioSeleccionado);
            } else {
                idUsuarioSeleccionado = null;
                usuarioConsultado = false;
                limpiarCamposUsuario();
            }
        }
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}