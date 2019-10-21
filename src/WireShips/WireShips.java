/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import static org.lwjgl.glfw.GLFW.*;
import engine.Window;
import engine.IGameLogic;
import engine.Camera;
import engine.RenderGroup;
import engine.Text;
import engine.Timer;
import engine.Utils;
import engine.Utils.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class WireShips implements IGameLogic {

    // Input / Update Variables:
    //   -Camera Variables
    private int windowHeight = -1;
    private int windowWidth = -1;
    private int sDirection = 0;
    private int rDirection = 0;
    private int xDirection = 0;
    private int yDirection = 0;
    private final static float MOVE_SPEED = 8f;
    private final static float ZOOM_SPEED = 0.03f;
    private final static float ROTATION_SPEED = 3f;
    private final static float MAX_SCALE = 5.0f;
    private final static float MIN_SCALE = 0.1f;
    //   -MouseClick Variables
    private boolean inFocus = true;
    private Vector2f currentWinPos = new Vector2f();
    private Vector2d initialPos = new Vector2d();
    private Vector2d finalPos = new Vector2d();

    // Sim control variables
    private final static int SIM_STEP_MILLISECONDS = 1000/120;
    private float prevUpdateInterval = 0;
    private boolean run = false;
    private boolean pauseWaiting = true;
    private boolean step = false;
    private boolean stepWaiting = true;
    
    // timer for periodic events
    private final Timer markerTimer;

    private final Renderer renderer;
    private final Camera camera;
    private final Map<String, RenderObject> renderObs;
    private final List<RenderObject> renderObjects;
    private Ship ship1;
    private Ship ship2;
    private Helmsman driver;
    private RenderGroup test;
    private RenderGroup RGPpointer;
    private ArrayList<RenderGroup> RGPpointers;
    private Text text;

    public WireShips() {
        renderer = new Renderer();
        camera = new Camera();
        renderObjects = new ArrayList<>();
        renderObs = new HashMap<>();
        RGPpointers = new ArrayList<>();
        markerTimer = new Timer();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        
        test = engine.Parser.parseRGP("iowa.rgp");
        test.setPosition(200,200,0);
        test.scale(10);
        
        Text.parseLetters("letters.txt");
        
        text = new Text("had");
        text.setPosition(100, -100, 0);
        
        RGPpointer = engine.Parser.parseRGP("pointer.rgp");
        
        ship1 = new Ship(test, 0);
        ship2 = new Ship(test, 0);
        ship2.setPosition(100, 0, 0);
        
        driver = new Helmsman(new Vector3f(100,0,0));
        driver.setShip(ship2);
        
        markerTimer.init();
        
        
    }
    
    @Override
    public void windowSizeCallback(int width, int height) {
        windowWidth = width;
        windowHeight = height;
    }

    @Override
    public void keyCallback(int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {        
            switch (key) {
                case GLFW_KEY_I:
                    if (mods == GLFW_MOD_SHIFT) ship1.setEngine(1f);
                    else ship1.shiftEngine(0.1f);
                    break;
                case GLFW_KEY_K:
                    if (mods == GLFW_MOD_SHIFT) ship1.setEngine(-1f);
                    else ship1.shiftEngine(-0.1f);
                    break;
                case GLFW_KEY_B:
                    ship1.setEngine(0.0f);
                    break;
                case GLFW_KEY_J:
                    if (mods == GLFW_MOD_SHIFT) ship1.setRudder(1f);
                    else ship1.shiftRudder(0.1f);
                    break;
                case GLFW_KEY_L:
                    if (mods == GLFW_MOD_SHIFT) ship1.setRudder(-1f);
                    else ship1.shiftRudder(-0.1f);
                    break;
                case GLFW_KEY_N:
                    ship1.setRudder(0.0f);
                    break;
            }
        }
    }
    
    @Override
    public void cursorPosCallback(double xpos, double ypos) {
        currentWinPos.x = (float) xpos;
        currentWinPos.y = (float) ypos;
    }
    
    @Override
    public void cursorEnterCallback(boolean entered) {
        inFocus = entered;
    }
    
    @Override
    public void mouseButtonCallback(int button, int action, int mods) {
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
            Vector2f blam = getWorldCursorPosition();
            
            RenderGroup rgp = new RenderGroup(RGPpointer);
            rgp.setPosition(blam.x,blam.y, 0f);
            RGPpointers.add(rgp);
            
            driver.addToRoute(blam.x, blam.y);
            driver.startRoute();
        }
    }
    
    @Override
    public void input(Window window) {
        // Camera zooming control
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            sDirection = -1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            sDirection = 1;
        } else {
            sDirection = 0;
        }

        // Camera vertical movement control
        if (window.isKeyPressed(GLFW_KEY_W)) {
            yDirection = 1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            yDirection = -1;
        } else {
            yDirection = 0;
        }

        // Camera horizontal moevement control
        if (window.isKeyPressed(GLFW_KEY_A)) {
            xDirection = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            xDirection = 1;
        } else {
            xDirection = 0;
        }

        // Simulation run controls
        if (window.isKeyPressed(GLFW_KEY_SPACE) && pauseWaiting) {
            run = !run;
            System.out.println(run ? "Running" : "Paused");
            pauseWaiting = false;
        } else if (!window.isKeyPressed(GLFW_KEY_SPACE) && !pauseWaiting) {
            pauseWaiting = true;
        }
        if (window.isKeyPressed(GLFW_KEY_PERIOD) && stepWaiting) {
            step = true;
            stepWaiting = false;
        } else if (!window.isKeyPressed(GLFW_KEY_PERIOD) && !stepWaiting) {
            stepWaiting = true;
        }
    }

    @Override
    public void update(float interval) {
        float scale = camera.getScale();
        scale += sDirection * ZOOM_SPEED;
        scale = Utils.clamp(scale, MIN_SCALE, MAX_SCALE);
        
        float rotation = camera.getRotation();
        rotation += rDirection * ROTATION_SPEED;
        rotation = Utils.clamp(rotation, -180, 180);
        
        float dX = xDirection * scale * MOVE_SPEED;
        float dY = yDirection * scale * MOVE_SPEED;

        camera.addPosition(dX, dY);
        camera.setScale(scale);
        camera.setRotation(rotation);

        if (run) prevUpdateInterval += interval;
        if (prevUpdateInterval * 1000 >= SIM_STEP_MILLISECONDS || step) {
            ship1.update(prevUpdateInterval);
            
            driver.update(prevUpdateInterval);
            ship2.update(prevUpdateInterval);
            
            step = false;
            prevUpdateInterval = 0f;
            
            if (markerTimer.hasPassed(3f)) {
                RenderGroup p = new RenderGroup(RGPpointer);
                p.setPosition(ship1.getPosition());
                RGPpointers.add(p);
            }
        }
    }

    @Override
    public void render(Window window) {
        Pair<Matrix4f,Matrix4f> matrices = engine.Renderer.prepRender(window, camera);
        Matrix4f PVMatrix = matrices.a;
        Matrix4f NMatrix = matrices.b;
        renderer.prepRender(window, camera);
        
        renderObjects.forEach(r -> {
            renderer.render(r);
        });
        
        text.render(PVMatrix, NMatrix);
        
        ship1.render(PVMatrix, NMatrix);
        ship2.render(PVMatrix, NMatrix);
        
        if (RGPpointers.size() > 0)
            RGPpointers.forEach(rgp -> rgp.nestedRender(PVMatrix, NMatrix));
        
        RGPpointer.nestedRender(PVMatrix, NMatrix);
        
        test.nestedRender(PVMatrix, NMatrix);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        RenderObject.cleanUp();
        RenderGroup.cleanUp();
    }
    
    private Vector2f getWorldCursorPosition() {
        float halfWidth = windowWidth / 2.0f;
        float halfHeight = windowHeight / 2.0f;
        
        float scale = camera.getScale();
        Vector2f position = camera.getPosition();
        
        Vector2f winPos = new Vector2f(currentWinPos);
        
        winPos.x -= halfWidth;
        winPos.y -= halfHeight;
        winPos.y *= -1;
        winPos.mul(scale);
        winPos.add(position);
        
        return winPos;
    }
}
