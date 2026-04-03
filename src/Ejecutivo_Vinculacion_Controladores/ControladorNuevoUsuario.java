package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.Nuevo_Usuario;
import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
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

public class ControladorNuevoUsuario implements ActionListener, ItemListener {

    private Nuevo_Usuario vista;

    private List<ClienteItem> listaClientes = new ArrayList<>();
    private List<ClienteItem> clientesFiltrados = new ArrayList<>();
    private List<PolizaItem> empresasDelCliente = new ArrayList<>();

    public ControladorNuevoUsuario(Nuevo_Usuario vista) {
        this.vista = vista;

        this.vista.JBNSiguiente.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        this.vista.JCBClientes.addItemListener(this);
        this.vista.JTEmpresa.addItemListener(this);

        cargarClientes();
        activarFiltroTiempoReal();
        prepararCombos();
    }

    private static class ClienteItem {
        private int idCliente;
        private String nombreCompleto;

        public ClienteItem(int idCliente, String nombre, String apellido) {
            this.idCliente = idCliente;
            this.nombreCompleto = nombre + " " + apellido;
        }

        public int getIdCliente() {
            return idCliente;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }
    }

    private static class PolizaItem {
        private int idPoliza;
        private String empresa;

        public PolizaItem(int idPoliza, String empresa) {
            this.idPoliza = idPoliza;
            this.empresa = empresa;
        }

        public int getIdPoliza() {
            return idPoliza;
        }

        public String getEmpresa() {
            return empresa;
        }
    }

    private void prepararCombos() {
        vista.JCBClientes.removeAllItems();
        vista.JCBClientes.addItem("Clientes registrados...");

        vista.JTEmpresa.removeAllItems();
        vista.JTEmpresa.addItem("Empresa");
    }

        private void cargarClientes() {
            String sql = "SELECT idCliente, nombreC, apellidoC " +
                         "FROM cliente " +
                         "ORDER BY nombreC, apellidoC";

            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                listaClientes.clear();

                while (rs.next()) {
                    listaClientes.add(new ClienteItem(
                            rs.getInt("idCliente"),
                            rs.getString("nombreC"),
                            rs.getString("apellidoC")
                    ));
                }

                filtrarClientes();

            } catch (SQLException ex) {
                System.out.println("Error al cargar clientes: " + ex.getMessage());
                JOptionPane.showMessageDialog(vista,
                        "No se pudieron cargar los clientes.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    private void activarFiltroTiempoReal() {
        vista.JTFcliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarClientes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarClientes();
            }
        });
    }

    private void filtrarClientes() {
        String texto = vista.JTFcliente.getText().trim().toLowerCase();

        vista.JCBClientes.removeAllItems();
        vista.JCBClientes.addItem("Clientes registrados...");

        clientesFiltrados.clear();

        for (ClienteItem cliente : listaClientes) {
            String nombre = cliente.getNombreCompleto().toLowerCase();

            if (texto.isEmpty() || nombre.startsWith(texto)) {
                clientesFiltrados.add(cliente);
                vista.JCBClientes.addItem(cliente.getNombreCompleto());
            }
        }

        vista.JTEmpresa.removeAllItems();
        vista.JTEmpresa.addItem("Empresa");
        empresasDelCliente.clear();

        if (!texto.isEmpty() && vista.JCBClientes.getItemCount() > 1) {
            vista.JCBClientes.showPopup();
        }
    }

        private void cargarEmpresasPorCliente(int idCliente) {
            String sql = "SELECT idPoliza, nombreEmpresaP " +
                         "FROM poliza " +
                         "WHERE idCliente = ? AND estadoP = 'Activa' " +
                         "ORDER BY nombreEmpresaP";

            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, idCliente);

                try (ResultSet rs = ps.executeQuery()) {
                    empresasDelCliente.clear();

                    vista.JTEmpresa.removeAllItems();
                    vista.JTEmpresa.addItem("Empresa");

                    while (rs.next()) {
                        PolizaItem item = new PolizaItem(
                                rs.getInt("idPoliza"),
                                rs.getString("nombreEmpresaP")
                        );
                        empresasDelCliente.add(item);
                        vista.JTEmpresa.addItem(item.getEmpresa());
                    }

                    if (empresasDelCliente.isEmpty()) {
                        JOptionPane.showMessageDialog(vista,
                                "Este cliente no tiene empresas con póliza activa.",
                                "Aviso",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                System.out.println("Error al cargar empresas: " + ex.getMessage());
                JOptionPane.showMessageDialog(vista,
                        "No se pudieron cargar las empresas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    private boolean empresaYaTieneTresUsuarios(int idPoliza) {
        String sql = "SELECT COUNT(*) AS total FROM usuario WHERE idPoliza = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") >= 3;
                }
            }

        } catch (SQLException ex) {
            System.out.println("Error al validar usuarios: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Error al validar los usuarios de la empresa.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguiente) {
            pasarASegundoPaso();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        if (e.getSource() == vista.JCBClientes) {
            int indiceCliente = vista.JCBClientes.getSelectedIndex();

            vista.JTEmpresa.removeAllItems();
            vista.JTEmpresa.addItem("Empresa");
            empresasDelCliente.clear();

            if (indiceCliente > 0) {
                ClienteItem cliente = clientesFiltrados.get(indiceCliente - 1);
                cargarEmpresasPorCliente(cliente.getIdCliente());
            }
        }
    }

    private void pasarASegundoPaso() {
        int indiceCliente = vista.JCBClientes.getSelectedIndex();
        int indiceEmpresa = vista.JTEmpresa.getSelectedIndex();

        if (indiceCliente <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un cliente válido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (indiceEmpresa <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione una empresa válida.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClienteItem cliente = clientesFiltrados.get(indiceCliente - 1);
        PolizaItem empresa = empresasDelCliente.get(indiceEmpresa - 1);

        int idCliente = cliente.getIdCliente();
        int idPoliza = empresa.getIdPoliza();
        String textoVisible = cliente.getNombreCompleto() + " - " + empresa.getEmpresa();

        if (empresaYaTieneTresUsuarios(idPoliza)) {
            JOptionPane.showMessageDialog(vista,
                    "Esta empresa ya tiene el máximo de 3 usuarios autorizados.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Cliente seleccionado: " + cliente.getNombreCompleto());
        System.out.println("Empresa seleccionada: " + empresa.getEmpresa());
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("ID Póliza: " + idPoliza);

        NuevoUsuario2 vista2 = new NuevoUsuario2();
        new ControladorNuevoUsuario2(vista2, idCliente, idPoliza, textoVisible);
        vista2.setVisible(true);
        vista.dispose();
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}