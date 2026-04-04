/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Menus_Inicio;

/**
 *
 * @author glomy
 */
public class Menu_Carga extends javax.swing.JFrame {

    /**
     * Creates new form Menu_Carga
     */
    public Menu_Carga() {
        javax.swing.UIManager.put("nimbusOrange", new java.awt.Color(39, 174, 96)); //este fuerza el color verde de la barra
        
        initComponents();
                this.setSize(500, 350); // Fuerza el tamaño exacto
        this.setLocationRelativeTo(null); // Centra la ventana en tu monitor
        iniciarCarga();
    }

    private void iniciarCarga() {
        javax.swing.Timer timer = new javax.swing.Timer(30, new java.awt.event.ActionListener() {
            int progreso = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                progreso++;
                pgbCarga.setValue(progreso); //actualiza la barra de carga

                if(progreso == 30){
                    lblEstadoCarga.setText("Cargando módulos...");
                } else if (progreso == 70) {
                    lblEstadoCarga.setText("Iniciando interfaz...");
                } else if (progreso == 100) {
                    lblEstadoCarga.setText("¡Listo!");
                }

                //timer
                //timer
                if (progreso == 100) {
                    ((javax.swing.Timer)e.getSource()).stop();

                    try {
                        Inicio_Sesion principal = new Inicio_Sesion(); 
                        // Instanciamos el controlador y le pasamos la vista
                        Menus_Inicio.ControladorLogin controlador = new Menus_Inicio.ControladorLogin(principal);
                        principal.setVisible(true);
                        
                        //Cierra
                        dispose();
                } catch (Exception ex) {
                        System.out.println("Error al cargar la siguiente ventana: " + ex.getMessage());
                        ex.printStackTrace(); // Esto nos dirá la línea exacta del error
                    }
                }
                
            }
        });
        timer.start();
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPFondo = new ImagenPanel_MC("/imagenes/FondoETS.jpg");
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        pgbCarga = new javax.swing.JProgressBar();
        lblEstadoCarga = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JPFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/LogoEts.png"))); // NOI18N
        JPFondo.add(lblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 80, 80));

        lblTitulo.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 102));
        lblTitulo.setText("<html>Expert<br>Technical<br>Support</html>");
        JPFondo.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 170, 130));

        pgbCarga.setForeground(new java.awt.Color(39, 174, 96));
        pgbCarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(39, 174, 96)));
        pgbCarga.setStringPainted(true);
        JPFondo.add(pgbCarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 210, 310, 40));

        lblEstadoCarga.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblEstadoCarga.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstadoCarga.setText("Cargando base de datos...");
        JPFondo.add(lblEstadoCarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, 221, -1));

        getContentPane().add(JPFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 350));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu_Carga.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu_Carga.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu_Carga.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu_Carga.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu_Carga().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPFondo;
    private javax.swing.JLabel lblEstadoCarga;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JProgressBar pgbCarga;
    // End of variables declaration//GEN-END:variables
}
