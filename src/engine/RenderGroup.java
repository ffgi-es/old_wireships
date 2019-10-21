/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.joml.Matrix4f;

/**
 *
 * @author ffgi
 */
public class RenderGroup extends PositionGroup {
    private static final ArrayList<RenderPoints> RENDERABLES = new ArrayList<RenderPoints>();
    private static final HashMap<Integer, Renderer> RENDERERS = new HashMap<>();
    
    private class RenderItem {
        public static final int RENDERGROUP = -1;
        
        public RenderPoints rPoints;
        public RenderGroup renderGroup;
        public int type;
        public int layer;
        
        RenderItem(RenderPoints rPoints, int layer, int type) {
            this.rPoints = rPoints;
            this.layer = layer;
            this.type = type;
        }
        RenderItem(RenderGroup rGroup, int layer) {
            this.renderGroup = rGroup;
            this.layer = layer;
            this.type = RENDERGROUP;
        }
    }
    
    private final RenderItem[] orderedItems;
    private final boolean nesting;
    
    public RenderGroup() {
        super();
        nesting = false;
        orderedItems = new RenderItem[] {};
    }
    
    public RenderGroup(RenderPoints[][] rPoints, int[][] layers) {
        super();
        this.nesting = false;
        
        int orderedItemsLength = 0;
        
        // find the total number of RenderItems to be stored
        // also store the RenderPoints for cleanup later...
        for (RenderPoints[] array : rPoints) {
            orderedItemsLength += array.length;
            RENDERABLES.addAll(Arrays.asList(array));
        }
        
        // create the items array
        this.orderedItems = new RenderItem[orderedItemsLength];
        
        // assign values to the items array
        int startIndex = 0;
        for (int i=0; i<rPoints.length; i++) {
            // turn the renderpoints to renderitem arrays
            RenderItem[] rItems = toRenderItemArray(rPoints[i], layers[i], i);
            
            // copy those array into place in the final item array
            System.arraycopy(rItems, 0, orderedItems, startIndex, rItems.length);
            
            startIndex += rPoints[i].length;
        }
        
        // sort the item array so that it renders by layer and then by type;
        Arrays.sort(orderedItems, (a,b)->{
            int layerResult = a.layer - b.layer;
            if (layerResult == 0) return a.type - b.type;
            else return layerResult;
        });
    }
    
    public RenderGroup(RenderGroup other) {
        this(other, new RenderGroup[] {}, new int[] {});
    }
    
    public RenderGroup(RenderGroup other, RenderGroup[] rGroups, int[] rGLayers) {
        super(other);
        this.nesting = rGroups.length > 0;
        
        if (other.nesting) {
            int numGroups = 0;
            for (RenderItem item : other.orderedItems)
                if (item.type == RenderItem.RENDERGROUP) numGroups++;
            orderedItems = new RenderItem[other.orderedItems.length-numGroups+rGroups.length];
        }
        else {
            orderedItems = new RenderItem[other.orderedItems.length+rGroups.length];
        }
        
        RenderItem[] groupItems = toRenderItemArray(rGroups, rGLayers);
        Arrays.sort(groupItems, (a,b)->{ return a.layer - b.layer; });
        
        int currLayer = 0;
        int otherIndex = 0;
        int groupIndex = 0;
        for (int i=0; i<orderedItems.length; i++) {
            while(orderedItems[i] == null) {
                while (otherIndex < other.orderedItems.length &&
                        other.orderedItems[otherIndex].type == RenderItem.RENDERGROUP)
                    otherIndex++;
                if (otherIndex < other.orderedItems.length &&
                        other.orderedItems[otherIndex].layer == currLayer) {
                    orderedItems[i] = other.orderedItems[otherIndex++];
                    break;
                }
                else if (groupIndex < groupItems.length && 
                        groupItems[groupIndex].layer == currLayer) {
                    orderedItems[i] = groupItems[groupIndex++];
                    break;
                }
                else currLayer++;
            }
        }
    }
    
    public static void setRenderer(int type, Renderer renderer) {
        RENDERERS.put(type, renderer);
    }
    
    public void render() {
        if (nesting) throw new RuntimeException("Cannot simply render nested RenderGroup");
        
        for (RenderItem item : orderedItems) {
            RENDERERS.get(item.type).render(item.rPoints);
        }
    }
    
    public void nestedRender(Matrix4f PVWMatrix, Matrix4f NMatrix) {
        Matrix4f thisPVWM = new Matrix4f(PVWMatrix).mul(
                Transformation.getWorldMatrix(this));
        Matrix4f thisNM = new Matrix4f(NMatrix).mul(
                Transformation.getNormalMatrix(this));
        
        ShaderProgram.setUniformValue("PVWMatrix", thisPVWM);
        ShaderProgram.setUniformValue("NMatrix", thisNM);
        
        for (RenderItem item : orderedItems) {
            if (item.type < 0)
                item.renderGroup.nestedRender(thisPVWM, thisNM);
            else
                RENDERERS.get(item.type).render(item.rPoints);
        }
        
        ShaderProgram.setUniformValue("PVWMatrix", PVWMatrix);
        ShaderProgram.setUniformValue("NMatrix", NMatrix);
    }
    
    private RenderItem[] toRenderItemArray(RenderPoints[] rPoints, int[] layers, int type) {
        RenderItem[] rPointItems = new RenderItem[rPoints.length];
        for (int i=0; i<rPoints.length; i++) {
            rPointItems[i] = new RenderItem(rPoints[i], layers[i], type);
        }
        return rPointItems;
    }
    
    private RenderItem[] toRenderItemArray(RenderGroup[] rGrps, int[] rGLyrs) {
        RenderItem[] rGroupItems = new RenderItem[rGrps.length];
        for (int i=0; i<rGrps.length; i++) {
            rGroupItems[i] = new RenderItem(rGrps[i], rGLyrs[i]);
        }
        return rGroupItems;
    }
    
    public static void cleanUp() {
        RENDERABLES.forEach(rPoints->rPoints.cleanUp());
    }
}
