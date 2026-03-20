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
import Supervisor_Tecnico_Frames.ModificarTecnico2;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorModificarTecnico2 implements ActionListener {
    private ModificarTecnico2 vistaEdicion;

    public ControladorModificarTecnico2(ModificarTecnico2 vistaEdicion) {
        this.vistaEdicion = vistaEdicion;
        this.vistaEdicion.JBNGuardar.addActionListener(this);
        this.vistaEdicion.JBNCancelar.addActionListener(this); // Botón Cancelar
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaEdicion.JBNGuardar) {
            // UPDATE empleado SET nombreEmp = ?, telefonoEmp = ?...
            JOptionPane.showMessageDialog(vistaEdicion, "Información del técnico actualizada.");
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