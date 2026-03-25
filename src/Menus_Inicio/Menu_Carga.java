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

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/LogoEts.png"))); // NOI18N

        lblTitulo.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 102));
        lblTitulo.setText("<html>Expert<br>Technical<br>Support</html>");

        pgbCarga.setForeground(new java.awt.Color(39, 174, 96));
        pgbCarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(39, 174, 96)));
        pgbCarga.setStringPainted(true);

        lblEstadoCarga.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblEstadoCarga.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEstadoCarga.setText("Cargando base de datos...");

        javax.swing.GroupLayout JPFondoLayout = new javax.swing.GroupLayout(JPFondo);
        JPFondo.setLayout(JPFondoLayout);
        JPFondoLayout.setHorizontalGroup(
            JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
            .addGroup(JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(JPFondoLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(JPFondoLayout.createSequentialGroup()
                            .addComponent(lblLogo)
                            .addGap(12, 12, 12)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(pgbCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblEstadoCarga, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        JPFondoLayout.setVerticalGroup(
            JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
            .addGroup(JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(JPFondoLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(JPFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(38, 38, 38)
                    .addComponent(pgbCarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(lblEstadoCarga)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(JPFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 450, 350));

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
