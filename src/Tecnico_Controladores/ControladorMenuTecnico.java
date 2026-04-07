package Tecnico_Controladores;

import Menus_Inicio.ControladorLogin;
import Menus_Inicio.Inicio_Sesion;
import Menus_Inicio.SesionEmpleado;
import Tecnico_Frames.Menu_Tecnico;
import Tecnico_Frames.MisTickets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuTecnico implements ActionListener {

    private Menu_Tecnico vistaMenu;
    private int idEmpleado;

    public ControladorMenuTecnico(Menu_Tecnico vistaMenu, int idEmpleado) {
        this.vistaMenu = vistaMenu;
        this.idEmpleado = idEmpleado;

        this.vistaMenu.JBNMisTickets.addActionListener(this);
        this.vistaMenu.JMIMisTickets.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
    }

    // Constructor adicional para no romper llamadas viejas
    public ControladorMenuTecnico(Menu_Tecnico vistaMenu) {
        this(vistaMenu, SesionEmpleado.getIdEmpleado());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaMenu.JBNMisTickets || e.getSource() == vistaMenu.JMIMisTickets) {
            MisTickets vista = new MisTickets();
            new ControladorMisTickets(vista, idEmpleado);
            vista.setVisible(true);
            vistaMenu.dispose();
        } 
        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            SesionEmpleado.cerrarSesion();
            Inicio_Sesion login = new Inicio_Sesion();
            new ControladorLogin(login);
            login.setVisible(true);
            vistaMenu.dispose();
        }
    }
}