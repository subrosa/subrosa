package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.image.Image;

/**
 * Represents an Accolade (or achievement) in the Subrosa application.
 */
public class Accolade {

    private AccoladeType accoladeType;
    private Image image;

    public AccoladeType getAccoladeType() {
        return accoladeType;
    }

    public void setAccoladeType(AccoladeType accoladeType) {
        this.accoladeType = accoladeType;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
