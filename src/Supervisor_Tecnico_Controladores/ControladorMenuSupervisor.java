/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Supervisor_Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Menus_Inicio.ControladorLogin;
import Supervisor_Tecnico_Controladores.ControladorAsignarTicket;
import Supervisor_Tecnico_Frames.AsignarTicket;
import Supervisor_Tecnico_Frames.ModificarTicket;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.ModificarTecnico;
import Supervisor_Tecnico_Frames.NuevoTicket;
import Menus_Inicio.Inicio_Sesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControladorMenuSupervisor implements ActionListener {
    
    private MenuSupervisorTecnico vistaMenu;

    // Constructor
    public ControladorMenuSupervisor(MenuSupervisorTecnico vistaMenu) {
        this.vistaMenu = vistaMenu;
        
        // listener botones grandes
        this.vistaMenu.JBNNuevoTicket.addActionListener(this);
        this.vistaMenu.JBNAsignarTicket.addActionListener(this);
        
        // listener a las opciones del menú superior
        this.vistaMenu.JMINuevoTicket.addActionListener(this);   // Item Nuevo Ticket
        this.vistaMenu.JMIAsignarTicket.addActionListener(this); // Item Asignar Ticket
        this.vistaMenu.JMIEditarTicket.addActionListener(this);
        this.vistaMenu.JMIEditarTecnico.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Flujos NUEVO TICKET
        System.out.println("Clic detectado en: " + e.getSource().toString());

        if (e.getSource() == vistaMenu.JBNNuevoTicket || e.getSource() == vistaMenu.JMINuevoTicket) {
            System.out.println("Abriendo Nuevo Ticket...");
            NuevoTicket vistaNuevoTicket = new NuevoTicket();
            ControladorNuevoTicket ctrl = new ControladorNuevoTicket(vistaNuevoTicket);
            vistaNuevoTicket.setVisible(true);
            vistaMenu.dispose();
    }

        
        // Flujos ASIGNAR TICKET
        else if (e.getSource() == vistaMenu.JBNAsignarTicket || e.getSource() == vistaMenu.JMIAsignarTicket) {
            AsignarTicket vistaAsignarTicket = new AsignarTicket();
            new ControladorAsignarTicket(vistaAsignarTicket); 
            vistaAsignarTicket.setVisible(true);
            vistaMenu.dispose();
        }
        
        // Flujos EDITAR TICKET
        else if (e.getSource() == vistaMenu.JMIEditarTicket) {
            ModificarTicket vista = new ModificarTicket();
            new ControladorModificarTicket(vista); // Conecta el controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // Flujos EDITAR TÉCNICO
        else if (e.getSource() == vistaMenu.JMIEditarTecnico) {
            ModificarTecnico vista = new ModificarTecnico();
            new ControladorModificarTecnico(vista); // Conecta el controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // Flujo CERRAR SESIÓN
        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Inicio_Sesion login = new Inicio_Sesion();
            ControladorLogin ctrlLogin = new ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}