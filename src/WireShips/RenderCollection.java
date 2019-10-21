/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

/**
 *
 * @author ffgi
 */
public class RenderCollection extends PositionObject {
    private final RenderObject[] ROs;
    
    public RenderCollection(RenderObject[] ros) {
        super();
        ROs = new RenderObject[ros.length];
        for (int i=0; i<ROs.length; i++) {
            ROs[i] = new RenderObject(ros[i]);
        }
    }
    
    public RenderCollection(RenderCollection r) {
        super(r);
        ROs = new RenderObject[r.ROs.length];
        for (int i=0; i<ROs.length; i++) {
            ROs[i] = new RenderObject(r.ROs[i]);
        }
    }
    
    public RenderObject[] getROs() { return ROs; }
}
