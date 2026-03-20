/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Controladores.ControladorNuevoUsuario2;
import Ejecutivo_Vinculacion_Frames.Nuevo_Usuario;
import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevoUsuario implements ActionListener {
    
    private Nuevo_Usuario vista1;

    public ControladorNuevoUsuario(Nuevo_Usuario vista1) {
        this.vista1 = vista1;
        
        // Listeners
        this.vista1.JBNSiguiente.addActionListener(this);
        this.vista1.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón SIGUIENTE
        if (e.getSource() == vista1.JBNSiguiente) {

            if (vista1.JCBClientes.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(vista1, "Por favor seleccione un cliente registrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            NuevoUsuario2 vista2 = new NuevoUsuario2();
            new ControladorNuevoUsuario2(vista2); // controlador
            vista2.setVisible(true);
            vista1.dispose();
        }
        
        // Botón CANCELAR
        else if (e.getSource() == vista1.JBNCancelar) {
            volverAlMenu();
        }
    }
    
    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista1.dispose();
    }
}