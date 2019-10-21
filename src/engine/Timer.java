/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author ffgi
 */
public class Timer {
    private double lastLoopTime;

    public void init() {
        lastLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() { return lastLoopTime; }
    
    public boolean hasPassed(float period) {
        double time = getTime();
        double elapsedTime = time - lastLoopTime;
        if (elapsedTime > period) {
            lastLoopTime += period;
            return true;
        }
        return false;
    }
}
