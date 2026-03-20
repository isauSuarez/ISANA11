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
import Supervisor_Tecnico_Frames.ModificarTecnico;
import Supervisor_Tecnico_Frames.ModificarTecnico2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorModificarTecnico implements ActionListener {
    private ModificarTecnico vistaBuscador;

    public ControladorModificarTecnico(ModificarTecnico vistaBuscador) {
        this.vistaBuscador = vistaBuscador;
        this.vistaBuscador.JBNSiguienteTF.addActionListener(this);
        this.vistaBuscador.JBNSiguienteJLS.addActionListener(this);
        this.vistaBuscador.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaBuscador.JBNSiguienteTF || e.getSource() == vistaBuscador.JBNSiguienteJLS) {
            ModificarTecnico2 vistaEdicion = new ModificarTecnico2();
            new ControladorModificarTecnico2(vistaEdicion);
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