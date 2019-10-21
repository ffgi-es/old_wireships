/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Parser {
    private static final HashMap<String, RenderGroup> RGPS = new HashMap<>();
    
    private static final int START_STATE = 0;
    private static final int STARTED_STATE = 1;
    private static final int VERTEX_INFO_STATE = 2;
    private static final int VERTEX_DEF_STATE = 3;
    private static final int OBJECT_INFO_STATE = 4;
    private static final int OBJECT_INDEX_STATE = 5;
    
    public interface Handler {
        public RP handle(String type, float[][] vertexData, int[] indices, int layer,
            String[] uniforms);
    }
    
    private static Handler objectHandler = (a,b,c,d,e) -> { return null; };
    
    public final static class RP {
        public RenderPoints rp;
        public int layer;
        public int type;
        
        RP(RenderPoints rp, int layer, int type) {
            this.rp = rp;
            this.layer = layer;
            this.type = type;
        }
    }
    
    public static RenderGroup parseRGP(String filename) throws IOException {
        if (RGPS.containsKey(filename))
            return RGPS.get(filename);
        
        System.out.println("parsing: "+filename);
        
        int state = START_STATE;
        String objectName;
        
        int layer = 0;
        int vertexCount = 0;
        int attribCount = 0;
        int[] attribSizes = null;
        int[] attribOffsets = null;
        int vertexDataSize = 0;
        
        float[] vertices = null;
        
        String objectType = "";
        int indexCount = 0;
        int[] indices = null;
        float[][] vertexData = null;
        String[] uniforms = null;
        
        ArrayList<RP> RPs = new ArrayList<>();
        
        String fullFilename = "src/resources/ships/"+filename;
        BufferedReader reader = null;
        
        try {
            FileReader fileReader = new FileReader(fullFilename);
            reader = new BufferedReader(fileReader);
            
            int vIndex = 0;
            int iIndex = 0;
            String sLine;
            
            while ((sLine = reader.readLine()) != null) {
                String[] parts = sLine.split(" ");
                
                if (parts.length > 0) {
                    switch (state) {
                        case START_STATE:
                            if (parts[0].equals("object"))
                                objectName = parts[1];
                            else if (parts[0].equals("start"))
                                state = STARTED_STATE;
                            break;
                            
                        case STARTED_STATE:
                            if (parts[0].equals("finish"))
                                state = START_STATE;
                            else if (parts[0].equals("begin"))
                                state = VERTEX_INFO_STATE;
                            break;
                            
                        case VERTEX_INFO_STATE:
                            if (parts[0].equals("layer")) 
                                layer = Integer.parseInt(parts[1]);
                            else if (parts[0].equals("vertex_count"))
                                vertexCount = Integer.parseInt(parts[1]);
                            else if (parts[0].equals("attrib_count")) {
                                attribCount = Integer.parseInt(parts[1]);
                                attribSizes = new int[attribCount];
                                attribOffsets = new int[attribCount];
                            }
                            else if (parts[0].equals("attrib_size")) {
                                int start = 0;
                                for (int i=0; i<attribCount; i++) {
                                    attribSizes[i] = Integer.parseInt(parts[i+1]);
                                    attribOffsets[i] = start;
                                    start += attribSizes[i];
                                }
                            }
                            else {
                                vertexDataSize = 0;
                                for (int size : attribSizes)
                                    vertexDataSize += size;
                                vertices = new float[vertexCount*vertexDataSize];
                                
                                vIndex = 0;
                                state = VERTEX_DEF_STATE;
                            }
                            break;
                            
                        case VERTEX_DEF_STATE:
                            if (parts[0].equals("v"))
                                for (int i=1; i<=vertexDataSize; i++)
                                    vertices[vIndex++] = (i<parts.length) ?
                                            Float.parseFloat(parts[i]) :
                                            0f;
                            else if (parts[0].equals("object")) {
                                objectType = parts[1];
                                state = OBJECT_INFO_STATE;
                            }
                            break;
                            
                        case OBJECT_INFO_STATE:
                            if (parts[0].equals("index_count")) {
                                indexCount = Integer.parseInt(parts[1]);
                                indices = new int[indexCount];
                            }
                            else if (parts[0].equals("attribs")) {
                                int usedAttribCount = parts.length-1;
                                vertexData = new float[usedAttribCount][];
                                for (int i=0; i<usedAttribCount; i++) {
                                    int attr = Integer.parseInt(parts[i+1]);
                                    int attrSize = attribSizes[attr];
                                    vertexData[i] = new float[vertexCount*attrSize];
                                    int start = attribOffsets[attr];
                                    int j = 0;
                                    for (int x=0; x<vertexCount; x++)
                                        for (int y=start; y<start+attrSize; y++)
                                            vertexData[i][j++] = vertices[(x*vertexDataSize)+y];
                                }
                            }
                            else if (parts[0].equals("uniforms")) {
                                uniforms = new String[parts.length-1];
                                System.arraycopy(parts, 1, uniforms, 0, parts.length-1);
                            }
                            else {
                                iIndex = 0;
                                state = OBJECT_INDEX_STATE;
                            }
                            break;
                            
                        case OBJECT_INDEX_STATE:
                            if (parts[0].equals("i"))
                                for (int i=1; i<parts.length; i++)
                                    indices[iIndex++] = Integer.parseInt(parts[i]);
                            else if (parts[0].equals("object")) {
                                RPs.add(handleObject(objectType, vertexData, indices, layer, uniforms));
                                objectType = parts[1];
                                state = OBJECT_INFO_STATE;
                            }
                            else if (parts[0].equals("end")) {
                                RPs.add(handleObject(objectType, vertexData, indices, layer, uniforms));
                                objectType = "";
                                state = STARTED_STATE;
                            }
                            break;
                    }
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Unable to open file: "+filename);
        } 
        catch(IOException e) {
            System.out.println("Error reading file: "+filename);
        }
        finally {
            if (reader != null) reader.close();
        }
        
        ArrayList<Integer> typeCount = new ArrayList<>();
        RPs.forEach((rp) -> {
            while (rp.type+1 > typeCount.size())
                typeCount.add(0);
            typeCount.set(rp.type, typeCount.get(rp.type)+1);
        });
        
        RenderPoints[][] rPoints = new RenderPoints[typeCount.size()][];
        int[][] layers = new int[typeCount.size()][];
        
        for (int i=0; i<typeCount.size(); i++) {
            int size = typeCount.get(i);
            rPoints[i] = new RenderPoints[size];
            layers[i] = new int[size];
        }
        
        typeCount.replaceAll(i -> {return 0;});
        
        RPs.forEach(rp -> {
            int type = rp.type;
            int index = typeCount.get(type);
            rPoints[type][index] = rp.rp;
            layers[type][index] = rp.layer;
            typeCount.set(type, index+1);
        });
        
        RenderGroup rGroup = new RenderGroup(rPoints, layers);
        RGPS.put(filename, rGroup);
        
        return rGroup;
    }
    
    private static RP handleObject(String type, float[][] vertexData, int[] indices, int layer,
            String[] uniforms) {
        switch (type) {
            case "Mesh":
                Vector3f meshColour = new Vector3f();
                for (int i=0; i<uniforms.length; i++) {
                    if (uniforms[i].equals("colour")) {
                        meshColour.x = Float.parseFloat(uniforms[++i]);
                        meshColour.y = Float.parseFloat(uniforms[++i]);
                        meshColour.z = Float.parseFloat(uniforms[++i]);
                    }
                }
                
                Mesh mesh = new Mesh(vertexData[0],indices, meshColour);
                return new RP(mesh, layer, 0);
                
            case "Line":
                Vector3f lineColour = new Vector3f();
                int lineWidth = 0;
                for (int i=0; i<uniforms.length; i++) {
                    if (uniforms[i].equals("colour")) {
                        lineColour.x = Float.parseFloat(uniforms[++i]);
                        lineColour.y = Float.parseFloat(uniforms[++i]);
                        lineColour.z = Float.parseFloat(uniforms[++i]);
                    }
                    else if (uniforms[i].equals("width")) 
                        lineWidth = Integer.parseInt(uniforms[++i]);
                }
                
                Line line = new Line(vertexData[0],vertexData[1],indices, lineWidth, lineColour);
                return new RP(line, layer, 1);
        }
        
        return objectHandler.handle(type, vertexData, indices, layer, uniforms);
    }
    
    public static void setHandler(Handler handler) {
        objectHandler = handler;
    }
}
