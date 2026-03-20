/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.NuevoCliente;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevoCliente implements ActionListener {
    
    private NuevoCliente vista;

    public ControladorNuevoCliente(NuevoCliente vista) {
        this.vista = vista;
        
        // Listeners
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vista.JBNGuardar) {
            String nombre = vista.JTFNombre.getText();
            String apellido = vista.JTFApellido.getText();
            String telefono = vista.JTFTelefono.getText();
            String correo = vista.JTFCorreo.getText();
            
            // Validación de campos vacíos
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Aquí iría el INSERT INTO cliente (nombreC, apellidoC, telefonoC, correoC) VALUES (...)
            
            JOptionPane.showMessageDialog(vista, "Cliente registrado exitosamente.");
            volverAlMenu();
        } 
        
        else if (e.getSource() == vista.JBNCancelar) {
            volverAlMenu();
        }
    }
    
    private void volverAlMenu() {
        MenuEjecutivoVinculacion2 menu = new MenuEjecutivoVinculacion2();
        new ControladorMenuEjecutivo(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}