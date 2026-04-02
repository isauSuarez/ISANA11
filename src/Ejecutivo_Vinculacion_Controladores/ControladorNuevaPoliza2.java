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
import java.sql.ResultSet;
import java.sql.Statement;

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

    int idEmpleado = 1;

    int maxPres = 0;
    int maxRem = 0;
    int maxAse = 0;
    String nombrePlan = planSeleccionado;

    switch (planSeleccionado.toLowerCase()) {
        case "esencial":
            maxPres = 5;
            maxRem = 10;
            maxAse = 10;
            break;
        case "profesional":
            maxPres = 15;
            maxRem = 25;
            maxAse = 15;
            break;
        case "empresarial":
            maxPres = 30;
            maxRem = 30;
            maxAse = 25;
            break;
        default:
            JOptionPane.showMessageDialog(vista,
                    "Plan no válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
    }

    String sqlPoliza = "INSERT INTO poliza "
            + "(idCliente, idEmpleado, nombreEmpresaP, direccionServicioP, correoP, telefonoP, estadoP, fechaInicioP, fechaVencimientoP) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(), ?)";

    String sqlPlan = "INSERT INTO plan "
            + "(idPoliza, nombreP, maxPres, maxRem, maxAse) "
            + "VALUES (?, ?, ?, ?, ?)";

    try (Connection con = Conexion.getConexion()) {
        con.setAutoCommit(false);

        try (PreparedStatement psPoliza = con.prepareStatement(sqlPoliza, java.sql.Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psPlan = con.prepareStatement(sqlPlan)) {

            psPoliza.setInt(1, idCliente);
            psPoliza.setInt(2, idEmpleado);
            psPoliza.setString(3, empresa);
            psPoliza.setString(4, direccion);
            psPoliza.setString(5, correo);
            psPoliza.setString(6, telefono);
            psPoliza.setString(7, "Activa");
            psPoliza.setDate(8, java.sql.Date.valueOf(LocalDate.parse(fechaVencimientoTexto)));

            int filasPoliza = psPoliza.executeUpdate();

            if (filasPoliza == 0) {
                con.rollback();
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar la póliza.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idPolizaGenerada = 0;

            try (ResultSet rs = psPoliza.getGeneratedKeys()) {
                if (rs.next()) {
                    idPolizaGenerada = rs.getInt(1);
                } else {
                    con.rollback();
                    JOptionPane.showMessageDialog(vista,
                            "No se pudo obtener el ID de la póliza.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            psPlan.setInt(1, idPolizaGenerada);
            psPlan.setString(2, nombrePlan);
            psPlan.setInt(3, maxPres);
            psPlan.setInt(4, maxRem);
            psPlan.setInt(5, maxAse);

            int filasPlan = psPlan.executeUpdate();

            if (filasPlan == 0) {
                con.rollback();
                JOptionPane.showMessageDialog(vista,
                        "No se pudo registrar el plan de la póliza.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            con.commit();

            System.out.println("Póliza registrada correctamente.");
            System.out.println("ID póliza: " + idPolizaGenerada);
            System.out.println("Cliente: " + nombreCliente);
            System.out.println("Empresa: " + empresa);
            System.out.println("Plan: " + nombrePlan);

            JOptionPane.showMessageDialog(vista,
                    "Póliza registrada correctamente.");

            limpiarCampos();
            volverAPaso1();

        } catch (SQLException ex) {
            con.rollback();
            throw ex;
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