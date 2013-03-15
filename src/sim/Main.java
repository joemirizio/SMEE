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
import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import sim.cam.Cam;

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

    private Cam cam1;
    private Cam cam2;

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
    private Light Light;

    @Override
    public void simpleInitApp() {
        // Statically define for convenience
        Main.ASSET_MANAGER = assetManager;
        Main.ROOT_NODE = rootNode;

        // Background color
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        // Lighting
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(10f));         // light color
        spot.setPosition(cam.getLocation());               // shine from camera loc
        spot.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));    // shine forward from camera loc
        rootNode.addLight(spot); 

        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(100f);                           // distance
        spot1.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot1.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot1.setColor(ColorRGBA.Red.mult(10f));         // light color
        spot1.setPosition(cam.getLocation());               // shine from camera loc
        spot1.setDirection(new Vector3f(-0.4f, -1f, 0.55f));      // shine forward from camera loc
        rootNode.addLight(spot1); 
                
        
        // Initialize Controls and GUI
        this.initControls();
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        guiNode.detachAllChildren();

        // Initialize Main Camera
        flyCam.setMoveSpeed(20);
        flyCam.setEnabled(true);
        cam.setLocation(new Vector3f(20, 20, -20));
        cam.lookAt(new Vector3f(-5, 0, 5), Vector3f.UNIT_Y);

        /** Setup Test Area **/
        Material unshaded_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_mat.setColor("Color", ColorRGBA.Blue);
        
        // Floor
        Geometry floor = new Geometry("Floor", new Box(Vector3f.ZERO, 15, 0.1f, 15));
        floor.setLocalTranslation(Vector3f.ZERO);
        floor.setMaterial(unshaded_mat);
        rootNode.attachChild(floor);
        
        // Prediction Zone
        Material prediction_line_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        //prediction_line_mat.setColor("Color", ColorRGBA.Gray);
        prediction_line_mat.setBoolean("UseMaterialColors",true);  // needed for shininess
        prediction_line_mat.setColor("Specular", ColorRGBA.White); // needed for shininess
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.White); // needed for shininess
        prediction_line_mat.setFloat("Shininess", 20f); // shininess from 1-128
        Cylinder prediction_line_cylinder = new Cylinder(10, 30, 12, 0.3f, true);
        prediction_line_cylinder.setMode(Mode.Lines);
        Geometry prediction_line = new Geometry("PredictionZone", prediction_line_cylinder);
        prediction_line.rotate(FastMath.HALF_PI, 0, 0);
        prediction_line.setMaterial(prediction_line_mat);
        rootNode.attachChild(prediction_line);
        // Alert Zone
        Material alert_zone_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //alert_zone_mat.setColor("Color", ColorRGBA.Red); //this line to become setTexture? file to pull in?
        alert_zone_mat.setBoolean("UseMaterialColors",true);  // needed for shininess
        alert_zone_mat.setColor("Specular", ColorRGBA.White); // needed for shininess
        alert_zone_mat.setColor("Diffuse",  ColorRGBA.White); // needed for shininess
        alert_zone_mat.setFloat("Shininess", 20f); // shininess from 1-128
        Geometry alert_zone = new Geometry("AlertZone", new Cylinder(2, 30, 10, 0.5f, true));
        alert_zone.rotate(FastMath.HALF_PI, 0, 0);
        alert_zone.setMaterial(alert_zone_mat);
        rootNode.attachChild(alert_zone);
        // Safe Zone
        Material safe_zone_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        //safe_zone_mat.setColor("Color", ColorRGBA.Green);
        safe_zone_mat.setBoolean("UseMaterialColors",true);  // needed for shininess
        safe_zone_mat.setColor("Specular", ColorRGBA.White); // needed for shininess
        safe_zone_mat.setColor("Diffuse",  ColorRGBA.White); // needed for shininess
        safe_zone_mat.setFloat("Shininess", 20f); // shininess from 1-128
        Geometry safe_zone = new Geometry("SafeZone", new Cylinder(2, 30, 5, 0.6f, true));
        safe_zone.rotate(FastMath.HALF_PI, 0, 0);
        safe_zone.setMaterial(safe_zone_mat);
        rootNode.attachChild(safe_zone);
        
