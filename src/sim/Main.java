package sim;

import sim.rover.*;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import sim.cam.Cam;
import sim.scenario.Scenario;

/**
 * test
 * @author joemirizio
 */
public class Main extends SimpleApplication implements ActionListener, SceneProcessor {

	public static AssetManager ASSET_MANAGER;
	public static Node ROOT_NODE;
	
	static final int APP_RES_X = 1024, APP_RES_Y = 768;
	static final boolean APP_VSYNC = true, APP_FULLSCREEN = false;
	public static final float INCH_PER_FOOT = 1f / 12f;
	
        public Scenario scenario;
        
        /** Flooring **/
        public static Geometry floor;
        public static Geometry prediction_line;
        public static Geometry alert_zone;
        public static Geometry safe_zone;
        public static boolean show_cam_point = true;
        public static boolean show_cam_frust = true;
        
        /** Cameras**/
	public static Cam cam1;
	public static Cam cam2;
	//public static final Vector3f cam_1_DEFAULT_LOC = new Vector3f(0.3f, -0.75f, 0.55f);
	//public static final Vector3f cam_2_DEFAULT_LOC = new Vector3f(-0.3f, -0.75f, 0.55f);
	public static final Vector3f cam_1_DEFAULT_LOC = new Vector3f(0.4f, -0.7f, 0.55f);
	public static final Vector3f cam_2_DEFAULT_LOC = new Vector3f(-0.4f, -0.7f, 0.55f);

	// Camera processing
	private FrameBuffer nfb;
	//private ByteBuffer bb;
	//private BufferedImage bi;
	
	Spatial test_child = null;
	
    public static void main(String[] args) {
        /** Application Settings **/
        AppSettings settings = new AppSettings(true);
        settings.setResolution(APP_RES_X, APP_RES_Y);
        settings.setVSync(APP_VSYNC);
        settings.setTitle("S.M.E.E - Simulated Model for Emergency Evacuations");
        settings.setFullscreen(APP_FULLSCREEN);

        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    }

    @Override
    public void simpleInitApp() {
        // Statically define for convenience
        Main.ASSET_MANAGER = assetManager;
        Main.ROOT_NODE = rootNode;

        // Background color
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		
        // Initialize Controls and GUI
        this.initControls();
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        guiNode.detachAllChildren();
        
        // Lighting
        /** Basic shadow for even surfaces */ 
        BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 256);
        bsr.setDirection(new Vector3f(0f,-1f,0f).normalizeLocal());
        viewPort.addProcessor(bsr);

        // Initialize Main Camera
        flyCam.setMoveSpeed(20);
        flyCam.setEnabled(true);
        cam.setLocation(new Vector3f(20, 20, -20)); 
        cam.lookAt(new Vector3f(-5, 0, 5), Vector3f.UNIT_Y);

        /** Setup Test Area **/
        // Floor
        floor = new Geometry("Floor", new Box(Vector3f.ZERO, 15, 0.1f, 15));
        floor.setLocalTranslation(Vector3f.ZERO);
        floor.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(floor);
        
        // Prediction Zone
        Cylinder prediction_line_cylinder = new Cylinder(10, 30, 12, 0.3f, true);
        prediction_line_cylinder.setMode(Mode.Lines);
        prediction_line = new Geometry("PredictionZone", prediction_line_cylinder);
        prediction_line.rotate(FastMath.HALF_PI, 0, 0);
        prediction_line.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(prediction_line);
        
        // Alert Zone
        alert_zone = new Geometry("AlertZone", new Cylinder(2, 30, 10, 0.5f, true));
        alert_zone.rotate(FastMath.HALF_PI, 0, 0);
        alert_zone.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(alert_zone);
        
        // Safe Zone
        safe_zone = new Geometry("SafeZone", new Cylinder(2, 30, 5, 0.6f, true));
        safe_zone.rotate(FastMath.HALF_PI, 0, 0);
        safe_zone.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(safe_zone);
        
        // 120 degrees boundries (+/- 60 degrees)
        float dist = 12;
        Node bounds = new Node();
        bounds.move(0, 1, 0);
        rootNode.attachChild(bounds);
        Debug.attachArrow(bounds, new Vector3f(FastMath.sin(FastMath.DEG_TO_RAD * 60) * dist, 1, FastMath.cos(FastMath.DEG_TO_RAD * 60) * dist), ColorRGBA.Yellow, "Bound1");
        Debug.attachArrow(bounds, new Vector3f(-FastMath.sin(FastMath.DEG_TO_RAD * 60) * dist, 1, FastMath.cos(FastMath.DEG_TO_RAD * 60) * dist), ColorRGBA.Yellow, "Bound2");

