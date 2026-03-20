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
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;

public class ControladorNuevaPoliza implements ActionListener {

    private NuevaPoliza vista;
    private ButtonGroup grupoPlanes;

    public ControladorNuevaPoliza(NuevaPoliza vista) {
        this.vista = vista;

        this.vista.JBNSiguiente.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        agruparRadios();
        cargarClientes();
    }

    private void agruparRadios() {
        grupoPlanes = new ButtonGroup();
        grupoPlanes.add(vista.JRBEscencial);
        grupoPlanes.add(vista.JRBProfesional);
        grupoPlanes.add(vista.JRBEmpresarial);
    }

    private void cargarClientes() {
        String sql = "SELECT idCliente, nombreC FROM cliente ORDER BY nombreC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            vista.JCBClientes.removeAllItems();
            vista.JCBClientes.addItem("Seleccione un cliente");

            while (rs.next()) {
                int idCliente = rs.getInt("idCliente");
                String nombreCliente = rs.getString("nombreC");

                // Guardamos id y nombre juntos
                vista.JCBClientes.addItem(idCliente + " - " + nombreCliente);
            }

            System.out.println("Clientes cargados correctamente en el combo.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar clientes: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "No se pudieron cargar los clientes.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        if (vista.JCBClientes.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un cliente.",
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

        String clienteSeleccionado = vista.JCBClientes.getSelectedItem().toString();

        // Formato esperado: "3 - Abarrotes San Juan"
        String[] partes = clienteSeleccionado.split(" - ", 2);

        int idCliente = Integer.parseInt(partes[0]);
        String nombreCliente = partes[1];

        System.out.println("Cliente seleccionado: " + nombreCliente);
        System.out.println("ID Cliente: " + idCliente);
        System.out.println("Plan seleccionado: " + planSeleccionado);

        NuevaPoliza2 vista2 = new NuevaPoliza2();

        // Este constructor lo harás en el segundo controlador
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