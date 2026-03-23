package Director_General_Controladores;

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

        if (e.getSource() == vistaMenu.JBNPersonal || e.getSource() == vistaMenu.JMINuevoTecnico) {
            NuevoTecnico vista = new NuevoTecnico();
            new ControladorNuevoTecnico(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMIReportePoliza) {
            GenerarReportePóliza vista = new GenerarReportePóliza();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMIReporteTecnico) {
            GenerarReporteporTécnico vista = new GenerarReporteporTécnico();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

            else if (e.getSource() == vistaMenu.JMIReporteCliente) {
            GenerarReporteporCliente vista = new GenerarReporteporCliente();
            vista.setVisible(true);
            vistaMenu.dispose();
}

        else if (e.getSource() == vistaMenu.JBNReportes || e.getSource() == vistaMenu.JMIConsumos) {
            ConsultarConsumos vista = new ConsultarConsumos();
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Inicio_Sesion login = new Inicio_Sesion();
            new Menus_Inicio.ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}