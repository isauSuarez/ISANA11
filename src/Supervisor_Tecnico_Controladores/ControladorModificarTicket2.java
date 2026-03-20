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
import Supervisor_Tecnico_Frames.ModificarTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorModificarTicket2 implements ActionListener {
    private ModificarTicket2 vistaEdicion;

    public ControladorModificarTicket2(ModificarTicket2 vistaEdicion) {
        this.vistaEdicion = vistaEdicion;
        this.vistaEdicion.JBNSiguienteJLS3.addActionListener(this); // Botón Guardar
        this.vistaEdicion.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaEdicion.JBNSiguienteJLS3) {
            // Update en DB (SET statusT = 'CERRADO' si el JCheckBox está seleccionado)
            JOptionPane.showMessageDialog(vistaEdicion, "Ticket actualizado exitosamente.");
            volverAlMenu();
        } else if (e.getSource() == vistaEdicion.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vistaEdicion.dispose();
    }
}