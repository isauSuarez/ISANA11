/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Tecnico_Controladores.ControladorMisTickets;
import Tecnico_Frames.Menu_Tecnico;
import Tecnico_Frames.MisTickets;
import Menus_Inicio.Inicio_Sesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuTecnico implements ActionListener {
    private Menu_Tecnico vistaMenu;

    public ControladorMenuTecnico(Menu_Tecnico vistaMenu) {
        this.vistaMenu = vistaMenu;
        this.vistaMenu.JBNMisTickets.addActionListener(this);
        this.vistaMenu.JMIMisTickets.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaMenu.JBNMisTickets || e.getSource() == vistaMenu.JMIMisTickets) {
            MisTickets vista = new MisTickets();
            new ControladorMisTickets(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        // Lógica para cerrar sesión...
    }
}