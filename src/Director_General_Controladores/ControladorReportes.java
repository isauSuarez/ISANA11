/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Director_General_Controladores;

/**
 *
 * @author glomy
 */

import Director_General_Controladores.ControladorMenuDirector;
import Director_General_Frames.Menu_Director_General;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ControladorReportes implements ActionListener {
    
    private JFrame vistaReporte;
    private JButton btnRegresar;

    public ControladorReportes(JFrame vistaReporte, JButton btnRegresar) {
        this.vistaReporte = vistaReporte;
        this.btnRegresar = btnRegresar;

        this.btnRegresar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegresar) {
            Menu_Director_General menu = new Menu_Director_General();
            new ControladorMenuDirector(menu);
            menu.setVisible(true);
            vistaReporte.dispose(); // Cierra el reporte actual
        }
    }
}
