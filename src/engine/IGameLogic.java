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
public interface IGameLogic {
    void init() throws Exception;
    void windowSizeCallback(int width, int height);
    void keyCallback(int key, int scancode, int action, int mods); // handle key presses
    void cursorPosCallback(double xpos, double ypos); // handle mouse movements
    void cursorEnterCallback(boolean entered); // handle window cursor entry
    void mouseButtonCallback(int button, int action, int mods); // handle mouse button presses
    void input(Window window);
    void update(float interval);
    void render(Window window);
    void cleanUp();
}
