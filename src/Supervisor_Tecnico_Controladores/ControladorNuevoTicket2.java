/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Supervisor_Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Supervisor_Tecnico_Controladores.ControladorMenuSupervisor;
import Supervisor_Tecnico_Frames.NuevoTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Conexion_BD.Conexion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

public class ControladorNuevoTicket2 implements ActionListener {
    
    private NuevoTicket2 vista2;

    public ControladorNuevoTicket2(NuevoTicket2 vista2) {
        this.vista2 = vista2;
        
        this.vista2.JBNSiguienteJLS3.addActionListener(this); // Asumiendo que es el botón Guardar
        this.vista2.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón GUARDAR
        if (e.getSource() == vista2.JBNSiguienteJLS3) {
            String empresa = vista2.JTFEmpresa.getText();
            String tipo = vista2.JTFTipo.getText();
            String descripcion = vista2.JTADescripcion.getText();
            
            // Validación
            if (empresa.isEmpty() || tipo.isEmpty() || descripcion.isEmpty() || descripcion.equals("Descripción de los servicios a realizar y detalles puntuales")) {
                JOptionPane.showMessageDialog(vista2, "Complete todos los detalles del ticket.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Aquí Query a SQL: INSERT INTO ticket (descripcionT, statusT, modalidadAtencionT, fechaCreacionT...) VALUES (...)
            try {
                Connection con = Conexion.getConexion();
                if(con != null) {
                    // String sql = "INSERT INTO ticket (...) VALUES (...)";
                    // PreparedStatement ps = con.prepareStatement(sql);
                    // ps.executeUpdate();
                    System.out.println("Guardando en BD...");
                }
            } catch (Exception ex) {
                System.out.println("Aún no hay BD conectada.");
            }

            JOptionPane.showMessageDialog(vista2, "¡Ticket creado exitosamente!");
            volverAlMenu();
        }
        
        // Botón CANCELAR
        else if (e.getSource() == vista2.JBNCancelar) {
            volverAlMenu();
        }
    }
    
    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        ControladorMenuSupervisor ctrlMenu = new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista2.dispose();
    }
}
