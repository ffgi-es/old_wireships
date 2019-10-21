/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Letter extends RenderGroup{
    private float midLeftLimit;
    private float midRightLimit;
    
    private boolean upperLimits;
    private float upperLeftLimit;
    private float upperRightLimit;
    
    private boolean lowerLimits;
    private float lowerLeftLimit;
    private float lowerRightLimit;
    
    private final char letter;
    
    public Letter(char c, RenderGroup rgp) {
        super(rgp);
        letter = c;
        
        upperLeftLimit = 0;
        upperRightLimit = 4;
        midLeftLimit = 0;
        midRightLimit = 5;
        lowerLeftLimit = 0;
        lowerRightLimit = 4;
    }
    public Letter(Letter other) {
        super(other);
        this.letter = other.letter;
        
        this.midLeftLimit = other.midLeftLimit;
        this.midRightLimit = other.midRightLimit;
        this.upperLimits = other.upperLimits;
        this.upperLeftLimit = other.upperLeftLimit;
        this.upperRightLimit = other.upperRightLimit;
        this.lowerLimits = other.lowerLimits;
        this.lowerLeftLimit = other.lowerLeftLimit;
        this.lowerRightLimit = other.lowerRightLimit;
    }
    
    public char getChar() { return letter; }
    
    public void setPositionAfter(Letter other) {
        float midOffset = other.midRightLimit - this.midLeftLimit;
        float upperOffset = other.upperRightLimit - this.upperLeftLimit;
        float lowerOffset = other.lowerRightLimit - this.lowerLeftLimit;
        
        float finalOffset = Math.max(midOffset, Math.max(upperOffset, lowerOffset));
        Vector3f offset = new Vector3f(finalOffset, 0 ,0);
        
        this.setPosition(other.getPosition().add(offset.mul(other.getScale())));
    }
}