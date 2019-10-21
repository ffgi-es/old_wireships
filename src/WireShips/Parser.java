/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Line;
import engine.Mesh;
import engine.Utils;
import java.io.*;
import java.util.Map;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Parser {
    
    public static RenderObject parseROFile(String fileName) throws Exception {
        System.out.println("parsing: "+fileName);
        float[] meshVertices = null;
        float[] lineVertices = null;
        float scale = 1f;
        float[] lineNormals = null;
        int[] meshIndices = null;
        int[] lineIndices = null;
        int vj = 0;
        int nj = 0;
        int mij = 0;
        int lij = 0;
        String sLine = null;
        String fullFileName = "src/resources/ships/"+fileName;
        try {
            FileReader fileReader = new FileReader(fullFileName);
            BufferedReader reader = new BufferedReader(fileReader);
            
            while ((sLine = reader.readLine()) != null) {
                String[] parts = sLine.split(" ");
                switch (parts[0]) {
                    case "o":
                        break;
                    case "s":
                        scale = Float.parseFloat(parts[1]);
                        break;
                    case "c":
                        int cv = Integer.parseInt(parts[1]);
                        int cmi = Integer.parseInt(parts[2]);
                        int cli = Integer.parseInt(parts[3]);
                        meshVertices = new float[cv * 3];
                        lineVertices = new float[cv * 3];
                        lineNormals = new float[cv * 3];
                        meshIndices = new int[cmi];
                        lineIndices = new int[cli];
                        break;
                    case "v":
                        meshVertices[vj] = Float.parseFloat(parts[1]) * scale;
                        lineVertices[vj++] = Float.parseFloat(parts[1]) * scale;
                        meshVertices[vj] = Float.parseFloat(parts[2]) * scale;
                        lineVertices[vj++] = Float.parseFloat(parts[2]) * scale;
                        if (parts.length > 3) {
                            meshVertices [vj] = Float.parseFloat(parts[3]);
                            lineVertices [vj++] = Float.parseFloat(parts[3]) + 0.1f;
                        } else {
                            meshVertices[vj] = 0.0f;
                            lineVertices[vj++] = 0.1f;
                        }
                        break;
                    case "n":
                        lineNormals[nj++] = Float.parseFloat(parts[1]);
                        lineNormals[nj++] = Float.parseFloat(parts[2]);
                        lineNormals[nj++] = 0.0f;
                        break;
                    case "mi":
                        meshIndices[mij++] = Integer.parseInt(parts[1]);
                        meshIndices[mij++] = Integer.parseInt(parts[2]);
                        meshIndices[mij++] = Integer.parseInt(parts[3]);
                        break;
                    case "li":
                        lineIndices[lij++] = Integer.parseInt(parts[1]);
                        break;
                }
            }
            reader.close();
        } catch(FileNotFoundException e) {
            System.out.println("Unable to open file: "+fileName);
        } catch(IOException e) {
            System.out.println("Error reading file: "+fileName);
        }
        
        // Calculate line normals if not provided
        if ((lineNormals[0]+lineNormals[1] < 0.8)) {
            float[] lineVerticesIndexed = new float[2 * lineIndices.length];
            
            for (int i=0; i<lineIndices.length; i++) {
                lineVerticesIndexed[2*i]   = lineVertices[3*lineIndices[i]];
                lineVerticesIndexed[2*i+1] = lineVertices[3*lineIndices[i]+1];
            }
            
            float[] lineNormalsIndexed = Utils.lineNormalsAdjusted(lineVerticesIndexed);
            
            for (int i=0; i<lineIndices.length; i++) {
                lineNormals[3*lineIndices[i]]   = lineNormalsIndexed[2*i];
                lineNormals[3*lineIndices[i]+1] = lineNormalsIndexed[2*i+1];
            }
        }
        
        Mesh mesh = new Mesh(meshVertices, meshIndices, new Vector3f(1,1,1));
        Line line = new Line(lineVertices, lineNormals, lineIndices, 1, new Vector3f(1,1,1));
        
        RenderObject result = new RenderObject(mesh,line);
        
        return result;
    }
    
    public static RenderCollection parseRCFile(Map<String,RenderObject> m, String fileName)
            throws Exception {
        String[] renderObjectNames = null;
        int roj = 0;
        
        String fullFileName = "src/resources/ships/"+fileName;
        
        try {
            FileReader fileReader = new FileReader(fullFileName);
            BufferedReader reader = new BufferedReader(fileReader);
            
            String sLine;
            while ((sLine = reader.readLine()) != null) {
                String[] parts = sLine.split(" ");
                switch(parts[0]) {
                    case "o":
                        break;
                    case "c":
                        renderObjectNames = new String[Integer.parseInt(parts[1])];
                        break;
                    case "ro":
                        if (parts.length > 1) renderObjectNames[roj++] = parts[1];
                        else roj++;
                        break;
                }
            }
            reader.close();
        } catch(FileNotFoundException e) {
            System.out.println("Unable to open file: "+fileName);
        } catch(IOException e) {
            System.out.println("Error reading file: "+fileName);
        }
        
        RenderObject[] renderObjects = new RenderObject[renderObjectNames.length];
        
        for (int i=0; i<renderObjects.length; i++) {
            String name = renderObjectNames[i];
            if (name == null) continue;
            if (!m.containsKey(name)) m.put(name, parseROFile(name+".ro"));
            renderObjects[i] = new RenderObject(m.get(name));
        }
        
        return new RenderCollection(renderObjects);
    }
}
