/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Supervisor_Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Supervisor_Tecnico_Frames.AsignarTicket;
import Supervisor_Tecnico_Frames.AsignarTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorAsignarTicket implements ActionListener {

    private AsignarTicket vista1;

    public ControladorAsignarTicket(AsignarTicket vista1) {
        this.vista1 = vista1;
        
        // Listeners para los dos caminos: escribir número o seleccionar de lista
        this.vista1.JBNSiguienteTF.addActionListener(this);
        this.vista1.JBNSiguienteCB.addActionListener(this);
        this.vista1.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista1.JBNSiguienteTF || e.getSource() == vista1.JBNSiguienteCB) {
            
            // Aquí iría la lógica para buscar si el ticket existe en la BD pero por ahora, validamos que no esté vacío xd
            if (vista1.JTFNumeroTicket.getText().equals("Numero de ticket") && vista1.JCBTickets.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(vista1, "Por favor seleccione o ingrese un ticket.");
                return;
            }

            AsignarTicket2 vista2 = new AsignarTicket2();
            new ControladorAsignarTicket2(vista2); // Conectamos controlador
            vista2.setVisible(true);
            vista1.dispose();
        } 
        else if (e.getSource() == vista1.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista1.dispose();
    }
}