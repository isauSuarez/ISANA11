package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza2;
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

public class ControladorNuevaPoliza implements ActionListener {

    private NuevaPoliza vista;
    private ButtonGroup grupoPlanes;

    // Lista completa de clientes
    private List<ClienteItem> listaClientes = new ArrayList<>();

    // Lista filtrada según lo que se escriba
    private List<ClienteItem> clientesFiltrados = new ArrayList<>();

    public ControladorNuevaPoliza(NuevaPoliza vista) {
        this.vista = vista;

        this.vista.JBNSiguiente.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        agruparRadios();
        cargarClientes();
        activarFiltroTiempoReal();
    }

    // Clase interna para guardar id + nombre sin mostrar el id
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

    private void agruparRadios() {
        grupoPlanes = new ButtonGroup();
        grupoPlanes.add(vista.JRBEscencial);
        grupoPlanes.add(vista.JRBProfesional);
        grupoPlanes.add(vista.JRBEmpresarial);
    }

    private void cargarClientes() {
        String sql = "SELECT idCliente, nombreC, apellidoC FROM cliente ORDER BY nombreC, apellidoC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            listaClientes.clear();

            while (rs.next()) {
                int idCliente = rs.getInt("idCliente");
                String nombre = rs.getString("nombreC");
                String apellido = rs.getString("apellidoC");

                listaClientes.add(new ClienteItem(idCliente, nombre, apellido));
            }

            filtrarClientes();
            System.out.println("Clientes cargados correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar clientes: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los clientes.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activarFiltroTiempoReal() {
        // Cambia jTextField1 por el nombre real si luego lo renombras
        vista.JTFBusqueda.getDocument().addDocumentListener(new DocumentListener() {
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
        String texto = vista.JTFBusqueda.getText().trim().toLowerCase();

        vista.JCBClientes.removeAllItems();
        vista.JCBClientes.addItem("Seleccione un cliente");

        clientesFiltrados.clear();

        for (ClienteItem cliente : listaClientes) {
            String nombreCompleto = cliente.getNombreCompleto().toLowerCase();

            // Filtra por los que comienzan con lo escrito
            if (texto.isEmpty() || nombreCompleto.startsWith(texto)) {
                clientesFiltrados.add(cliente);
                vista.JCBClientes.addItem(cliente.getNombreCompleto());
            }
        }

        if (!texto.isEmpty() && vista.JCBClientes.getItemCount() > 1) {
            vista.JCBClientes.showPopup();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguiente) {
            irASegundoPaso();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void irASegundoPaso() {
        int indiceSeleccionado = vista.JCBClientes.getSelectedIndex();

        if (indiceSeleccionado <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un cliente válido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planSeleccionado = obtenerPlanSeleccionado();

        if (planSeleccionado == null) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un plan.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClienteItem cliente = clientesFiltrados.get(indiceSeleccionado - 1);

        int idCliente = cliente.getIdCliente();
        String nombreCliente = cliente.getNombreCompleto();

        System.out.println("Cliente seleccionado: " + nombreCliente);
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("Plan seleccionado: " + planSeleccionado);

        NuevaPoliza2 vista2 = new NuevaPoliza2();
        new ControladorNuevaPoliza2(vista2, idCliente, nombreCliente, planSeleccionado);

        vista2.setVisible(true);
        vista.dispose();
    }

    private String obtenerPlanSeleccionado() {
        if (vista.JRBEscencial.isSelected()) {
            return "Esencial";
        }
        if (vista.JRBProfesional.isSelected()) {
            return "Profesional";
        }
        if (vista.JRBEmpresarial.isSelected()) {
            return "Empresarial";
        }
        return null;
    }

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}