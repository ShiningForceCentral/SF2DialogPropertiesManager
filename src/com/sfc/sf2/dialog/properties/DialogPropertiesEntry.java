/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.dialog.properties;

/**
 *
 * @author wiz
 */
public class DialogPropertiesEntry {
    
    private int spriteId;
    private int portraitId;
    private int sfxId;
    private String spriteName;
    private String portraitName;
    private String sfxName;

    public int getSpriteId() {
        return spriteId;
    }

    public int getPortraitId() {
        return portraitId;
    }

    public int getSfxId() {
        return sfxId;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public String getPortraitName() {
        return portraitName;
    }

    public String getSfxName() {
        return sfxName;
    }

    public void setPortrait(int portraitId, String portraitName) {
        this.portraitId = portraitId;
        this.portraitName = portraitName;
    }

    public void setSprite(int spriteId, String spriteName) {
        this.spriteId = spriteId;
        this.spriteName = spriteName;
    }

    public void setSfx(int sfxId, String sfxName) {
        this.sfxId = sfxId;
        this.sfxName = sfxName;
    }
}
