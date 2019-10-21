/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Line;
import engine.Mesh;
import engine.RenderPoints;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class RenderObject extends PositionObject {
    private static final List<RenderPoints> MESHES = new ArrayList<>();
    
    private final Mesh mesh;
    private final boolean bMesh;
    private final Line line;
    private final boolean bLine;
    private Vector3f colour;
    
    public RenderObject(RenderObject r) {
        super(r);
        this.mesh = r.mesh;
        this.bMesh = r.bMesh;
        this.line = r.line;
        this.bLine = r.bLine;
        this.colour = new Vector3f(r.colour);
    }

    public RenderObject(Mesh mesh, Line line) {
        super();
        if (mesh != null) {
            MESHES.add(mesh);
            this.mesh = mesh;
            bMesh = true;
        } else {
            this.mesh = null;
            bMesh = false;
        }
        
        if (line != null) {
            MESHES.add(line);
            this.line = line;
            bLine = true;
        } else {
            this.line = null;
            bLine = false;
        }
        
        colour = new Vector3f(1, 1, 1);
    }

    public boolean hasMesh() { return bMesh; }
    public boolean hasLine() { return bLine; }
    public Mesh getMesh() { return mesh; }
    public Line getLine() { return line; }
    
    public Vector3f getColour() { return new Vector3f(colour); }
    public void setColour(float r, float g, float b) {
        colour = new Vector3f(r,g,b);
    }
    
    public static void cleanUp() {
        MESHES.forEach(mesh -> {
            mesh.cleanUp();
        });
    }
}
