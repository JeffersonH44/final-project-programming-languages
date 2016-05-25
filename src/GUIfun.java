/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author sergiodiazpinilla
 */
public class GUIfun extends javax.swing.JFrame {

    /**
     * Creates new form GUIfun
     */
    public GUIfun() {
        initComponents();
    }

    private String[][] examplesData;

    public void setExamplesData(String[][] examplesData) {
        this.examplesData = examplesData;
    }

    public String[][] getExamplesData() {
        return examplesData;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        principalPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        examples = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();
        generate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        fitness = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        open = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        examples.setColumns(20);
        examples.setRows(5);
        jScrollPane1.setViewportView(examples);

        output.setColumns(20);
        output.setRows(5);
        jScrollPane2.setViewportView(output);

        generate.setText("Generate");
        generate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateActionPerformed(evt);
            }
        });

        jLabel1.setText("Fitnes :");

        javax.swing.GroupLayout principalPanelLayout = new javax.swing.GroupLayout(principalPanel);
        principalPanel.setLayout(principalPanelLayout);
        principalPanelLayout.setHorizontalGroup(
                principalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(principalPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(principalPanelLayout.createSequentialGroup()
                                .addGap(334, 334, 334)
                                .addComponent(generate, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(fitness)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        principalPanelLayout.setVerticalGroup(
                principalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(principalPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(principalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(principalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(generate, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(fitness))
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        fileMenu.setText("File");

        open.setText("Open");
        open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        fileMenu.add(open);

        exit.setText("Exit");
        fileMenu.add(exit);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(principalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(principalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
        String data = abrirArchivo();
        formatText(data);
        examples.setText(data);

    }//GEN-LAST:event_openActionPerformed

    private void generateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateActionPerformed
        Main main = new Main();
        formatText(examples.getText());
        main.generateSolution(getExamplesData());
        String solution = formatSolution(main.getSolution().toString());
        output.setText(solution);
        fitness.setText(Double.toString(main.getQuality()));
    }//GEN-LAST:event_generateActionPerformed

    private String formatSolution(String solution){
        String [] text = solution.split(";");
        String format = "";
        for(String eq:text){
            format += eq+"\n";
        }
        return format;
    }

    private void formatText(String text){
        String [] equations = text.split("\n");
        String [][] mat = new String[equations.length][2];
        for(int i = 0;i< equations.length;i++){
            mat[i][0] = equations[i].split("=")[0];
            mat[i][1] = equations[i].split("=")[1];
        }
        setExamplesData(mat);
    }
    
    private String abrirArchivo() {
        String aux="";   
        String texto="";
        try
        {
        /**llamamos el metodo que permite cargar la ventana*/
        JFileChooser file=new JFileChooser();
        file.showOpenDialog(this);
        /**abrimos el archivo seleccionado*/
        File abre=file.getSelectedFile();

        /**recorremos el archivo, lo leemos para plasmarlo
        *en el area de texto*/
        if(abre!=null)
        {     
          FileReader archivos=new FileReader(abre);
          BufferedReader lee=new BufferedReader(archivos);
          while((aux=lee.readLine())!=null)
          {
             texto+= aux+ "\n";
          }
             lee.close();
        }    
        }
        catch(IOException ex)
        {
         JOptionPane.showMessageDialog(null,ex+"" +
               "\nNo se ha encontrado el archivo",
                     "ADVERTENCIA!!!",JOptionPane.WARNING_MESSAGE);
        }
        return texto;//El texto se almacena en el JTextArea
    }
    
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
            java.util.logging.Logger.getLogger(GUIfun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIfun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIfun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIfun.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUIfun().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JTextArea examples;
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel fitness;
    private javax.swing.JButton generate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem open;
    private javax.swing.JTextArea output;
    private javax.swing.JPanel principalPanel;
    // End of variables declaration//GEN-END:variables
}
