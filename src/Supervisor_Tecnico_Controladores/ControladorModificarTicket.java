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
import Supervisor_Tecnico_Frames.ModificarTicket;
import Supervisor_Tecnico_Frames.ModificarTicket2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorModificarTicket implements ActionListener {
    private ModificarTicket vistaBuscador;

    public ControladorModificarTicket(ModificarTicket vistaBuscador) {
        this.vistaBuscador = vistaBuscador;
        this.vistaBuscador.JBNSiguienteJLS2.addActionListener(this); // Botón para el JTF
        this.vistaBuscador.JBNSiguienteJLS3.addActionListener(this); // Botón para la JList
        this.vistaBuscador.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaBuscador.JBNSiguienteJLS2 || e.getSource() == vistaBuscador.JBNSiguienteJLS3) {
            // Aquí se validaría el ticket en la BD antes de pasar
            ModificarTicket2 vistaEdicion = new ModificarTicket2();
            new ControladorModificarTicket2(vistaEdicion);
            vistaEdicion.setVisible(true);
            vistaBuscador.dispose();
        } else if (e.getSource() == vistaBuscador.JBNCancelar) {
            volverAlMenu();
        }
    }

    private void volverAlMenu() {
        MenuSupervisorTecnico menu = new MenuSupervisorTecnico();
        new ControladorMenuSupervisor(menu);
        menu.setVisible(true);
        vistaBuscador.dispose();
    }
}