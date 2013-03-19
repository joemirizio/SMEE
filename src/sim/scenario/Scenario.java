/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.scenario;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import sim.Main;

/**
 *
 * @author joemirizio
 */
public class Scenario implements ActionListener {
    
    public static final int DEFAULT_SCENARIO = 0;
    
    /**
     * Number of scenarios.
     * *** Change this when more are added. ***
     */
    public static final int SCENARIO_COUNT = 6;
    
    private Main main_app;
    public int scenario_num;
    
    public float light_intensity = 1;
    
    public Scenario(Main main_app) {
        this.main_app = main_app;
        scenario_num = DEFAULT_SCENARIO;
    }
    
    /**
     * The default scenario.
     * DO NOT CHANGE
     */
    public void default_scenario() {
        // Lighting
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        this.addLight(sun);
        
        // Flooring
        Material unshaded_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_mat.setColor("Color", ColorRGBA.Blue);
        this.setZoneMaterial(Zone.FLOOR, unshaded_mat);
        
        Material prediction_line_mat = unshaded_mat.clone();
        prediction_line_mat.setColor("Color", ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = unshaded_mat.clone();
        alert_zone_mat.setColor("Color", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = unshaded_mat.clone();
        safe_zone_mat.setColor("Color", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);
        
        // Camera
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(0, 6, 0));
        this.main_app.cam1.lookAt(Main.cam_1_DEFAULT_LOC);
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(0, 6, 0));		
        this.main_app.cam2.lookAt(Main.cam_2_DEFAULT_LOC);
        
        // Register controls
        this.initControls();

    }
    /* -----------------------------------------------------------------------*/
    /* EDIT SCENARIOS BELOW
     * (Remember to change SCENARIO_COUNT when more are added)
    /* -----------------------------------------------------------------------*/
    public void scenario1() {
        // Lighting - 2 Lights spaced out
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
                
        SpotLight spot = new SpotLight();
        spot.setSpotRange(20f);                           // distance
        spot.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot.setPosition(new Vector3f(-3f, 6f, 0f));               // shine from camera loc
        spot.setDirection(new Vector3f(.5f, -5f, 6f));    // shine forward from camera loc
        this.addLight(spot); 

        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(20f);                           // distance
        spot1.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot1.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot1.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot1.setPosition(new Vector3f(3f, 6f, 0f));               // shine from camera loc
        spot1.setDirection(new Vector3f(-.5f, -5f, 6f));      // shine forward from camera loc
        this.addLight(spot1);   
        
        // Flooring - Min shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",2f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);

        
        // Camera - 2 Cameras at center
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-.5f, 5.75f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(0.5f, 5.75f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    public void scenario2() {
        // Lighting - 2 Lights 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
                
        SpotLight spot = new SpotLight();
        spot.setSpotRange(20f);                           // distance
        spot.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot.setPosition(new Vector3f(-3f, 6f, 0f));               // shine from camera loc
        spot.setDirection(new Vector3f(.5f, -5f, 6f));    // shine forward from camera loc
        this.addLight(spot); 

        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(20f);                           // distance
        spot1.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot1.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot1.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot1.setPosition(new Vector3f(3f, 6f, 0f));               // shine from camera loc
        spot1.setDirection(new Vector3f(-.5f, -5f, 6f));      // shine forward from camera loc
        this.addLight(spot1);   

        // Flooring - Max shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",8f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat); 
        
        // Camera - 2 Cameras spaced out
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-3f, 6f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(3f, 6f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    public void scenario3() {
      
        // Lighting - 2 lights
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
                
        SpotLight spot = new SpotLight();
        spot.setSpotRange(20f);                           // distance
        spot.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot.setPosition(new Vector3f(-3f, 6f, 0f));               // shine from camera loc
        spot.setDirection(new Vector3f(.5f, -5f, 6f));    // shine forward from camera loc
        this.addLight(spot); 

        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(20f);                           // distance
        spot1.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot1.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot1.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot1.setPosition(new Vector3f(3f, 6f, 0f));               // shine from camera loc
        spot1.setDirection(new Vector3f(-.5f, -5f, 6f));      // shine forward from camera loc
        this.addLight(spot1); 
        
        // Flooring - min shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",2f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);

        // Camera - 2 cameras spaced out
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-3f, 6f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(3f, 6f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    public void scenario4() {
        // Lighting - ambient
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
        
        // Flooring - min shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",2f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);

        // Camera - 2 cameras spaced out
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-3f, 6f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(3f, 6f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    public void scenario5() {
        // Lighting - 2 lights
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
                
        SpotLight spot = new SpotLight();
        spot.setSpotRange(20f);                           // distance
        spot.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot.setPosition(new Vector3f(-3f, 6f, 0f));               // shine from camera loc
        spot.setDirection(new Vector3f(.5f, -5f, 6f));    // shine forward from camera loc
        this.addLight(spot); 

        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(20f);                           // distance
        spot1.setSpotInnerAngle(5f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot1.setSpotOuterAngle(500f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot1.setColor(ColorRGBA.White.mult(light_intensity));         // light color
        spot1.setPosition(new Vector3f(3f, 6f, 0f));               // shine from camera loc
        spot1.setDirection(new Vector3f(-.5f, -5f, 6f));      // shine forward from camera loc
        this.addLight(spot1); 
        
        // Flooring - max shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",8f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);

        // Camera - 2 cameras at center
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-.5f, 5.75f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(0.5f, 5.75f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    public void scenario6() {
        // Lighting - ambient
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        sun.setColor(ColorRGBA.White.mult(.7f));
        this.addLight(sun);
        
        // Flooring - min shininess
        Material flooring_mat = new Material(Main.ASSET_MANAGER, "Common/MatDefs/Light/Lighting.j3md");//unshaded_mat.clone();
        flooring_mat.setBoolean("UseMaterialColors",true);
        flooring_mat.setColor("Diffuse",  ColorRGBA.Blue); // <--- This is the color
        flooring_mat.setColor("Specular", ColorRGBA.White);
        flooring_mat.setFloat("Shininess",2f); // shininess from 1-128
        this.setZoneMaterial(Zone.FLOOR, flooring_mat);
        
        Material prediction_line_mat = flooring_mat.clone(); // Cloning copies all properties, so just change what you need
        prediction_line_mat.setColor("Diffuse",  ColorRGBA.Gray);
        this.setZoneMaterial(Zone.PREDICTION_LINE, prediction_line_mat);
        
        Material alert_zone_mat = flooring_mat.clone();
        alert_zone_mat.setColor("Diffuse", ColorRGBA.Red);
        this.setZoneMaterial(Zone.ALERT_ZONE, alert_zone_mat);

        Material safe_zone_mat = flooring_mat.clone();
        safe_zone_mat.setColor("Diffuse", ColorRGBA.Green);
        this.setZoneMaterial(Zone.SAFE_ZONE, safe_zone_mat);

        // Camera - 2 cameras at center
        this.main_app.cam1.setVerticalFOV(56);
        this.main_app.cam1.setLocalTranslation(new Vector3f(-.5f, 5.75f, 0f));
        this.main_app.cam1.lookAt(new Vector3f(0.3f,-0.7f,0.55f));
        
        this.main_app.cam2.setVerticalFOV(56);
        this.main_app.cam2.setLocalTranslation(new Vector3f(0.5f, 5.75f, 0f));	
        this.main_app.cam2.lookAt(new Vector3f(-0.3f, -0.7f, 0.55f));

    }
    
    /* -----------------------------------------------------------------------*/
    
    /**
     * Initiaizes the controls
     */
    public void initControls() {
        main_app.getInputManager().addMapping("LOWER_LIGHT", new KeyTrigger(KeyInput.KEY_K));
        main_app.getInputManager().addMapping("RAISE_LIGHT", new KeyTrigger(KeyInput.KEY_L));
        
        main_app.getInputManager().addListener(this, new String[]{
            "LOWER_LIGHT", "RAISE_LIGHT"
        });
    }
    /**
     * Performs actions
     * @param name The name of the action
     * @param isPressed True if the putton is pressed down
     * @param tpf The time per frame
     */
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("LOWER_LIGHT") && isPressed) {
            light_intensity -= 0.5f;
            System.out.println("Light value: " + light_intensity);
        } else if (name.equals("RAISE_LIGHT") && isPressed) {
            light_intensity += 0.5f;
            System.out.println("Light value: " + light_intensity);
        }
    }
    
    /**
     * Runs the current scenario
     * @return The scenario number
     */
    public int runScenario() {
        return this.runScenario(this.scenario_num);
    }
    /**
     * Runs a specific scenario
     * @param scenario_num The scenario number
     */
    public int runScenario(int scenario_num) {
        if (scenario_num < 0 && scenario_num > 2) {
            throw new UnsupportedOperationException("Scenario not supported");
        }
        this.resetScenario();
        System.out.println("Running Scenario #" + this.scenario_num);
        // *** Add new scenarios here ***
        switch (scenario_num) {
            case 0: default_scenario(); break;
            case 1: scenario1(); break;
            case 2: scenario2(); break;
            case 3: scenario3(); break;
            case 4: scenario4(); break;
            case 5: scenario5(); break;
            case 6: scenario6(); break;
        }
        this.scenario_num = scenario_num;
        return this.scenario_num;
    }
    /**
     * Runs the previous scenario
     * @return The scenario number
     */
    public int runPrevScenario() {
        if (--this.scenario_num < 0) {
            this.scenario_num = SCENARIO_COUNT;
        }
        return this.runScenario(this.scenario_num);
    }
    /**
     * Runs the next scenario
     * @return The scenario number
     */
    public int runNextScenario() {
        if (++this.scenario_num > SCENARIO_COUNT) {
            this.scenario_num = 0;
        }
        return this.runScenario(this.scenario_num);
    }
    
    /**
     * Resets the scenario so another can be set
     */
    public void resetScenario() {
        // Remove all lights
        LightList light_list = Main.ROOT_NODE.getWorldLightList();
        for (Light l : light_list) {
            Main.ROOT_NODE.removeLight(l);
        }
        
        // Clear all frustums
        /*List<Spatial> root_children = Main.ROOT_NODE.getChildren();
        for (Spatial s : root_children) {
            if (s.getName() != null && s.getName().equals("Frustum")) {
                Main.ROOT_NODE.detachChild(s);
            }
        }*/
    }
    
    /**
     * Adds a light to the Main light node
     * @param light The light to add
     */
    public void addLight(Light light) {
        Main.ROOT_NODE.addLight(light);
    }
    
    /**
     * Sets a Zone's material
     * @param zone The desired zone
     * @param mat The material to set
     */
    public void setZoneMaterial(Zone zone, Material mat) {
        Geometry zone_geo = null;
        switch (zone) {
            case FLOOR: zone_geo = Main.floor; break;
            case SAFE_ZONE: zone_geo = Main.safe_zone; break;
            case ALERT_ZONE: zone_geo = Main.alert_zone; break;
            case PREDICTION_LINE: zone_geo = Main.prediction_line; break;
        }
        zone_geo.setMaterial(mat);
    }
    
    /**
     * The different zones and floor
     */
    enum Zone {
        FLOOR, SAFE_ZONE, ALERT_ZONE, PREDICTION_LINE;
    }
}