//        /** An unshaded textured cube. 
// *  Uses texture from jme3-test-data library! */ 
//Box boxshape1 = new Box(Vector3f.ZERO, 1f,1f,1f); 
//Geometry cube_tex = new Geometry("A Textured Box", boxshape1); 
//Material mat_tex = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
//Texture tex = assetManager.loadTexture("Interface/Logo/Monkey.jpg"); 
//mat_tex.setTexture("ColorMap", tex); 
//cube_tex.setMaterial(mat_tex); 
//rootNode.attachChild(cube_tex);
//
///** Illuminated bumpy rock with shiny effect. 
// *  Uses Texture from jme3-test-data library! Needs light source! */
//Sphere rock = new Sphere(32,32, 2f);
//Geometry rock_shiny = new Geometry("Shiny rock", rock);
//rock.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
//TangentBinormalGenerator.generate(rock);   // for lighting effect
//Material mat_shiny = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
//mat_shiny.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.png"));
//mat_shiny.setTexture("NormalMap",  assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
////mat_shiny.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires flow filter!
//mat_shiny.setBoolean("UseMaterialColors",true);  // needed for shininess
//mat_shiny.setColor("Specular", ColorRGBA.White); // needed for shininess
//mat_shiny.setColor("Diffuse",  ColorRGBA.White); // needed for shininess
//mat_shiny.setFloat("Shininess", 5f); // shininess from 1-128
//rock_shiny.setMaterial(mat_shiny);
//rootNode.attachChild(rock_shiny);
//        /** An unshaded textured cube. 
// *  Uses texture from jme3-test-data library! */ 
//Box boxshape1 = new Box(Vector3f.ZERO, 1f,1f,1f); 
//Geometry cube_tex = new Geometry("A Textured Box", boxshape1); 
//Material mat_tex = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
//Texture tex = assetManager.loadTexture("mindstorm2.j3o"); 
//mat_tex.setTexture("ColorMap", tex); 
//cube_tex.setMaterial(mat_tex); 
//rootNode.attachChild(cube_tex); 

// Material mat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
       
        // 120 degrees boundries (+/- 60 degrees)
        float dist = 12;
        Debug.attachArrow(rootNode, new Vector3f(FastMath.sin(FastMath.DEG_TO_RAD * 60) * dist, 1, FastMath.cos(FastMath.DEG_TO_RAD * 60) * dist), ColorRGBA.Yellow, "1");
        Debug.attachArrow(rootNode, new Vector3f(-FastMath.sin(FastMath.DEG_TO_RAD * 60) * dist, 1, FastMath.cos(FastMath.DEG_TO_RAD * 60) * dist), ColorRGBA.Yellow, "2");

        /** Initilize Cameras **/
        cam1 = new Cam("cam1");
        cam1.setLocalTranslation(new Vector3f(0, 6, 0));
        cam1.lookAt(cam_1_DEFAULT_LOC);
        rootNode.attachChild(cam1.getMainNode());
        
        cam2 = new Cam("cam2");
        cam2.setLocalTranslation(new Vector3f(0, 6, 0));		
        cam2.lookAt(cam_2_DEFAULT_LOC);
        rootNode.attachChild(cam2.getMainNode());

        /** Camera Viewports **/
        cam1.getCamera().setViewPort(0f, 0.625f, 0f, 0.625f);
        ViewPort cam_vp2 = renderManager.createMainView("cam1", cam1.getCamera());
        cam_vp2.setClearFlags(true, true, true);
        cam_vp2.attachScene(rootNode);
        cam_vp2.setBackgroundColor(ColorRGBA.Pink);

        cam2.getCamera().setViewPort(1.0f, 1.625f, 0f, 0.625f);
        ViewPort cam_vp1 = renderManager.createMainView("cam2", cam2.getCamera());
        cam_vp1.setClearFlags(true, true, true);
        cam_vp1.attachScene(rootNode);
        cam_vp1.setBackgroundColor(ColorRGBA.Pink);

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


        inputManager.addListener(this, new String[]{
            "CREATE_SLOW", "CREATE_FAST", "CREATE_ARC", 
            "SPEED_NORMAL", "SPEED_UP", "SPEED_DOWN",
            "SPACE", "RESET", "LEFT_CLICK", "RIGHT_CLICK"});

        /** Debug **/
        inputManager.addMapping("DEBUG_PHYSICS", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, new String[]{"DEBUG_PHYSICS"});
    }

    private Spatial createTestChild(TestChild.Type test_child_type) {
        // Load and scale model
        Spatial child = assetManager.loadModel("Models/mindstorm2.j3o");
        child.scale(0.05f);
        child.setLocalTranslation(new Vector3f(0, 0.3f, 0));
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
                cam1.lookAt(cam_1_DEFAULT_LOC);
                cam2.lookAt(cam_2_DEFAULT_LOC);
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
