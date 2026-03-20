package Ejecutivo_Vinculacion_Controladores;

import Conexion_BD.Conexion;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class ControladorNuevaPoliza2 implements ActionListener {

    private NuevaPoliza2 vista;
    private int idCliente;
    private String nombreCliente;
    private String planSeleccionado;

    public ControladorNuevaPoliza2(NuevaPoliza2 vista, int idCliente, String nombreCliente, String planSeleccionado) {
        this.vista = vista;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.planSeleccionado = planSeleccionado;

        this.vista.JBNRegistrar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);

        cargarFechasVencimiento();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNRegistrar) {
            registrarPoliza();
        } else if (e.getSource() == vista.JBNCancelar) {
            volverAPaso1();
        }
    }

    private void cargarFechasVencimiento() {
        vista.JCBFechaVencimiento.removeAllItems();
        vista.JCBFechaVencimiento.addItem("Fecha");

        LocalDate hoy = LocalDate.now();

        for (int i = 1; i <= 12; i++) {
            vista.JCBFechaVencimiento.addItem(hoy.plusMonths(i).toString());
        }
    }

    private void registrarPoliza() {
        String empresa = vista.JTFEmpresa.getText().trim();
        String direccion = vista.JTFDireccion.getText().trim();
        String telefono = vista.JTFTelefono.getText().trim();
        String correo = vista.JTFCorreo.getText().trim();
        String fechaVencimientoTexto = vista.JCBFechaVencimiento.getSelectedItem().toString();

        if (empresa.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || correo.isEmpty()
                || fechaVencimientoTexto.equals("Fecha")) {
            JOptionPane.showMessageDialog(vista,
                    "Llene todos los campos.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Temporal: mientras no manejes sesión del empleado logueado
        int idEmpleado = 1;

        String sql = "INSERT INTO poliza "
                + "(idCliente, idEmpleado, nombreEmpresaP, direccionServicioP, estadoP, tipoPlanP, fechaInicioP, fechaVencimientoP) "
                + "VALUES (?, ?, ?, ?, ?, ?, CURDATE(), ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("Conexión correcta. Registrando póliza...");

            ps.setInt(1, idCliente);
            ps.setInt(2, idEmpleado);
            ps.setString(3, empresa);
            ps.setString(4, direccion);
            ps.setString(5, "Activa");
            ps.setString(6, planSeleccionado);
            ps.setDate(7, java.sql.Date.valueOf(LocalDate.parse(fechaVencimientoTexto)));

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Póliza registrada correctamente.");
                System.out.println("Cliente: " + nombreCliente);
                System.out.println("Empresa: " + empresa);
                System.out.println("Plan: " + planSeleccionado);

                JOptionPane.showMessageDialog(vista,
                        "Póliza registrada correctamente.");

                limpiarCampos();
                volverAPaso1();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar la póliza.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar póliza: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error con la BD: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        vista.JTFEmpresa.setText("");
        vista.JTFDireccion.setText("");
        vista.JTFTelefono.setText("");
        vista.JTFCorreo.setText("");
        vista.JCBFechaVencimiento.setSelectedIndex(0);
    }

    private void volverAPaso1() {
        NuevaPoliza vistaPaso1 = new NuevaPoliza();
        new ControladorNuevaPoliza(vistaPaso1);
        vistaPaso1.setVisible(true);
        vista.dispose();
    }
}