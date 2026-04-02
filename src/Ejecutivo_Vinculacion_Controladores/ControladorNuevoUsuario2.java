package Ejecutivo_Vinculacion_Controladores;

import Ejecutivo_Vinculacion_Frames.NuevoUsuario2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorNuevoUsuario2 implements ActionListener {

    private NuevoUsuario2 vista;
    private int idCliente;
    private int idPoliza;
    private String textoVisible;

    public ControladorNuevoUsuario2(NuevoUsuario2 vista, int idCliente, int idPoliza, String textoVisible) {
        this.vista = vista;
        this.idCliente = idCliente;
        this.idPoliza = idPoliza;
        this.textoVisible = textoVisible;

        // ejemplo: mostrar la selección en un label o textfield
        // vista.JLBDatos.setText(textoVisible);

        // listeners aquí
        // vista.JBNGuardar.addActionListener(this);
        // vista.JBNCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}