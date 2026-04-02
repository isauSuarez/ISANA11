package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.Nuevo_Usuario;
import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControladorNuevoUsuario implements ActionListener {

    private Nuevo_Usuario vista;

    private List<ClienteEmpresaItem> listaOpciones = new ArrayList<>();
    private List<ClienteEmpresaItem> opcionesFiltradas = new ArrayList<>();

    public ControladorNuevoUsuario(Nuevo_Usuario vista) {
        this.vista = vista;

        this.vista.JBNSiguiente.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        cargarClientesEmpresas();
        activarFiltroTiempoReal();
    }

    private static class ClienteEmpresaItem {
        private int idCliente;
        private int idPoliza;
        private String textoVisible;

        public ClienteEmpresaItem(int idCliente, int idPoliza, String nombre, String apellido, String empresa) {
            this.idCliente = idCliente;
            this.idPoliza = idPoliza;
            this.textoVisible = nombre + " " + apellido + " - " + empresa;
        }

        public int getIdCliente() {
            return idCliente;
        }

        public int getIdPoliza() {
            return idPoliza;
        }

        public String getTextoVisible() {
            return textoVisible;
        }
    }

    private void cargarClientesEmpresas() {
        String sql = "SELECT c.idCliente, p.idPoliza, c.nombreC, c.apellidoC, p.nombreEmpresaP " +
                     "FROM cliente c " +
                     "JOIN poliza p ON p.idCliente = c.idCliente " +
                     "WHERE p.estadoP = 'Activa' " +
                     "ORDER BY c.nombreC, c.apellidoC, p.nombreEmpresaP";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            listaOpciones.clear();

            while (rs.next()) {
                listaOpciones.add(new ClienteEmpresaItem(
                        rs.getInt("idCliente"),
                        rs.getInt("idPoliza"),
                        rs.getString("nombreC"),
                        rs.getString("apellidoC"),
                        rs.getString("nombreEmpresaP")
                ));
            }

            filtrarOpciones();
            System.out.println("Clientes y empresas cargados correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar clientes y empresas: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los clientes y empresas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activarFiltroTiempoReal() {
        vista.JTFcliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarOpciones();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarOpciones();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarOpciones();
            }
        });
    }

    private void filtrarOpciones() {
        String texto = vista.JTFcliente.getText().trim().toLowerCase();

        vista.JCBClientes.removeAllItems();
        vista.JCBClientes.addItem("Clientes registrados...");

        opcionesFiltradas.clear();

        for (ClienteEmpresaItem item : listaOpciones) {
            String visible = item.getTextoVisible().toLowerCase();

            if (texto.isEmpty() || visible.startsWith(texto) || visible.contains(texto)) {
                opcionesFiltradas.add(item);
                vista.JCBClientes.addItem(item.getTextoVisible());
            }
        }

        if (!texto.isEmpty() && vista.JCBClientes.getItemCount() > 1) {
            vista.JCBClientes.showPopup();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguiente) {
            pasarASegundoPaso();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void pasarASegundoPaso() {
        int indice = vista.JCBClientes.getSelectedIndex();

        if (indice <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un cliente y empresa válidos.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClienteEmpresaItem seleccionado = opcionesFiltradas.get(indice - 1);

        int idCliente = seleccionado.getIdCliente();
        int idPoliza = seleccionado.getIdPoliza();
        String textoVisible = seleccionado.getTextoVisible();

        if (empresaYaTieneTresUsuarios(idPoliza)) {
            JOptionPane.showMessageDialog(vista,
                    "Esta empresa ya tiene el máximo de 3 usuarios autorizados.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Cliente/Empresa seleccionados: " + textoVisible);
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("ID Póliza: " + idPoliza);

        NuevoUsuario2 vista2 = new NuevoUsuario2();
        new ControladorNuevoUsuario2(vista2, idCliente, idPoliza, textoVisible);
        vista2.setVisible(true);
        vista.dispose();
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
            System.out.println("Error al validar usuarios por empresa: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Error al validar el número de usuarios autorizados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}