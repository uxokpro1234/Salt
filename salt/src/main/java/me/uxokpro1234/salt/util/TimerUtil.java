package me.uxokpro1234.salt.util;

public class TimerUtil {

    private long lastTime = System.currentTimeMillis();

    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - lastTime >= delay;
    }

    public void reset() {
        lastTime = System.currentTimeMillis();
    }
}