/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevoUsuario2 implements ActionListener {
    
    private NuevoUsuario2 vista2;

    public ControladorNuevoUsuario2(NuevoUsuario2 vista2) {
        this.vista2 = vista2;
        
        // Listeners
        this.vista2.JBNGuardar.addActionListener(this); 
        this.vista2.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón GUARDAR
        if (e.getSource() == vista2.JBNGuardar) {
            String nombre = vista2.JTFNombre.getText();
            String locacion = vista2.JTFLocacion.getText();
            String telefono = vista2.JTFTelefono.getText();
            String correo = vista2.JTFCorreo.getText();
            
            // Validación
            if (nombre.isEmpty() || locacion.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(vista2, "Por favor llene todos los datos del usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            //INSERT INTO usuario (nombre, locacion, telefono, correo, idPoliza) VALUES (...)
            JOptionPane.showMessageDialog(vista2, "¡Usuario registrado y enlazado a la póliza exitosamente!");
            volverAlMenu();
        }
        
        // Botón CANCELAR
        else if (e.getSource() == vista2.JBNCancelar) {
            volverAlMenu();
        }
    }
    
    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista2.dispose();
    }
}