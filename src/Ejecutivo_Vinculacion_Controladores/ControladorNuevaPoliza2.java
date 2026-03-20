/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.NuevaPoliza2;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevaPoliza2 implements ActionListener {
    
    private NuevaPoliza2 vista2;

    public ControladorNuevaPoliza2(NuevaPoliza2 vista2) {
        this.vista2 = vista2;
        
        // Listeners
        this.vista2.JBNRegistrar.addActionListener(this); 
        this.vista2.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón REGISTRAR
        if (e.getSource() == vista2.JBNRegistrar) {
            String empresa = vista2.JTFEmpresa.getText();
            String direccion = vista2.JTFDireccion.getText();
            String telefono = vista2.JTFTelefono.getText();
            String correo = vista2.JTFCorreo.getText();
            
            // Validación de campos
            if (empresa.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(vista2, "Por favor complete todos los datos de la póliza.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            //INSERT INTO poliza (...) VALUES (...)
            
            JOptionPane.showMessageDialog(vista2, "¡Póliza registrada exitosamente!");
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