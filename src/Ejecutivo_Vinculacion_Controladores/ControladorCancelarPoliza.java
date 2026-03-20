/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Controladores.ControladorMenuEjecutivo;
import Ejecutivo_Vinculacion_Frames.CancelarPolizaN;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorCancelarPoliza implements ActionListener {
    
    private CancelarPolizaN vista;

    public ControladorCancelarPoliza(CancelarPolizaN vista) {
        this.vista = vista;
        this.vista.JBNConfirmar.addActionListener(this); //
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNConfirmar) {
            String numPoliza = vista.JTFNumPoliza.getText();
            
            if (numPoliza.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Ingrese el número de póliza a cancelar.");
                return;
            }
            
            // Cuadro de confirmación de seguridad
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Está completamente seguro de cancelar la póliza " + numPoliza + "?", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                // UPDATE poliza SET estado = 'Cancelada' WHERE idPoliza = ?
                JOptionPane.showMessageDialog(vista, "Póliza cancelada del sistema.");
                volverAlMenu();
            }
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