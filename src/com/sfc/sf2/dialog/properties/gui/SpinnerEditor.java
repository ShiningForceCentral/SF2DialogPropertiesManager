/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.dialog.properties.gui;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;


/**
 *
 * @author wiz
 */
public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
   protected JSpinner spinner;
   private DialogPropertiesTableModel dptm;
   private JTable table;
   
   public SpinnerEditor(DialogPropertiesTableModel dptm, final JTable table) {
      spinner = new JSpinner();
      this.dptm = dptm;
      this.table = table;
      spinner.setModel(new SpinnerNumberModel(-1,-1,255,1));
        ChangeListener listener = new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            //System.out.println("stateChanged");
            fireEditingStopped();
            //dptm.fireTableCellUpdated(0, 0);
            table.repaint();
          }
        };

        spinner.addChangeListener(listener);
   }
  
   public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
            //System.out.println("getTableCellEditorComponent");
      spinner.setValue(value);
      //dptm.fireTableCellUpdated(row, column);
      return spinner;
   }
  
   public Object getCellEditorValue() {
      return spinner.getValue();
   }
}
