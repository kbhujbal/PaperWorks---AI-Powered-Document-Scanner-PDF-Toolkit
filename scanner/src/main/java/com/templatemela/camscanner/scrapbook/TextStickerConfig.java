package com.templatemela.camscanner.scrapbook;

import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.UUID;

public class TextStickerConfig implements StickerConfigInterface {
    private Paint.Align align;
    private int backgroundColor;
    private int color;
    private Typeface font;
    private final String identifierId = UUID.randomUUID().toString();
    private String text;
    @Override
    public int getStickerId() {
        return -1;
    }

    public TextStickerConfig(String str, Paint.Align align2, Typeface typeface, int i, int i2) {
        this.text = str;
        this.color = i;
        this.font = typeface;
        this.backgroundColor = i2;
        this.align = align2;
    }
    @Override
    public StickerConfigInterface.STICKER_TYPE getType() {
        return StickerConfigInterface.STICKER_TYPE.TEXT;
    }

    public String getText() {
        return this.text;
    }

    public Paint.Align getAlign() {
        return this.align;
    }

    public void setAlign(Paint.Align align2) {
        this.align = align2;
    }

    public Typeface getTypeface() {
        Typeface typeface = this.font;
        if (typeface == null) {
            return null;
        }
        return typeface;
    }

    public int getColor() {
        return this.color;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setText(String str) {
        this.text = str;
    }

    public void setText(String str, Paint.Align align2) {
        this.text = str;
        this.align = align2;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public void setBackgroundColor(int i) {
        this.backgroundColor = i;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.identifierId.equals(((TextStickerConfig) obj).identifierId);
    }
    @Override
    public int hashCode() {
        return this.identifierId.hashCode();
    }
    @Override
    public String toString() {
        return "TextStickerConfig{text='" + this.text + '\'' + ", Pack 1=" + this.font + ", color=" + this.color + ", backgroundColor=" + this.backgroundColor + ", align=" + this.align + ", identifierId='" + this.identifierId + '\'' + '}';
    }
}
