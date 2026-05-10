package me.uxokpro1234.salt.module;

import org.lwjgl.input.Keyboard;

public class Bind {
    private int key = Keyboard.KEY_NONE;

    public Bind() { }

    public Bind(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isNone() {
        return key == Keyboard.KEY_NONE;
    }

    @Override
    public String toString() {
        if (isNone()) return "NONE";
        return Keyboard.getKeyName(key);
    }
}