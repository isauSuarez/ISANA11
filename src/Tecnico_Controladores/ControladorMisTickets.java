/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tecnico_Controladores;

/**
 *
 * @author glomy
 */

import Tecnico_Frames.MisTickets;
import Tecnico_Frames.FinalizarTicket;
import Tecnico_Frames.Menu_Tecnico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorMisTickets implements ActionListener {
    private MisTickets vista;

    public ControladorMisTickets(MisTickets vista) {
        this.vista = vista;
        this.vista.JBNSiguienteJLS2.addActionListener(this); // Botón de Presenciales
        this.vista.JBNSiguienteJLS3.addActionListener(this); // Botón de Remotos
        this.vista.JBNSiguienteJLS4.addActionListener(this); // Botón de Asesoría
        this.vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.JBNSiguienteJLS2 || e.getSource() == vista.JBNSiguienteJLS3 || e.getSource() == vista.JBNSiguienteJLS4) {
            // Aquí se pasaría el ID del ticket seleccionado a la siguiente ventana
            FinalizarTicket vistaFinalizar = new FinalizarTicket();
            new ControladorFinalizarTicket(vistaFinalizar);
            vistaFinalizar.setVisible(true);
            vista.dispose();
        } else if (e.getSource() == vista.JBNCancelar) {
            Menu_Tecnico menu = new Menu_Tecnico();
            new ControladorMenuTecnico(menu);
            menu.setVisible(true);
            vista.dispose();
        }
    }
}