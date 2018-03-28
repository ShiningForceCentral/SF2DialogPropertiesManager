/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.dialog.properties.gui;

import com.sfc.sf2.dialog.properties.DialogProperties;
import com.sfc.sf2.dialog.properties.DialogPropertiesEntry;
import com.sfc.sf2.mapsprite.MapSprite;
import com.sfc.sf2.portrait.Portrait;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class DialogPropertiesTableModel extends AbstractTableModel {
    
    private final Object[][] tableData;
    private final String[] columns = {"Sprite Id", "Graphics", "Portrait Id", "Graphics", "SFX Id"};
    private DialogProperties properties = null;
    private BufferedImage[] mapspriteImages = null;
    private Portrait[] portraits = null;
    private static final BufferedImage emptyImage = new BufferedImage(24,24,BufferedImage.TYPE_INT_RGB);
 
    public DialogPropertiesTableModel(DialogProperties properties, BufferedImage[] mapspriteImages, Portrait[] portraits) {
        super();
        this.properties = properties;
        this.mapspriteImages = mapspriteImages;
        this.portraits = portraits;
        tableData = new Object[256][];
        int i = 0;
        DialogPropertiesEntry[] entries = properties.getEntries();
        if(entries!=null){
            while(i<entries.length){
                tableData[i] = new Object[10];
                int spriteId = entries[i].getSpriteId();
                tableData[i][0] = spriteId;
                if(spriteId<mapspriteImages.length){
                    tableData[i][1] = mapspriteImages[spriteId];
                }else{
                    tableData[i][1] = emptyImage;
                }
                int portraitId = entries[i].getPortraitId();
                tableData[i][2] = portraitId;
                if(portraitId<portraits.length){
                    tableData[i][3] = portraits[portraitId].getImage();
                }else{
                    tableData[i][3] = emptyImage;
                }
                tableData[i][4] = entries[i].getSfxId();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Object[5];
            tableData[i][0] = -1;
            tableData[i][1] = emptyImage;
            tableData[i][2] = -1;
            tableData[i][3] = emptyImage;
            tableData[i][4] = -1;
            i++;
        } 
    }
    
    @Override
    public Class getColumnClass(int column) {
        if(column == 1 || column == 3){
            return Object.class;
        }else {
            return Integer.class;
        }
    } 
    
    public void updateProperties(){
        List<DialogPropertiesEntry> entries = new ArrayList<>();
        for(Object[] entry : tableData){
            try{
                if(((int)entry[0])!=-1 && ((int)entry[2])!=-1 && ((int)entry[4])!=-1){
                    DialogPropertiesEntry dialogPropertiesEntry = new DialogPropertiesEntry();
                    dialogPropertiesEntry.setSpriteId((int)entry[0]);
                    dialogPropertiesEntry.setPortraitId((int)entry[2]);
                    dialogPropertiesEntry.setSfxId((int)entry[4]);
                    entries.add(dialogPropertiesEntry);
                }
            }catch(Exception e){
                break;
            }
        }
        DialogPropertiesEntry[] returnedEntries = new DialogPropertiesEntry[entries.size()];
        properties.setEntries(entries.toArray(returnedEntries));
    }  
    
    public void updateSprite(int row){
        int spriteId = (int)tableData[row][0];
        //System.out.println("updateSprite");
        if(spriteId>=0&&spriteId<mapspriteImages.length){
            tableData[row][1] = mapspriteImages[spriteId];
        }else{
             tableData[row][1] = emptyImage;
        }
    }  

    public void updatePortrait(int row){
        int portraitId = (int)tableData[row][2];
        if(portraitId>=0&&portraitId<portraits.length){
            tableData[row][3] = portraits[portraitId].getImage();
        }else{
            tableData[row][3] = emptyImage;
        }
    }  
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = value;
        updateProperties();
        if(col==0){
            updateSprite(row);
        }
        if(col==2){
            updatePortrait(row);
        }
    }   
    
    
 
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex==0 || columnIndex==2 || columnIndex==4){
            return true;
        } else return false;
    }    
    
    @Override
    public int getRowCount() {
        return tableData.length;
    }
 
    @Override
    public int getColumnCount() {
        return columns.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }
    
}
