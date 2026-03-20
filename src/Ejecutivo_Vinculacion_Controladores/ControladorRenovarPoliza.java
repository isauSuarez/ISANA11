/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.RenovarPoliza;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorRenovarPoliza implements ActionListener {
    
    private RenovarPoliza vista;

    public ControladorRenovarPoliza(RenovarPoliza vista) {
        this.vista = vista;
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            String numPoliza = vista.JTFNumPoliza.getText();
            
            if (numPoliza.isEmpty() || numPoliza.equals("Numero de poliza")) {
                JOptionPane.showMessageDialog(vista, "Ingrese un número de póliza válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // UPDATE poliza SET plan = ?, fechaVencimiento = ? WHERE idPoliza = ?
            JOptionPane.showMessageDialog(vista, "La póliza ha sido renovada exitosamente.");
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