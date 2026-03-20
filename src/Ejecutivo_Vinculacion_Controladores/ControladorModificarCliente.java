/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Ejecutivo_Vinculacion_Controladores;

/**
 *
 * @author glomy
 */

import Ejecutivo_Vinculacion_Frames.ModificarCliente;
import Ejecutivo_Vinculacion_Frames.MenuEjecutivoVinculacion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorModificarCliente implements ActionListener {
    
    private ModificarCliente vista;

    public ControladorModificarCliente(ModificarCliente vista) {
        this.vista = vista;
        this.vista.JBNGuardar.addActionListener(this);
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNGuardar) {
            // UPDATE cliente SET nombreC = ?, apellidoC = ?... WHERE numPoliza = ?
            JOptionPane.showMessageDialog(vista, "Datos del cliente actualizados correctamente.");
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