        /** Initilize Cameras **/
        cam1 = new Cam("cam1");
        rootNode.attachChild(cam1.getMainNode());
        
        cam2 = new Cam("cam2");
        rootNode.attachChild(cam2.getMainNode());

        /** Camera Viewports **/
        cam1.getCamera().setViewPort(0f, 0.625f, 0f, 0.625f);
        ViewPort cam_vp2 = renderManager.createMainView("cam1", cam1.getCamera());
        cam_vp2.setClearFlags(true, true, true);
        cam_vp2.attachScene(rootNode);
        cam_vp2.setBackgroundColor(ColorRGBA.Pink);
        //cam_vp2.addProcessor(bsr);

        cam2.getCamera().setViewPort(1.0f, 1.625f, 0f, 0.625f);
        ViewPort cam_vp1 = renderManager.createMainView("cam2", cam2.getCamera());
        cam_vp1.setClearFlags(true, true, true);
        cam_vp1.attachScene(rootNode);
        cam_vp1.setBackgroundColor(ColorRGBA.Pink);
        //cam_vp1.addProcessor(bsr);
        
        
        // Set default scenario
        scenario = new Scenario(this);
        scenario.runScenario(Scenario.DEFAULT_SCENARIO);

        //bb = BufferUtils.createByteBuffer(cam.RES_X * cam.RES_Y * 4);
        //bi = new BufferedImage(cam.RES_X, cam.RES_Y, BufferedImage.TYPE_4BYTE_ABGR);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Check if test child is in camera view
        /*if (test_child != null) {
            if (cam1.getCamera().containsGui(test_child.getWorldBound())) {
                    System.out.println("Contains: " + test_child.getName());
            } else {
                    System.out.println("Not Contains:" + test_child.getName());
            }
        }*/
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * Initializes the simulation controls
     */
    public void initControls() {
        // Camera Controls
        inputManager.addMapping("CREATE_SLOW", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("CREATE_FAST", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("CREATE_ARC", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("SPACE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("RESET", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addMapping("SPEED_UP", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping("SPEED_UP", new KeyTrigger(KeyInput.KEY_EQUALS));
        inputManager.addMapping("SPEED_DOWN", new KeyTrigger(KeyInput.KEY_MINUS));

        inputManager.addMapping("LEFT_CLICK", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("RIGHT_CLICK", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        inputManager.addMapping("PREV_SCENARIO", new KeyTrigger(KeyInput.KEY_LBRACKET));
        inputManager.addMapping("NEXT_SCENARIO", new KeyTrigger(KeyInput.KEY_RBRACKET));
        
        inputManager.addMapping("TOP_DOWN_VIEW", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("TOGGLE_CAM_POINT", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("TOGGLE_CAM_FRUST", new KeyTrigger(KeyInput.KEY_F));

        inputManager.addListener(this, new String[]{
            "CREATE_SLOW", "CREATE_FAST", "CREATE_ARC", 
            "SPEED_NORMAL", "SPEED_UP", "SPEED_DOWN",
            "SPACE", "RESET", "LEFT_CLICK", "RIGHT_CLICK",
            "PREV_SCENARIO", "NEXT_SCENARIO",
            "TOP_DOWN_VIEW", "TOGGLE_CAM_POINT", "TOGGLE_CAM_FRUST"
        });

        /** Debug **/
        inputManager.addMapping("DEBUG_PHYSICS", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, new String[]{"DEBUG_PHYSICS"});
    }

    private Spatial createTestChild(TestChild.Type test_child_type) {
        // Load and scale model
        Spatial child = assetManager.loadModel("Models/mindstorm.j3o");
        child.scale(0.25f);
        child.setLocalTranslation(new Vector3f(0, 0.3f, 0));
        child.setShadowMode(ShadowMode.Cast);
        // Set respective control
        TestChildControl child_control = null;
        if (test_child_type.equals(TestChild.Type.TangentialArc)) {
            child_control = new TangenialArcControl(test_child_type.getSpeed(), -FastMath.QUARTER_PI / 2);
        } else {
            child_control = new TestChildControl(test_child_type.getSpeed(), FastMath.rand.nextFloat() * (FastMath.PI / 3 * 2) - FastMath.PI / 3);
        }
        child.addControl(child_control);

        rootNode.attachChild(child);
        return child;
    }

    /**
     * Input action handler
     * @param name The name of the action
     * @param is_pressed True if the button is pressed down
     * @param tpf The time per frame
     */
    public void onAction(String name, boolean is_pressed, float tpf) {
        // Move cameras
        if (name.equals("LEFT_CLICK")) {
            cam1.lookAt(cam.getDirection());
        } else if (name.equals("RIGHT_CLICK")) {
            cam2.lookAt(cam.getDirection());
        }

        // Reset camera views
        if (name.equals("RESET") && is_pressed) {
            scenario.runScenario();
        }

        // Reset camera views
        if (name.equals("SPEED_NORMAL") && is_pressed) {
                TestChild.speed_multiplier = 1;
                System.out.println("Simulation Speed: " + TestChild.speed_multiplier);
        } else if (name.equals("SPEED_UP") && is_pressed) {
                TestChild.speed_multiplier++;
                System.out.println("Simulation Speed: " + TestChild.speed_multiplier);
        } else if (name.equals("SPEED_DOWN") && is_pressed) {
                TestChild.speed_multiplier--;
                System.out.println("Simulation Speed: " + TestChild.speed_multiplier);
        }

        // Create test children
        if (name.equals("CREATE_SLOW") && is_pressed) {
                test_child = this.createTestChild(TestChild.Type.SlowRadial);
        } else if (name.equals("CREATE_FAST") && is_pressed) {
                test_child = this.createTestChild(TestChild.Type.FastRadial);
        } else if (name.equals("CREATE_ARC") && is_pressed) {
                test_child = this.createTestChild(TestChild.Type.TangentialArc);
        }
        
        // Change scenarios
        if (name.equals("PREV_SCENARIO") && is_pressed) {
            int scenario_num = scenario.runNextScenario();
        } else if (name.equals("NEXT_SCENARIO") && is_pressed) {
            int scenario_num = scenario.runPrevScenario();
        }
        
        if (name.equals("TOP_DOWN_VIEW") && is_pressed) {
            cam.setLocation(new Vector3f(0, 25, -2));   //top down view of flycam
            cam.lookAt(new Vector3f(0, 0, 1.5f), Vector3f.UNIT_Y);   //looking at origin
        }
        
        if (name.equals("TOGGLE_CAM_POINT") && is_pressed) {
            show_cam_point = !show_cam_point;
            cam1.lookAt(cam1.getCamera().getDirection());
            cam2.lookAt(cam2.getCamera().getDirection());
        }
        if (name.equals("TOGGLE_CAM_FRUST") && is_pressed) {
            show_cam_frust = !show_cam_frust;
            cam1.lookAt(cam1.getCamera().getDirection());
            cam2.lookAt(cam2.getCamera().getDirection());
        }
        // Process camera viewport
        /*if (name.equals("SPACE1") && is_pressed) {

            System.out.println("cam 1: " + cam1.getCamera().getDirection());
            System.out.println("cam 2: " + cam2.getCamera().getDirection());

            System.out.println("Hit space!");

            int width = cam.RES_X;
            int height = cam.RES_Y;

            //ViewPort vp = renderManager.getMainViews().get(0);
            ViewPort vp = renderManager.getMainView("cam1Process");
            ViewPort cam2_vp = renderManager.getMainView("cam2");
            if (vp == null) {
                System.out.println("New viewport");

                vp = renderManager.createMainView("cam1Process", cam1.getCamera());
                //vp.setBackgroundColor(ColorRGBA.DarkGray);
                vp.setClearFlags(true, true, true);

                nfb = new FrameBuffer(width, height, 1);
                nfb.setColorBuffer(Format.RGBA8); 
                nfb.setDepthBuffer(Format.Depth);

                Texture2D txt = new Texture2D(width, height, Format.RGBA8);
                nfb.setColorTexture(txt);
                vp.setOutputFrameBuffer(nfb);
                vp.addProcessor(this);

                vp.attachScene(rootNode);
            }

            //if (nfb != null) {				
                try {
                    File f = new File("/Users/joemirizio/Desktop/image.png");
                    ImageIO.write(bi, "png", f);
                    System.out.println("Updated image file.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
        }*/
    }

    public void initialize(RenderManager rm, ViewPort vp) {
    }

    public void reshape(ViewPort vp, int w, int h) {
    }

    public boolean isInitialized() {
            return true;
    }

    public void preFrame(float tpf) {
    }

    public void postQueue(RenderQueue rq) {
    }

    public void postFrame(FrameBuffer out) {
        /*bb.clear();
        renderer.readFrameBuffer(out, bb);
        synchronized (bi) {
            Screenshots.convertScreenShot(bb, bi);
        }

        System.out.println("- Update fb");*/
    }

    public void cleanup() {
    }
}
