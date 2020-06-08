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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    
    public static DialogProperties importDisassembly(String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Importing disassembly file ...");
        DialogProperties dialogproperties = new DialogProperties();
        if(filepath.endsWith(".asm")){
            String enumFilePath = "..\\sf2enums.asm";
            dialogproperties = importDisassemblyAsm(enumFilePath, filepath);
        }else{
            dialogproperties = importDisassemblyBin(filepath);
        }
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return dialogproperties;
    }
    
    public static DialogProperties importDisassemblyAsm(String enumFilePath, String filepath){
        DialogProperties dialogproperties = new DialogProperties();
        DialogPropertiesEntry[] entries = null;
        List<DialogPropertiesEntry> entryList = new ArrayList();
        try{
            Map<String, Integer> mapspriteEnum = new HashMap();
            Map<String, Integer> portraitEnum = new HashMap();
            Map<String, Integer> sfxEnum = new HashMap();
            
            File enumFile = new File(enumFilePath);
            Scanner enumScan = new Scanner(enumFile);
            while(enumScan.hasNext()){
                String line = enumScan.nextLine();
                if(line.trim()!=null){
                    switch(line.trim()){
                        default:
                            break;
                            
                        case "; enum Mapsprites":
                            line = enumScan.nextLine();
                            while(line.startsWith("MAPSPRITE")){
                                String key = line.substring(0,line.indexOf(":"));
                                Integer value = Integer.valueOf(line.substring(line.indexOf("$")+1).trim(), 16);
                                mapspriteEnum.put(key, value);
                                line = enumScan.nextLine();
                            }
                            break;
                            
                        case "; enum Portraits":
                            line = enumScan.nextLine();
                            while(line.startsWith("PORTRAIT")){
                                String key = line.substring(0,line.indexOf(":"));
                                Integer value = Integer.valueOf(line.substring(line.indexOf("$")+1).trim(), 16);
                                portraitEnum.put(key, value);
                                line = enumScan.nextLine();
                            }
                            break;
                            
                        case "; enum Sfx":
                            line = enumScan.nextLine();
                            while(line.startsWith("SFX")){
                                String key = line.substring(0,line.indexOf(":"));
                                Integer value = Integer.valueOf(line.substring(line.indexOf("$")+1).trim(), 16);
                                sfxEnum.put(key, value);
                                line = enumScan.nextLine();
                            }
                            break;
                                
                    }
                }
            }
            
            if(mapspriteEnum.isEmpty()){
                System.out.println("WARNING - No enum entry found for enum Mapsprites. Please check formatting : starts with line \"; enum Mapsprites\", directly followed by lines all starting with \"MAPSPRITE\"");
            }
            if(portraitEnum.isEmpty()){
                System.out.println("WARNING - No enum entry found for enum Portraits. Please check formatting : starts with line \"; enum Portraits\", directly followed by lines all starting with \"PORTRAIT\"");
            }
            if(sfxEnum.isEmpty()){
                System.out.println("WARNING - No enum entry found for enum Sfx. Please check formatting : starts with line \"; enum Sfx\", directly followed by lines all starting with \"SFX\"");
            }
            
            File file = new File(filepath);
            Scanner scan = new Scanner(file);
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.trim().contains(":")){
                    line = line.substring(0,line.indexOf(":"));
                }
                if(line.trim().startsWith("mapSprite")){
                    DialogPropertiesEntry entry = new DialogPropertiesEntry();
                    
                    if(line.contains(";")){
                        line = line.substring(0,line.indexOf(";"));
                    }
                    String value = line.trim().substring("mapSprite".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        entry.setSpriteId(valueOf(value));
                    }else{
                        entry.setSpriteId(mapspriteEnum.get("MAPSPRITE_"+value));
                    }
                    
                    if(scan.hasNext()){line = scan.nextLine();}
                    if(!line.trim().startsWith("portrait")){break;}
                    if(line.contains(";")){
                        line = line.substring(0,line.indexOf(";"));
                    }
                    value = line.trim().substring("portrait".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        entry.setPortraitId(valueOf(value));
                    }else{
                        entry.setPortraitId(portraitEnum.get("PORTRAIT_"+value));
                    }
                    
                    if(scan.hasNext()){line = scan.nextLine();}
                    if(!line.trim().startsWith("speechSound")){break;}
                    if(line.contains(";")){
                        line = line.substring(0,line.indexOf(";"));
                    }
                    value = line.trim().substring("speechSound".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        entry.setSfxId(valueOf(value));
                    }else{
                        entry.setSfxId(sfxEnum.get("SFX_"+value));
                    }
                    
                    entryList.add(entry);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        entries = new DialogPropertiesEntry[entryList.size()];
        entries = entryList.toArray(entries);
        dialogproperties.setEntries(entries);
        return dialogproperties;
    }
    
    private static int valueOf(String s){
        s = s.trim();
        if(s.startsWith("$")){
            return Integer.valueOf(s.substring(1),16);
        }else{
            return Integer.valueOf(s);
        }
    }
    
    public static DialogProperties importDisassemblyBin(String filepath){
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
            if(filepath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append("SpriteDialogProperties:\n");
                asm.append(producePropertiesFileAsm(props));
                Path propsFilePath = Paths.get(filepath);
                Files.write(propsFilePath,asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] propertiesFileBytes = producePropertiesFileBytes(props);
                Path propsFilePath = Paths.get(filepath);
                Files.write(propsFilePath,propertiesFileBytes);
                System.out.println(propertiesFileBytes.length + " bytes into " + propsFilePath);
            }
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
    
    private static String producePropertiesFileAsm(DialogProperties props){
        DialogPropertiesEntry[] entries = props.getEntries();
        StringBuilder asm = new StringBuilder();
        for(int i=0;i<entries.length;i++){
            asm.append("                "+"mapSprite   "+entries[i].getSpriteId()+"\n");
            asm.append("                "+"portrait    "+entries[i].getPortraitId()+"\n");
            asm.append("                "+"speechSound "+entries[i].getSfxId()+"\n\n");
        }
        asm.append("                "+"tableEnd"+"\n");
        return asm.toString();
    }
    
    private static byte[] producePropertiesFileBytes(DialogProperties props){
        DialogPropertiesEntry[] entries = props.getEntries();
        byte[] propertiesFileBytes = new byte[entries.length*4+2];
        for(int i=0;i<entries.length;i++){
            propertiesFileBytes[i*4+0] = (byte)(entries[i].getSpriteId());
            propertiesFileBytes[i*4+1] = (byte)(entries[i].getPortraitId());
            propertiesFileBytes[i*4+2] = (byte)(entries[i].getSfxId());
        }
        propertiesFileBytes[entries.length*4] = -1;
        propertiesFileBytes[entries.length*4+1] = -1;
        return propertiesFileBytes;
    }
    
    
}