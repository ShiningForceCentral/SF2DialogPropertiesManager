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
import java.util.Map.Entry;
import java.util.Scanner;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    
    static String asmHeader;
    static String asmHeaderAllies;
    
    public static DialogProperties importDisassembly(String basePath, String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Importing disassembly file ...");
        DialogProperties dialogproperties = new DialogProperties();
        String enumFilePath = basePath + "sf2enums.asm";
        if(filepath.endsWith(".asm")){
            dialogproperties = importDisassemblyAsm(enumFilePath, filepath);
        }else{
            dialogproperties = importDisassemblyBin(enumFilePath, filepath);
        }
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return dialogproperties;
    }
    
    public static DialogProperties importDisassemblyAsm(String enumFilePath, String filepath){
        DialogProperties dialogproperties = new DialogProperties();
        DialogPropertiesEntry[] entries = null;
        asmHeader = new String();
        List<DialogPropertiesEntry> entryList = new ArrayList();
        try{
            Map<String, Integer> mapspriteEnum = new HashMap();
            Map<String, Integer> portraitEnum = new HashMap();
            Map<String, Integer> sfxEnum = new HashMap();
            importEnumFileData(enumFilePath, mapspriteEnum, portraitEnum, sfxEnum);
            
            boolean isHeader = true;
            File file = new File(filepath);
            Scanner scan = new Scanner(file);
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(!line.startsWith(";") && line.contains(";")){
                    line = line.substring(0,line.indexOf(";"));
                }
                if(line.trim().startsWith("mapsprite")){
                    isHeader = false;
                    DialogPropertiesEntry entry = new DialogPropertiesEntry();
                    
                    String value = line.trim().substring("mapsprite".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        int val = valueOf(value);
                        entry.setSprite(val, FindByValue(val, mapspriteEnum));
                    }else{                        
                        entry.setSprite(mapspriteEnum.get("MAPSPRITE_"+value), value);
                    }
                    
                    if(scan.hasNext()){line = scan.nextLine().trim();}
                    if(!line.startsWith("portrait")){break;}
                    value = line.trim().substring("portrait".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        int val = valueOf(value);
                        entry.setPortrait(val, FindByValue(val, portraitEnum));
                    }else{                        
                        entry.setPortrait(portraitEnum.get("PORTRAIT_"+value), value);
                    }
                    
                    if(scan.hasNext()){line = scan.nextLine().trim();}
                    if (line.trim().startsWith("if (FIX_ELIS_SPEECH_SFX"))
                    {
                        //Special case to handle the "FIX_ELIS_SPEECH_SFX"
                        //Not intelligent enough to preserve both formats. Just applies the fix
                        line = scan.nextLine().trim();
                        scan.nextLine();
                        scan.nextLine();
                        scan.nextLine();
                    }
                    if(!line.trim().startsWith("speechSfx")){break;}
                    value = line.substring("speechSfx".length()).trim();
                    if(value.contains("$")||value.matches("[0-9]+")){
                        int val = valueOf(value);
                        entry.setSfx(val, FindByValue(val, sfxEnum));
                    }else{                        
                        entry.setSfx(sfxEnum.get("SFX_"+value), value);
                    }
                    
                    entryList.add(entry);
                } else if (isHeader) {
                    asmHeader = asmHeader+line+"\n";
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
    
    public static DialogProperties importAlliesDisassembly(String basePath, String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importAlliesDisassembly() - Importing disassembly file ...");
        DialogProperties dialogproperties = new DialogProperties();
        if(filepath.endsWith(".asm")){
            String enumFilePath = basePath + "sf2enums.asm";
            dialogproperties = importAlliesDisassemblyAsm(enumFilePath, filepath);
        }else{
            System.out.println("Importing ally dialogs from .bin is not implemented");
        }
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.importAlliesDisassembly() - Disassembly imported.");
        return dialogproperties;
    }
    
    public static DialogProperties importAlliesDisassemblyAsm(String enumFilePath, String filepath){
        DialogProperties dialogproperties = new DialogProperties();
        DialogPropertiesEntry[] entries = null;
        asmHeaderAllies = new String();
        List<DialogPropertiesEntry> entryList = new ArrayList();
        try{
            Map<String, Integer> mapspriteEnum = new HashMap();
            Map<String, Integer> portraitEnum = new HashMap();
            Map<String, Integer> sfxEnum = new HashMap();
            importEnumFileData(enumFilePath, mapspriteEnum, portraitEnum, sfxEnum);
            
            boolean isHeader = true;
            File file = new File(filepath);
            Scanner scan = new Scanner(file);
            int index = 0;
            while(scan.hasNext()){
                String line = scan.nextLine();
                if (line.startsWith("allyPortraitAndSfx: macro")) {
                    //Macro line starts the same as each entry line
                    asmHeaderAllies = asmHeaderAllies+line+"\n";
                    continue;   //Ignore the macro definition
                }
                if (line.startsWith("; 0:")) {
                    isHeader = false;
                    continue;
                }
                if(!line.startsWith(";") && line.contains(";")){
                    line = line.substring(0,line.indexOf(";"));
                }
                if(line.trim().startsWith("allyPortraitAndSfx")){
                    isHeader = false;
                    DialogPropertiesEntry entry = new DialogPropertiesEntry();
                    
                    String[] split = line.trim().split("\\s+");
                    split[1] = split[1].replace(",", "");
                    
                    entry.setPortrait(portraitEnum.get("PORTRAIT_"+split[1]), split[1]);
                    entry.setSfx(sfxEnum.get("SFX_"+split[2]), split[2]);
                    
                    String mapspriteSuffix = (index % 3 == 0 ? "_BASE" : (index % 3 == 1 ? "_PROMO" : "_SPECIAL"));
                    String mapspritePrefix;
                    if (split[1].contains("_")) {
                        mapspritePrefix = split[1].substring(0, split[1].indexOf("_"));
                    } else {
                        mapspritePrefix = split[1];                            
                    }
                    if (mapspriteEnum.containsKey("MAPSPRITE_"+mapspritePrefix+mapspriteSuffix)) {
                        entry.setSprite(mapspriteEnum.get("MAPSPRITE_"+mapspritePrefix+mapspriteSuffix), mapspritePrefix+mapspriteSuffix);
                    } else {
                        if (mapspriteEnum.containsKey("MAPSPRITE_"+mapspritePrefix+"_PROMO")) {
                            entry.setSprite(mapspriteEnum.get("MAPSPRITE_"+mapspritePrefix+"_PROMO"), mapspritePrefix+"_PROMO");
                        } else if (mapspriteEnum.containsKey("MAPSPRITE_"+mapspritePrefix+"_BASE")) {
                            entry.setSprite(mapspriteEnum.get("MAPSPRITE_"+mapspritePrefix+"_BASE"), mapspritePrefix+"_BASE");
                        }
                    }                  
                    entryList.add(entry);
                    index++;
                } else if (isHeader) {
                    asmHeaderAllies = asmHeaderAllies+line+"\n";
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

    public static void importEnumFileData(String enumFilePath, Map<String, Integer> mapspriteEnum, Map<String, Integer> portraitEnum, Map<String, Integer> sfxEnum){
        try{            
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
                                if(line.contains(";")){
                                    line = line.substring(0,line.indexOf(";"));
                                }
                                String key = line.substring(0,line.indexOf(":"));
                                line = line.substring(line.indexOf(":")+1);
                                Integer value = valueOf(line);
                                mapspriteEnum.put(key, value);
                                line = enumScan.nextLine();
                            }
                            break;
                            
                        case "; enum Portraits":
                            line = enumScan.nextLine();
                            while(line.startsWith("PORTRAIT")){
                                if(line.contains(";")){
                                    line = line.substring(0,line.indexOf(";"));
                                }
                                String key = line.substring(0,line.indexOf(":"));
                                line = line.substring(line.indexOf(":")+1);
                                Integer value = valueOf(line);
                                portraitEnum.put(key, value);
                                line = enumScan.nextLine();
                            }
                            break;
                            
                        case "; enum Sfx":
                            line = enumScan.nextLine();
                            while(line.startsWith("SFX")){
                                if(line.contains(";")){
                                    line = line.substring(0,line.indexOf(";"));
                                }
                                String key = line.substring(0,line.indexOf(":"));
                                line = line.substring(line.indexOf(":")+1);
                                Integer value = valueOf(line);
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
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private static int valueOf(String s){
        s = s.replace("equ", "");
        s = s.trim();
        if(s.startsWith("$")){
            return Integer.valueOf(s.substring(1),16);
        }else{
            return Integer.valueOf(s);
        }
    }
    
    public static DialogProperties importDisassemblyBin(String enumFilepath, String filepath){
        DialogProperties dialogproperties = new DialogProperties();
        DialogPropertiesEntry[] entries = null;
        List<DialogPropertiesEntry> entryList = new ArrayList();
        try{
            Map<String, Integer> mapspriteEnum = new HashMap();
            Map<String, Integer> portraitEnum = new HashMap();
            Map<String, Integer> sfxEnum = new HashMap();
            importEnumFileData(enumFilepath, mapspriteEnum, portraitEnum, sfxEnum);
            
            Path path = Paths.get(filepath);
            if(path.toFile().exists()){
                byte[] data = Files.readAllBytes(path);
                int cursor = 0;
                
                while((cursor+4)<data.length && getWord(data,cursor)!=-1){                    
                    int spriteId = getByte(data,cursor)&0xFF;
                    int portraitId = getByte(data,cursor+1)&0xFF;
                    int sfxId = getByte(data,cursor+2)&0xFF;
                    String spriteName = FindByValue(spriteId, mapspriteEnum);
                    String portraitName = FindByValue(portraitId, portraitEnum);
                    String sfxName = FindByValue(sfxId, sfxEnum);
                    
                    DialogPropertiesEntry entry = new DialogPropertiesEntry();
                    entry.setSprite(spriteId, spriteName);
                    entry.setPortrait(portraitId, portraitName);
                    entry.setSfx(sfxId, sfxName);
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
                String data = producePropertiesFileAsm(props);
                Path propsFilePath = Paths.get(filepath);
                Files.write(propsFilePath,data.getBytes());
                System.out.println("asm exported to " + propsFilePath);
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
    
    public static void exportAlliesDisassembly(DialogProperties props, String filepath){
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.exportAlliesDisassembly() - Exporting disassembly ...");
        try{
            if(filepath.endsWith(".asm")){
                String data = produceAlliesPropertiesFileAsm(props);
                Path propsFilePath = Paths.get(filepath);
                Files.write(propsFilePath,data.getBytes());
                System.out.println("asm exported to " + propsFilePath);
            }else{
                System.out.println("Exporting ally dialogs from .bin is not implemented");
            }
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }  
        System.out.println("com.sfc.sf2.dialogproperties.io.DisassemblyManager.exportAlliesDisassembly() - Disassembly exported.");        
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
        if (asmHeader == null || asmHeader.length() < 5) {
            asm.append("\ntable_MapspriteDialogueProperties:\n\n");
        } else {
            asm.append(asmHeader);
        }
        for(int i=0;i<entries.length;i++){
            asm.append("                "+"mapSprite "+entries[i].getSpriteName()+"\n");
            asm.append("                "+"portrait "+entries[i].getPortraitName()+"\n");
            asm.append("                "+"speechSfx "+entries[i].getSfxName()+"\n\n");
        }
        asm.append("                "+"tableEnd"+"\n");
        return asm.toString();
    }
    
    private static String produceAlliesPropertiesFileAsm(DialogProperties props){
        DialogPropertiesEntry[] entries = props.getEntries();
        StringBuilder asm = new StringBuilder();
        if (asmHeaderAllies == null || asmHeaderAllies.length() < 5) {
            asm.append("\nallyPortraitAndSfx: macro\n");
            asm.append("    defineShorthand.b PORTRAIT_,\\1\n");
            asm.append("    defineShorthand.b SFX_,\\2\n");
            asm.append("    endm\n");
            asm.append("\n\n\ntable_AllyDialogueProperties:\n\n");
        } else {
            asm.append(asmHeaderAllies);
        }
        int allyIndex;
        int idx;
        for(int i=0;i<entries.length;i++){
            allyIndex = i / 3;
            idx = i % 3;
            if (idx == 0) {
                //First line for ally
                String name = entries[i].getPortraitName();
                if (name.equals("BOWIE_PAINTING")) {
                    name = "";
                } else if (name.contains("_")) {
                    name = name.substring(0, name.indexOf("_"));
                }
                if (allyIndex == 30) {
                    asm.append("            if (EXPANDED_FORCE_MEMBERS=1)\n");
                }
                asm.append("; "+allyIndex+": "+name+"\n");
            }
            asm.append("                allyPortraitAndSfx "+entries[i].getPortraitName()+", "+entries[i].getSfxName()+"\n");
            if (idx == 2) {
                //Last line for ally
                asm.append("\n");
                if (allyIndex == 31) {
                    asm.append("\n            endif\n");
                }
            }
        }
        if (entries.length / 3 < 32) {
            for (int i = entries.length / 3; i < 32; i++) {
                asm.append("; "+i+": \n");
                for (int a = 0; a < 3; a++) {
                    asm.append("                allyPortraitAndSfx BOWIE_PAINTING, DIALOG_BLEEP_5\n");
                }
                if (i == 31) {
                    asm.append("            endif\n");
                }
            }
        }
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
    
    private static int FindByKey(String key, Map<String, Integer> map) {
        if (map.containsKey(key))
            return map.get(key);
        return -1;
    }
    
    private static String FindByValue(int value, Map<String, Integer> map) {
        for (Entry<String, Integer> ent : map.entrySet()) {
            if (ent.getValue().equals(value)) {
                return ent.getKey().substring(ent.getKey().indexOf("_")+1);
            }
        }
        
        return "NOT_FOUND";
    }
}