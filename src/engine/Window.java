/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author ffgi
 */
public class Window {

    private final String title;
    private int width;
    private int height;
    private long handle;
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWWindowSizeCallback windowSizeCallback;
    private boolean resized;
    private boolean vSync;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.resized = false;
    }

    @FunctionalInterface
    interface InputConsumer<A, B, C, D, E> {
        public void accept(A a, B b, C c, D d, E e);
    }

    public void init(IGameLogic logic) {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialise GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwSetWindowSizeCallback(handle, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                Window.this.setResized(true);
                logic.windowSizeCallback(width, height);
            }
        });

        /*glfwSetKeyCallback(handle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
                logic.handle(key, scancode, action, mods);
            }
        });*/
        
        glfwSetKeyCallback(handle, 
                (windowHandle, key, scancode, action, mods) -> {
                    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                        glfwSetWindowShouldClose(windowHandle, true);
                    }
                    logic.keyCallback(key, scancode, action, mods);
                });
        
        glfwSetCursorPosCallback(handle,
                (windowHandle, xpos, ypos) -> {
                    logic.cursorPosCallback(xpos, ypos);
                });
        
        glfwSetCursorEnterCallback(handle,
                (windowHandle, entered) -> {
                    logic.cursorEnterCallback(entered);
                });
        
        glfwSetMouseButtonCallback(handle,
                (windowHandle, button, action, mods) -> {
                    logic.mouseButtonCallback(button, action, mods);
                });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(handle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        glfwMakeContextCurrent(handle);

        if (isvSync()) {
            glfwSwapInterval(1);
        }

        glfwShowWindow(handle);

        GL.createCapabilities();
        
        System.out.println(glGetString(GL_VERSION));

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r,g,b,alpha);
    }

    public boolean isKeyPressed (int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public String getTitle() { return title; }

    public long getWindowHandle() { return handle; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isResized() { return resized; }
    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() { return vSync; }
    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }
}