/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.NuevaPoliza;
import Ejecutivo_Vinculacion_Frames.NuevaPoliza2;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorNuevaPoliza implements ActionListener {
    
    private NuevaPoliza vista1;

    public ControladorNuevaPoliza(NuevaPoliza vista1) {
        this.vista1 = vista1;
        
        // Asignamos los listeners a los botones
        this.vista1.JBNSiguiente.addActionListener(this); 
        this.vista1.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Botón SIGUIENTE 
        if (e.getSource() == vista1.JBNSiguiente) {
            
            // Valida que haya seleccionado un plan
            if (!vista1.JRBEscencial.isSelected() && !vista1.JRBProfesional.isSelected() && !vista1.JRBEmpresarial.isSelected()) {
                JOptionPane.showMessageDialog(vista1, "Por favor seleccione un plan a contratar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validacion
            NuevaPoliza2 vista2 = new NuevaPoliza2();
            new ControladorNuevaPoliza2(vista2); // siguiente controlador
            vista2.setVisible(true);
            vista1.dispose();
        }
        
        //Botón CANCELAR
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