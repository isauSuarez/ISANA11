package Supervisor_Tecnico_Controladores;

import Menus_Inicio.ControladorLogin;
import Menus_Inicio.Inicio_Sesion;
import Supervisor_Tecnico_Frames.MenuSupervisorTecnico;
import Supervisor_Tecnico_Frames.ModificarTecnico2;
import Supervisor_Tecnico_Frames.ModificarTicket;
import Supervisor_Tecnico_Frames.NuevoTicket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuSupervisor implements ActionListener {
    
    private MenuSupervisorTecnico vistaMenu;

    public ControladorMenuSupervisor(MenuSupervisorTecnico vistaMenu) {
        this.vistaMenu = vistaMenu;

        this.vistaMenu.JBNNuevoTicket.addActionListener(this);
        this.vistaMenu.JMINuevoTicket.addActionListener(this);
        this.vistaMenu.JMIEditarTicket.addActionListener(this);
        this.vistaMenu.JMIEditarTecnico.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vistaMenu.JBNNuevoTicket || e.getSource() == vistaMenu.JMINuevoTicket) {
            NuevoTicket vista = new NuevoTicket();
            new ControladorNuevoTicket(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMIEditarTicket) {
            ModificarTicket vista = new ModificarTicket();
            new ControladorModificarTicket(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMIEditarTecnico) {
            ModificarTecnico2 vista = new ModificarTecnico2();
            new ControladorModificarTecnico2(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Inicio_Sesion login = new Inicio_Sesion();
            new ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}