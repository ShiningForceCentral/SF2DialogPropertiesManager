/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.dialog.properties;

import com.sfc.sf2.dialog.properties.io.DisassemblyManager;
import com.sfc.sf2.mapsprite.MapSprite;
import com.sfc.sf2.mapsprite.MapSpriteManager;
import com.sfc.sf2.mapsprite.layout.MapSpriteLayout;
import com.sfc.sf2.portrait.Portrait;
import com.sfc.sf2.portrait.PortraitManager;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author wiz
 */
public class DialogPropertiesManager {
       
    private MapSpriteManager mapSpriteManager = new MapSpriteManager();
    private PortraitManager portraitManager = new PortraitManager();
    private DisassemblyManager disassemblyManager = new DisassemblyManager();
    private DialogProperties dialogProperties = new DialogProperties();
    private MapSprite[] mapsprites;
    private BufferedImage[] mapspriteImages;
    private Portrait[] portraits;
       
    public void importDisassembly(String palettePath, String mapspritesPath, String portraitsPath, String basePath, String filePath){
        System.out.println("com.sfc.sf2.dialog.properties.DialogPropertiesManager.importDisassembly() - Importing disassembly ...");
        mapsprites = mapSpriteManager.importDisassemblyFromEntryFile(palettePath, mapspritesPath, basePath);
        mapspriteImages = new BufferedImage[mapsprites.length/3];
        for(int i=0;i<mapspriteImages.length;i++){
            MapSprite ms = mapsprites[i*3+2];
            if(ms!=null){            
                BufferedImage img = MapSpriteLayout.buildImage(ms.getTiles(), 6);
                img = img.getSubimage(0, 0, 24, 24);
                BufferedImage newImage = new BufferedImage(img.getWidth()*2, img.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
                Graphics g = newImage.getGraphics();
                g.drawImage(img, 0, 0, img.getWidth()*2, img.getHeight()*2, null);
                g.dispose();
                mapspriteImages[i] = newImage;
            }else{
                mapspriteImages[i] = new BufferedImage(24,24,BufferedImage.TYPE_INT_RGB);    
            }
        }
        portraits = portraitManager.importDisassemblyFromEntryFile(basePath, portraitsPath);
        dialogProperties = DisassemblyManager.importDisassembly(filePath);
        System.out.println("com.sfc.sf2.dialog.properties.DialogPropertiesManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String filepath){
        System.out.println("com.sfc.sf2.dialog.properties.DialogPropertiesManager.exportDisassembly() - Exporting disassembly ...");
        DisassemblyManager.exportDisassembly(dialogProperties, filepath);
        System.out.println("com.sfc.sf2.dialog.properties.DialogPropertiesManager.exportDisassembly() - Disassembly exported.");        
    } 

    public DialogProperties getDialogProperties() {
        return dialogProperties;
    }

    public void setDialogProperties(DialogProperties dialogProperties) {
        this.dialogProperties = dialogProperties;
    }

    public MapSprite[] getMapsprites() {
        return mapsprites;
    }

    public void setMapsprites(MapSprite[] mapsprites) {
        this.mapsprites = mapsprites;
    }

    public Portrait[] getPortraits() {
        return portraits;
    }

    public void setPortraits(Portrait[] portraits) {
        this.portraits = portraits;
    }

    public BufferedImage[] getMapspriteImages() {
        return mapspriteImages;
    }

    public void setMapspriteImages(BufferedImage[] mapspriteImages) {
        this.mapspriteImages = mapspriteImages;
    }

    
}
