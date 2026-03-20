/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Director_General_Controladores;

/**
 *
 * @author glomy
 */

import Director_General_Frames.NuevoTecnico;
import Director_General_Frames.GenerarReporteporTécnico;
import Director_General_Frames.Menu_Director_General;
import Director_General_Frames.GenerarReportePóliza;
import Director_General_Frames.GenerarReporteporCliente;
import Director_General_Frames.ConsultarConsumos;
import Menus_Inicio.Inicio_Sesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuDirector implements ActionListener {
    
    private Menu_Director_General vistaMenu;

    public ControladorMenuDirector(Menu_Director_General vistaMenu) {
        this.vistaMenu = vistaMenu;
        
        // Listeners Botones Grandes
        this.vistaMenu.JBNPersonal.addActionListener(this);
        this.vistaMenu.JBNReportes.addActionListener(this);
        
        // Listeners Ítems del Menú Superior
        this.vistaMenu.JMINuevoTecnico.addActionListener(this);
        this.vistaMenu.JMIReporteTecnico.addActionListener(this);
        this.vistaMenu.JMIReporteCliente.addActionListener(this);
        this.vistaMenu.JMIReportePoliza.addActionListener(this);
        this.vistaMenu.JMIConsumos.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // FLUJO NUEVO TÉCNICO
        if (e.getSource() == vistaMenu.JBNPersonal || e.getSource() == vistaMenu.JMINuevoTecnico) {
            NuevoTecnico vista = new NuevoTecnico();
            new ControladorNuevoTecnico(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // FLUJO REPORTES
        else if (e.getSource() == vistaMenu.JMIReporteTecnico) {
            GenerarReporteporTécnico vista = new GenerarReporteporTécnico();
            new ControladorReportes(vista, vista.JBNRegresar); // Controlador genérico para los reportes
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JMIReporteCliente) {
            GenerarReporteporCliente vista = new GenerarReporteporCliente();
            new ControladorReportes(vista, vista.JBNRegresar);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JMIReportePoliza) {
            GenerarReportePóliza vista = new GenerarReportePóliza();
            new ControladorReportes(vista, vista.JBNRegresar);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JBNReportes || e.getSource() == vistaMenu.JMIConsumos) {
            ConsultarConsumos vista = new ConsultarConsumos();
            new ControladorReportes(vista, vista.JBNRegresar);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // FLUJO CERRAR SESIÓN
        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Inicio_Sesion login = new Inicio_Sesion();
            new Menus_Inicio.ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}