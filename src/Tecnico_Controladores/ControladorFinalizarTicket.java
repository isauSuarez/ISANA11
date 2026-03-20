/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Tecnico_Controladores.ControladorMenuTecnico;
import Tecnico_Frames.FinalizarTicket;
import Tecnico_Frames.Menu_Tecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorFinalizarTicket implements ActionListener {
    private FinalizarTicket vista;

    public ControladorFinalizarTicket(FinalizarTicket vista) {
        this.vista = vista;
        this.vista.JBNSiguienteJLS3.addActionListener(this); // Botón Guardar
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteJLS3) {
            if (vista.JCBFinalizado.isSelected()) {
                // UPDATE ticket SET statusT = 'Cerrado', fechaCierreT = CURDATE() WHERE idTicket = ?
                JOptionPane.showMessageDialog(vista, "Ticket finalizado y cerrado correctamente.");
            } else {
                //UPDATE ticket SET notasTecnico = ? WHERE idTicket = ?
                JOptionPane.showMessageDialog(vista, "Notas guardadas. El ticket sigue en proceso.");
            }
            regresarAlMenu();
        } else if (e.getSource() == vista.JBNCancelar) {
            regresarAlMenu();
        }
    }

    private void regresarAlMenu() {
        Menu_Tecnico menu = new Menu_Tecnico();
        new ControladorMenuTecnico(menu);
        menu.setVisible(true);
        vista.dispose();
    }
}