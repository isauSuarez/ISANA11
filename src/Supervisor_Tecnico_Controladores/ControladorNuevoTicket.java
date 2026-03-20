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
import Supervisor_Tecnico_Frames.NuevoTicket;
import Supervisor_Tecnico_Frames.NuevoTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevoTicket implements ActionListener {
    
    private NuevoTicket vista1;

    public ControladorNuevoTicket(NuevoTicket vista1) {
        this.vista1 = vista1;
        
        // Ponemos a escuchar los botones (Asumiendo que JBNSiguienteJLS3 es tu botón "Siguiente")
        this.vista1.JBNSiguienteJLS3.addActionListener(this); 
        this.vista1.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón SIGUIENTE
        if (e.getSource() == vista1.JBNSiguienteJLS3) {
            String numPoliza = vista1.JTFNumeroDePoliza.getText();
            
            if (numPoliza.equals("Numero de poliza") || numPoliza.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista1, "Por favor, ingrese un número de póliza válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            NuevoTicket2 vista2 = new NuevoTicket2();
            ControladorNuevoTicket2 ctrl2 = new ControladorNuevoTicket2(vista2); // Conecta su controlador
            vista2.setVisible(true);
            vista1.dispose(); // Cierra la ventana 1
        }
        
        // Botón CANCELAR
        else if (e.getSource() == vista1.JBNCancelar) {
            volverAlMenu();
        }
    }
    
    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        ControladorMenuSupervisor ctrlMenu = new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista1.dispose();
    }
}