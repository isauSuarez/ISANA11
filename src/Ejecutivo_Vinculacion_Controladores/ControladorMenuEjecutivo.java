/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Menus_Inicio.ControladorLogin;
import Ejecutivo_Vinculacion_Controladores.ControladorEditarPoliza;
import Ejecutivo_Vinculacion_Controladores.ControladorModificarCliente;
import Ejecutivo_Vinculacion_Controladores.ControladorModificarUsuario;
import Ejecutivo_Vinculacion_Controladores.ControladorNuevoCliente;
import Ejecutivo_Vinculacion_Controladores.ControladorNuevaPoliza;
import Ejecutivo_Vinculacion_Controladores.ControladorNuevoUsuario;
import Ejecutivo_Vinculacion_Controladores.ControladorRenovarPoliza;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import Ejecutivo_Vinculacion_Frames.CancelarPolizaN;
import Ejecutivo_Vinculacion_Frames.ModificarCliente;
import Ejecutivo_Vinculacion_Frames.RenovarPoliza;
import Ejecutivo_Vinculacion_Frames.NuevoCliente;
import Ejecutivo_Vinculacion_Frames.EditarPoliza;
import Ejecutivo_Vinculacion_Frames.ModificarUsuario;
import Ejecutivo_Vinculacion_Frames.Nuevo_Usuario;
import Menus_Inicio.Inicio_Sesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMenuEjecutivo implements ActionListener {
    
    private MenuEjecutivoVinculacion2 vistaMenu;

    public ControladorMenuEjecutivo(MenuEjecutivoVinculacion2 vistaMenu) {
        this.vistaMenu = vistaMenu;
        
        // Listeners para los botones grandes
        this.vistaMenu.JBNNuevoCliente.addActionListener(this);
        this.vistaMenu.JBNNuevaPoliza.addActionListener(this);
        
        // Listeners para los ítems del menú superior
        this.vistaMenu.JMINuevoCliente.addActionListener(this);
        this.vistaMenu.JMIModificarCliente.addActionListener(this);
        this.vistaMenu.JMINuevaPoliza.addActionListener(this);
        this.vistaMenu.JMIEditarPoliza.addActionListener(this);
        this.vistaMenu.JMICancelarPoliza.addActionListener(this);
        this.vistaMenu.JMIRenovarPoliza.addActionListener(this);
        this.vistaMenu.JMICerrarSesion.addActionListener(this);
        this.vistaMenu.JMINuevoUsuario.addActionListener(this);
        this.vistaMenu.JMIEditarUsuario.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // FLUJO NUEVO CLIENTE
        if (e.getSource() == vistaMenu.JBNNuevoCliente || e.getSource() == vistaMenu.JMINuevoCliente) {
            NuevoCliente vista = new NuevoCliente();
            new ControladorNuevoCliente(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // FLUJO NUEVA PÓLIZA 
        else if (e.getSource() == vistaMenu.JBNNuevaPoliza || e.getSource() == vistaMenu.JMINuevaPoliza) {
            NuevaPoliza vista = new NuevaPoliza();
            new ControladorNuevaPoliza(vista);
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // FLUJO NUEVO USUARIO
        else if (e.getSource() == vistaMenu.JMINuevoUsuario) { 
            Nuevo_Usuario vista = new Nuevo_Usuario();
            new ControladorNuevoUsuario(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        
        // FLUJOS RESTANTES DE LA BARRA
        else if (e.getSource() == vistaMenu.JMIModificarCliente) {
            ModificarCliente vista = new ModificarCliente();
            new ControladorModificarCliente(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JMIEditarPoliza) {
            EditarPoliza vista = new EditarPoliza();
            new ControladorEditarPoliza(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JMIRenovarPoliza) {
            RenovarPoliza vista = new RenovarPoliza();
            new ControladorRenovarPoliza(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }
        else if (e.getSource() == vistaMenu.JMICancelarPoliza) {
            CancelarPolizaN vista = new CancelarPolizaN();
            new ControladorCancelarPoliza(vista); // controlador
            vista.setVisible(true);
            vistaMenu.dispose();
        }

        // FLUJO CERRAR SESIÓN
        else if (e.getSource() == vistaMenu.JMICerrarSesion) {
            Menus_Inicio.Inicio_Sesion vistaLogin = new Menus_Inicio.Inicio_Sesion();
            new ControladorLogin(vistaLogin); // controlador
            vistaLogin.setVisible(true);
            vistaMenu.dispose();
        }
        
        else if (e.getSource() == vistaMenu.JMIEditarUsuario){
                    ModificarUsuario vista = new ModificarUsuario();
                new ControladorModificarUsuario(vista);
                vista.setVisible(true);
                vistaMenu.dispose();
        }
        
        
        
        
    }
}