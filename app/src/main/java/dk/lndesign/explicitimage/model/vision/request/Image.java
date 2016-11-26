/**
 * Copyright (C) 2016 Lars Nielsen.
 */
package dk.lndesign.explicitimage.model.vision.request;

import java.util.Locale;

/**
 * @author Lars Nielsen <larn@tv2.dk>.
 */
public class Image {

    private String content;

    public Image(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s { Image content is too much for a log }",
                this.getClass().getSimpleName());
    }
}
