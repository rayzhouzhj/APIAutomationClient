package com.auto.tools.ui;

import java.awt.Color;

import javax.swing.table.DefaultTableModel;

import com.auto.tools.ui.components.editor.TestDataButtonEditor;

/**
 *
 * @author ray_zhou
 */
public class NewViewTestDataFrame extends javax.swing.JFrame {

	private TestDataButtonEditor dataSourceEditor;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates new form ViewTestDataFrame
	 */
	public NewViewTestDataFrame(TestDataButtonEditor editor) {
		this.dataSourceEditor = editor;
		
		initComponents();
	}

	public void initDataTable()
	{
		
        varTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null}
                },
                new String [] {
                    "Name", "Value"
                }
            ));
        
        DefaultTableModel model = (DefaultTableModel) this.varTable.getModel();
        model.removeRow(0);
        
        String variablesStr = this.dataSourceEditor.getCellEditorValue().toString();
		String[] variables = variablesStr.split("\" ");
		String[] valuePair;
		for(String var : variables)
		{
			if(!var.isEmpty())
			{
				valuePair = var.split("=");
				model.addRow(new Object[]{valuePair[0], valuePair[1].replace("\"", "")});
			}
		}
		
        dataPane.setViewportView(varTable);
        dataPane.getViewport().setBackground(new Color(233,235,238));
	}
	
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        dataPane = new javax.swing.JScrollPane();
        varTable = new javax.swing.JTable();
        update = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        addRow = new javax.swing.JButton();
        removeRow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Variables");

        initDataTable();

        update.setText("Update");
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        addRow.setText("+");
        addRow.setPreferredSize(new java.awt.Dimension(65, 29));
        addRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowActionPerformed(evt);
            }
        });

        removeRow.setText("-");
        removeRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(dataPane, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(addRow, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeRow, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(127, 127, 127)
                        .addComponent(update)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataPane, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(update)
                    .addComponent(cancel)
                    .addComponent(addRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeRow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    private void removeRowActionPerformed(java.awt.event.ActionEvent evt) {                                          

        int[] rows = this.varTable.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) this.varTable.getModel();
        
        for(int row : rows)
        {
            model.removeRow(row);
        }
    }                                         

    private void addRowActionPerformed(java.awt.event.ActionEvent evt) {                                       
        DefaultTableModel model = (DefaultTableModel) this.varTable.getModel();
        model.addRow(new Object[]{null, null});
    }                                      

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {
    	
    	String variables = "";
    	Object varName = "";
    	Object varValue = "";
    	
    	DefaultTableModel model = (DefaultTableModel) this.varTable.getModel();
    	for(int i = 0; i < model.getRowCount(); i++)
    	{
    		
    		varName = model.getValueAt(i, 0);
    		varValue = model.getValueAt(i, 1);
    		
    		if(varName == null || varValue == null)
    		{
    			continue;
    		}
    		
    		variables = variables + varName + "=\"" + varValue + "\" ";
    	}
    	
 	   this.dataSourceEditor.setCellEditorValue(variables);
 	   this.setVisible(false);
        this.dispose();
    }                                      

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {                                       
        this.setVisible(false);
        this.dispose();
    }                                      


    // Variables declaration - do not modify                     
    private javax.swing.JButton addRow;
    private javax.swing.JButton cancel;
    private javax.swing.JScrollPane dataPane;
    private javax.swing.JButton removeRow;
    private javax.swing.JButton update;
    private javax.swing.JTable varTable;
    // End of variables declaration         
}