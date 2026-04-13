package Director_General_Controladores;

import Conexion_BD.Conexion;
import Director_General_Frames.ConsultarConsumos;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReporteConsumos implements ActionListener {

    private ConsultarConsumos vista;

    public ControladorReporteConsumos(ConsultarConsumos vista) {
        this.vista = vista;

        this.vista.JBNAplicarFiltros3.addActionListener(this);
        this.vista.JBNLimpiarFiltros3.addActionListener(this);
        this.vista.JBNRegresar.addActionListener(this);

        cargarReporteConsumos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNAplicarFiltros3) {
            cargarReporteConsumos();
        } else if (e.getSource() == vista.JBNLimpiarFiltros3) {
            limpiarFiltros();
            cargarReporteConsumos();
        } else if (e.getSource() == vista.JBNRegresar) {
            volverAlMenu();
        }
    }

    private void cargarReporteConsumos() {
        String[] columnas = {
            "Cliente", "Empresa", "Tipo de Plan", "Consumo total", "% de uso"
        };

        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sqlBase =
            "SELECT cliente, empresa, tipoPlan, consumoTotal, porcentajeUso " +
            "FROM vista_consumo_clientes ";

        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        StringBuilder order = new StringBuilder();
        ArrayList<String> parametros = new ArrayList<>();

        // -------- FILTRO POR PLAN --------
        ArrayList<String> planes = new ArrayList<>();
        if (vista.JCBEsencial3.isSelected()) planes.add("Esencial");
        if (vista.JCBProfesional3.isSelected()) planes.add("Profesional");
        if (vista.JCBEmpresarial3.isSelected()) planes.add("Empresarial");

        if (!planes.isEmpty()) {
            where.append("AND tipoPlan IN (");
            for (int i = 0; i < planes.size(); i++) {
                where.append("?");
                if (i < planes.size() - 1) {
                    where.append(", ");
                }
                parametros.add(planes.get(i));
            }
            where.append(") ");
        }

        // -------- BÚSQUEDA --------
        String textoBusqueda = vista.JTFBuscador3.getText().trim();
        String tipoBusqueda = vista.JCBSeleccionador3.getSelectedItem().toString();

        if (!textoBusqueda.isEmpty()
                && !tipoBusqueda.equalsIgnoreCase("Tipo de Busqueda")
                && !tipoBusqueda.equalsIgnoreCase("Tipo de Búsqueda")) {

            switch (tipoBusqueda) {
                case "Cliente":
                    where.append("AND cliente LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;

                case "Empresa":
                    where.append("AND empresa LIKE ? ");
                    parametros.add("%" + textoBusqueda + "%");
                    break;

            }
        }

        // -------- ORGANIZAR --------
        String organizador = vista.JCBrganizador.getSelectedItem().toString();

        switch (organizador) {
            case "Mayor consumo total":
                order.append("ORDER BY consumoTotal DESC, porcentajeUso DESC ");
                break;

            case "Menor consumo total":
                order.append("ORDER BY consumoTotal ASC, porcentajeUso ASC ");
                break;

            case "Mayor % de uso":
                order.append("ORDER BY porcentajeUso DESC, consumoTotal DESC ");
                break;

            case "Menor % de uso":
                order.append("ORDER BY porcentajeUso ASC, consumoTotal ASC ");
                break;

            default:
                order.append("ORDER BY cliente ASC ");
                break;
        }

        String sqlFinal = sqlBase + where.toString() + order.toString();
        System.out.println("SQL REPORTE CONSUMOS: " + sqlFinal);

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sqlFinal)) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getString("cliente"),
                        rs.getString("empresa"),
                        rs.getString("tipoPlan"),
                        rs.getInt("consumoTotal"),
                        rs.getDouble("porcentajeUso") + "%"
                    };
                    modelo.addRow(fila);
                }
            }

            vista.JTBConsumos.setModel(modelo);
            System.out.println("Reporte de consumos cargado correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error al cargar reporte de consumos: " + ex.getMessage());
            JOptionPane.showMessageDialog(vista,
                    "Hubo un error al cargar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFiltros() {
        vista.JCBEsencial3.setSelected(false);
        vista.JCBProfesional3.setSelected(false);
        vista.JCBEmpresarial3.setSelected(false);

        vista.JTFBuscador3.setText("");
        vista.JCBSeleccionador3.setSelectedIndex(0);
        vista.JCBrganizador.setSelectedIndex(0);
    }

    private void volverAlMenu() {
        Menu_Director_General menu = new Menu_Director_General();
        new ControladorMenuDirector(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}