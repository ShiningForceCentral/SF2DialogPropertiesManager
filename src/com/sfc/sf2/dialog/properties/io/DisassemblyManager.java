/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.dialog.properties.io;

import com.sfc.sf2.dialog.properties.DialogProperties;
import com.sfc.sf2.dialog.properties.DialogPropertiesEntry;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    
    public static DialogProperties importDisassembly(String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Importing disassembly file ...");
        
        DialogProperties dialogproperties = new DialogProperties();
        DialogPropertiesEntry[] entries = null;
        List<DialogPropertiesEntry> entryList = new ArrayList();
        try{
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                byte[] data = Files.readAllBytes(path);
                int cursor = 0;
                
                while((cursor+4)<data.length && getWord(data,cursor)!=-1){
                    DialogPropertiesEntry entry = new DialogPropertiesEntry();
                    
                    entry.setSpriteId(getByte(data,cursor)&0xFF);
                    entry.setPortraitId(getByte(data,cursor+1)&0xFF);
                    entry.setSfxId(getByte(data,cursor+2)&0xFF);
                    entryList.add(entry);
                    
                    cursor+=4;
                }
                
                
            }            
        }catch(Exception e){
             System.err.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }    
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        
        entries = new DialogPropertiesEntry[entryList.size()];
        entries = entryList.toArray(entries);
        dialogproperties.setEntries(entries);
        
        return dialogproperties;
    }
    
    public static void exportDisassembly(DialogProperties props, String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.exportDisassembly() - Exporting disassembly ...");
        try{
            
            int cursor = 0;
            DialogPropertiesEntry[] entries = props.getEntries();
            byte[] propertiesFileBytes = new byte[entries.length*4+2];
            for(int i=0;i<entries.length;i++){
                propertiesFileBytes[cursor+0] = (byte)(entries[i].getSpriteId());
                propertiesFileBytes[cursor+1] = (byte)(entries[i].getPortraitId());
                propertiesFileBytes[cursor+2] = (byte)(entries[i].getSfxId());
                cursor+=4;
            }
            
            propertiesFileBytes[cursor] = -1;
            propertiesFileBytes[cursor+1] = -1;

            Path propsFilePath = Paths.get(filepath);
            Files.write(propsFilePath,propertiesFileBytes);
            System.out.println(propertiesFileBytes.length + " bytes into " + propsFilePath);   

        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }  
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
    }     
    
    private static short getWord(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor+1]);
        bb.put(data[cursor]);
        short s = bb.getShort(0);
        return s;
    }
    
    private static byte getByte(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor]);
        byte b = bb.get(0);
        return b;
    }    

    
}
