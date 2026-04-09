package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.RenovarPoliza;
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
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class ControladorRenovarPoliza implements ActionListener, ItemListener {

    private RenovarPoliza vista;
    private ButtonGroup grupoPlanes;

    private List<ClienteItem> listaClientes = new ArrayList<>();
    private List<ClienteItem> clientesFiltrados = new ArrayList<>();
    private List<PolizaItem> polizasCliente = new ArrayList<>();
    private Integer idPolizaSeleccionada = null;

    public ControladorRenovarPoliza(RenovarPoliza vista) {
        this.vista = vista;

        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        this.vista.JCBCliente.addItemListener(this);
        this.vista.JCBEmpresa.addItemListener(this);
        

        agruparRadios();
        cargarClientes();
        activarFiltroTiempoReal();
        prepararTabla();
        cargarFechasRenovacion();
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

    private void agruparRadios() {
        grupoPlanes = new ButtonGroup();
        grupoPlanes.add(vista.JRBEscencial);
        grupoPlanes.add(vista.JRBProfesional);
        grupoPlanes.add(vista.JRBEmpresarial);
    }

    private void prepararTabla() {
        String[] columnas = {"Estado", "Plan", "Fecha de Termino"};

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vista.JTInfo.setModel(modelo);
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
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar clientes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activarFiltroTiempoReal() {
        vista.JTFCliente1.getDocument().addDocumentListener(new DocumentListener() {
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
        String texto = vista.JTFCliente1.getText().trim().toLowerCase();

        vista.JCBCliente.removeAllItems();
        vista.JCBCliente.addItem("Seleccione cliente");

        clientesFiltrados.clear();

        for (ClienteItem cliente : listaClientes) {
            String nombre = cliente.getNombreCompleto().toLowerCase();

            if (texto.isEmpty() || nombre.startsWith(texto)) {
                clientesFiltrados.add(cliente);
                vista.JCBCliente.addItem(cliente.getNombreCompleto());
            }
        }

        vista.JCBEmpresa.removeAllItems();
        vista.JCBEmpresa.addItem("Empresa");
        limpiarTabla();

        if (!texto.isEmpty() && vista.JCBCliente.getItemCount() > 1) {
            vista.JCBCliente.showPopup();
        }
    }

    private void cargarEmpresasPorCliente(int idCliente) {
        String sql = "SELECT p.idPoliza, p.nombreEmpresaP " +
                     "FROM poliza p " +
                     "WHERE p.idCliente = ? " +
                     "ORDER BY p.nombreEmpresaP";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                polizasCliente.clear();

                vista.JCBEmpresa.removeAllItems();
                vista.JCBEmpresa.addItem("Empresa");

                while (rs.next()) {
                    PolizaItem item = new PolizaItem(
                            rs.getInt("idPoliza"),
                            rs.getString("nombreEmpresaP")
                    );
                    polizasCliente.add(item);
                    vista.JCBEmpresa.addItem(item.getEmpresa());
                }

                limpiarTabla();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar empresas: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        idPolizaSeleccionada = null;
    }

    private void cargarInfoPoliza(int idPoliza) {
        String sql = "SELECT p.estadoP, pl.nombreP, p.fechaVencimientoP " +
                     "FROM poliza p " +
                     "JOIN plan pl ON pl.idPoliza = p.idPoliza " +
                     "WHERE p.idPoliza = ?";

        DefaultTableModel modelo = (DefaultTableModel) vista.JTInfo.getModel();
        modelo.setRowCount(0);

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPoliza);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object[] fila = {
                        rs.getString("estadoP"),
                        rs.getString("nombreP"),
                        rs.getDate("fechaVencimientoP")
                    };
                    modelo.addRow(fila);

                    marcarPlanActual(rs.getString("nombreP"));
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "No se encontró información de la póliza.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error al cargar información de la póliza: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void marcarPlanActual(String plan) {
        grupoPlanes.clearSelection();

        if (plan == null) return;

        switch (plan.toLowerCase()) {
            case "esencial":
                vista.JRBEscencial.setSelected(true);
                break;
            case "profesional":
                vista.JRBProfesional.setSelected(true);
                break;
            case "empresarial":
                vista.JRBEmpresarial.setSelected(true);
                break;
        }
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

    private void limpiarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.JTInfo.getModel();
        modelo.setRowCount(0);
        grupoPlanes.clearSelection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if (e.getSource() == vista.JBNCancelar) {
                volverAlMenu();
            } else if (e.getSource() == vista.JBNGuardar) {
                guardarRenovacion();
            }
    }

@Override

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }

                if (e.getSource() == vista.JCBCliente) {
                    int indice = vista.JCBCliente.getSelectedIndex();

                    idPolizaSeleccionada = null;
                    vista.JCBEmpresa.removeAllItems();
                    vista.JCBEmpresa.addItem("Empresa");
                    limpiarTabla();

                    if (indice > 0) {
                        ClienteItem cliente = clientesFiltrados.get(indice - 1);
                        cargarEmpresasPorCliente(cliente.getIdCliente());
                    }
                }

                if (e.getSource() == vista.JCBEmpresa) {
                    int indice = vista.JCBEmpresa.getSelectedIndex();

                    if (indice > 0) {
                        PolizaItem poliza = polizasCliente.get(indice - 1);
                        idPolizaSeleccionada = poliza.getIdPoliza();
                        cargarInfoPoliza(idPolizaSeleccionada);
                    } else {
                        idPolizaSeleccionada = null;
                        limpiarTabla();
                    }
                }
            }
    

    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
    
    private void cargarFechasRenovacion() {
    vista.JCBFechaRenovacion.removeAllItems();
    vista.JCBFechaRenovacion.addItem("Fecha");

    java.time.LocalDate hoy = java.time.LocalDate.now();

    for (int i = 1; i <= 12; i++) {
        vista.JCBFechaRenovacion.addItem(hoy.plusMonths(i).toString());
    }
}
    
    
private void guardarRenovacion() {
    if (idPolizaSeleccionada == null) {
        JOptionPane.showMessageDialog(vista, "Seleccione una empresa.");
        return;
    }

    String nuevoPlan = obtenerPlanSeleccionado();
    String nuevaFecha = vista.JCBFechaRenovacion.getSelectedItem().toString();

    if (nuevoPlan == null || nuevaFecha.equals("Fecha")) {
        JOptionPane.showMessageDialog(vista, "Seleccione plan y fecha.");
        return;
    }

    int maxPres = 0, maxRem = 0, maxAse = 0;

    switch (nuevoPlan.toLowerCase()) {
        case "esencial":
            maxPres = 5; maxRem = 10; maxAse = 10;
            break;
        case "profesional":
            maxPres = 15; maxRem = 25; maxAse = 15;
            break;
        case "empresarial":
            maxPres = 30; maxRem = 30; maxAse = 25;
            break;
        default:
            JOptionPane.showMessageDialog(vista, "Plan no válido.");
            return;
    }

    String sqlPoliza = "UPDATE poliza SET estadoP = ?, fechaVencimientoP = ? WHERE idPoliza = ?";
    String sqlPlan = "UPDATE plan SET nombreP = ?, maxPres = ?, maxRem = ?, maxAse = ? WHERE idPoliza = ?";

    try (Connection con = Conexion.getConexion()) {
        con.setAutoCommit(false);

        try (PreparedStatement psPoliza = con.prepareStatement(sqlPoliza);
             PreparedStatement psPlan = con.prepareStatement(sqlPlan)) {

            psPoliza.setString(1, "Activa");
            psPoliza.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.parse(nuevaFecha)));
            psPoliza.setInt(3, idPolizaSeleccionada);

            psPlan.setString(1, nuevoPlan);
            psPlan.setInt(2, maxPres);
            psPlan.setInt(3, maxRem);
            psPlan.setInt(4, maxAse);
            psPlan.setInt(5, idPolizaSeleccionada);

            int filasPoliza = psPoliza.executeUpdate();
            int filasPlan = psPlan.executeUpdate();

            System.out.println("ID póliza: " + idPolizaSeleccionada);
            System.out.println("Plan nuevo: " + nuevoPlan);
            System.out.println("Fecha nueva: " + nuevaFecha);
            System.out.println("Filas poliza: " + filasPoliza);
            System.out.println("Filas plan: " + filasPlan);

            if (filasPoliza > 0 && filasPlan > 0) {
                con.commit();
                JOptionPane.showMessageDialog(vista,
                        "Póliza renovada correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                volverAlMenu(); 
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(vista,
                        "No se pudo renovar la póliza.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        }

    } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error en BD: " + ex.getMessage(),
                    "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
}