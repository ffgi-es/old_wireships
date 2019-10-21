/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author ffgi
 */
public class Texture {
    private final int id;
    private final int colNum;
    private final int rowNum;

    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName), 1, 1);
    }

    public Texture(String fileName, int rowNum, int colNum) throws Exception {
        this(loadTexture(fileName), rowNum, colNum);
    }

    public Texture(int id, int rowNum, int colNum) { 
        this.id = id; 
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() { return id; }
    public int getRowNum() { return rowNum; }
    public int getColNum() { return colNum; }

    private static int loadTexture(String fileName) throws Exception {
        //InputStream in = new FileInputStream("testTexture.png");
        InputStream in = Texture.class.getClass().getResourceAsStream(fileName);
        PNGDecoder decoder = new PNGDecoder(in);
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
        buf.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }

    public void cleanUp() {
        glDeleteTextures(id);
    }
}
