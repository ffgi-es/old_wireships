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
import java.util.HashMap;
import org.joml.Matrix4f;

/**
 *
 * @author ffgi
 */
public class Text extends PositionGroup{
    private static final HashMap<Character,Letter> symbols = new HashMap<>();
    
    private final String content;
    private final Letter[] letters;
    
    public Text(String text) {
        content = text;
        letters = new Letter[text.length()];
        
        for (int i=0; i<text.length(); i++) {
            letters[i] = new Letter(symbols.get(text.charAt(i)));
            if (i>0)
                letters[i].setPositionAfter(letters[i-1]);
        }
    }
    
    public void render(Matrix4f PVMatrix, Matrix4f NMatrix) {
        for (Letter l : letters) {
            Matrix4f thisPVWM = new Matrix4f(PVMatrix).mul(
                Transformation.getWorldMatrix(this));
            Matrix4f thisNM = new Matrix4f(NMatrix).mul(
                Transformation.getNormalMatrix(this));
            
            l.nestedRender(thisPVWM, thisNM);
        }
    }
    
    public static void parseLetters(String filename) {
        System.out.println("parsing: "+filename);
        
        String fullFilename = "src/resources/"+filename;
        BufferedReader reader = null;
        
        try {
            FileReader fileReader = new FileReader(fullFilename);
            reader = new BufferedReader(fileReader);
            
            String sLine;
            
            while ((sLine = reader.readLine()) != null) {
                String[] parts = sLine.split(" ");
                char letter = parts[0].charAt(0);
                symbols.put(letter, new Letter(letter, Parser.parseRGP(parts[1])));
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Unable to open file: "+filename);
        } 
        catch(IOException e) {
            System.out.println("Error reading file: "+filename);
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException e) {
                System.out.println("Couldn't close reader for: "+filename);
            }
        }
    }
}
