package Director_General_Controladores;

import Director_General_Frames.NuevoTecnico;
import Director_General_Frames.GenerarReporteporTicket;
import Director_General_Frames.Menu_Director_General;
import Director_General_Frames.GenerarReportePóliza;
import Director_General_Frames.GenerarReporteporCliente;
import Director_General_Frames.ConsultarConsumos;
import Menus_Inicio.Inicio_Sesion;
import Menus_Inicio.ControladorLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuDirector implements ActionListener {
    
    private Menu_Director_General vistaMenu;

    public ControladorMenuDirector(Menu_Director_General vistaMenu) {
        this.vistaMenu = vistaMenu;

        this.vistaMenu.JBNPersonal.addActionListener(this);
        this.vistaMenu.JBNReportes.addActionListener(this);

        this.vistaMenu.JMINuevoTecnico.addActionListener(this);
        this.vistaMenu.JMIReporteTecnico.addActionListener(this);
        this.vistaMenu.JMIReporteCliente.addActionListener(this);
        this.vistaMenu.JMIReportePoliza.addActionListener(this);
        this.vistaMenu.JMIConsumos.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // BOTÓN IZQUIERDO: Reporte por cliente
        if (e.getSource() == vistaMenu.JBNPersonal) {
            GenerarReporteporCliente vista = new GenerarReporteporCliente();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // BOTÓN DERECHO: Reporte por ticket
        else if (e.getSource() == vistaMenu.JBNReportes) {
            GenerarReporteporTicket vista = new GenerarReporteporTicket();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // MENÚ PERSONAL > NUEVO TÉCNICO
        else if (e.getSource() == vistaMenu.JMINuevoTecnico) {
            NuevoTecnico vista = new NuevoTecnico();
            new ControladorNuevoTecnico(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // MENÚ REPORTES > REPORTE TÉCNICO / TICKETS
        else if (e.getSource() == vistaMenu.JMIReporteTecnico) {
            GenerarReporteporTicket vista = new GenerarReporteporTicket();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // MENÚ REPORTES > REPORTE CLIENTE
        else if (e.getSource() == vistaMenu.JMIReporteCliente) {
            GenerarReporteporCliente vista = new GenerarReporteporCliente();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // MENÚ REPORTES > REPORTE PÓLIZA
        else if (e.getSource() == vistaMenu.JMIReportePoliza) {
            GenerarReportePóliza vista = new GenerarReportePóliza();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // MENÚ REPORTES > CONSUMOS
        else if (e.getSource() == vistaMenu.JMIConsumos) {
            ConsultarConsumos vista = new ConsultarConsumos();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // CERRAR SESIÓN
        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Inicio_Sesion login = new Inicio_Sesion();
            new ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}