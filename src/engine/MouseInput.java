/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author ffgi
 */
public class MouseInput {

    private final Vector2d prevPos;
    private final Vector2d currPos;
    private final Vector2f deltaPos;

    private boolean inWindow;
    private boolean button1_pressed;
    private boolean button2_pressed;
    private boolean shift_click;

    public MouseInput() {
        prevPos = new Vector2d(-1, -1);
        currPos = new Vector2d(0, 0);
        deltaPos = new Vector2f();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(),
                (windowHandle, xPos, yPos) -> {
                    currPos.x = xPos;
                    currPos.y = yPos;
                });

        glfwSetCursorEnterCallback(window.getWindowHandle(),
                (windowHandle, entered) -> {
                    inWindow = entered;
                });

        glfwSetMouseButtonCallback(window.getWindowHandle(),
                (windowHandle, button, action, mods) -> {
                    button1_pressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
                    button2_pressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
                    shift_click = mods == GLFW_MOD_SHIFT;
                });
    }

    public void input(Window window) {
        deltaPos.x = 0;
        deltaPos.y = 0;

        if (prevPos.x > 0 && prevPos.y > 0 && inWindow) {
            deltaPos.x = (float) (currPos.x - prevPos.x);
            deltaPos.y = (float) (currPos.y - prevPos.y);

            prevPos.x = currPos.x;
            prevPos.y = currPos.y;
        }
    }

    public Vector2d getCursPos() { return new Vector2d(currPos); }

    public Vector2f getDeltaPos() { return new Vector2f(deltaPos); }

    public boolean isButton1Pressed() { return button1_pressed; }
    public boolean isButton2Pressed() { return button2_pressed; }
    public boolean isShiftClick() { return shift_click;}
    
}
