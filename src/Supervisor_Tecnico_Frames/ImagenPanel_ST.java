/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Supervisor_Tecnico_Frames;

/**
 *
 * @author glomy
 */
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagenPanel_ST extends JPanel {
    
    private Image imagen;

    public ImagenPanel_ST(String rutaImagen) {
        // Carga la imagen usando la ruta en el Custom Code
        this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            // Dibuja la imagen ajustándola al tamaño exacto
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}