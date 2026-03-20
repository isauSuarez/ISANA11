/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Supervisor_Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Supervisor_Tecnico_Frames.AsignarTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorAsignarTicket2 implements ActionListener {

    private AsignarTicket2 vista2;

    public ControladorAsignarTicket2(AsignarTicket2 vista2) {
        this.vista2 = vista2;
        this.vista2.JBNGuardar.addActionListener(this);
        this.vista2.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista2.JBNGuardar) {
            String tecnico = vista2.JCBTecnico.getSelectedItem().toString();
            
            // Lógica de guardado (Update en la tabla ticket)
            JOptionPane.showMessageDialog(vista2, "Ticket asignado correctamente a: " + tecnico);
            volverAlMenu();
        } 
        else if (e.getSource() == vista2.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vista2.dispose();
    }
}