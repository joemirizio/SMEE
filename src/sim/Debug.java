/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;

/**
 *
 * @author joemirizio
 */
public class Debug {

	private static final Node debug_node = new Node();
	private static AssetManager assetManager = Main.ASSET_MANAGER;
	
	static {
		Main.ROOT_NODE.attachChild(debug_node);
	}
	
	public static void attachArrow(Node n, Vector3f dir, ColorRGBA color, String name) {
		n.detachChildNamed(name);
		Arrow arrow = new Arrow(dir);
		arrow.setLineWidth(4);
		putShape(n, arrow, color, name).setLocalRotation(n.getLocalRotation().inverse());
	}
	
	public static void attachCube(Vector3f pos, ColorRGBA color, String name) {
		debug_node.detachChildNamed(name);
		Box b = new Box(1f, 1f, 1f);
		putShape(debug_node, b, color, name).setLocalTranslation(pos);
	}
	
	public static void attachCoordinateAxes(Vector3f pos) {
		attachCoordinateAxes(debug_node, pos);
	}

	public static void attachCoordinateAxes(Node n, Vector3f pos) {
		Arrow arrow = new Arrow(Vector3f.UNIT_X);
		arrow.setLineWidth(4); // make arrow thicker
		putShape(n, arrow, ColorRGBA.Red).setLocalTranslation(pos);

		arrow = new Arrow(Vector3f.UNIT_Y);
		arrow.setLineWidth(4); // make arrow thicker
		putShape(n, arrow, ColorRGBA.Green).setLocalTranslation(pos);

		arrow = new Arrow(Vector3f.UNIT_Z);
		arrow.setLineWidth(4); // make arrow thicker
		putShape(n, arrow, ColorRGBA.Blue).setLocalTranslation(pos);
	}

	public static Geometry putShape(Mesh shape, ColorRGBA color) {
		return putShape(debug_node, shape, color);
	}
	
	public static Geometry putShape(Node n, Mesh shape, ColorRGBA color) {
		return putShape(n, shape, color, "DEBUG_SHAPE");
	}

	public static Geometry putShape(Node n, Mesh shape, ColorRGBA color, String name) {
		Geometry g = new Geometry(name, shape);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		//mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		n.attachChild(g);
		return g;
	}
	
	public static void clearNode() {
		debug_node.detachAllChildren();
	}
	public static void clearNode(String starts_with) {
		for (Spatial s : debug_node.getChildren()) {
			if (s.getName().startsWith(starts_with)) {
				s.removeFromParent();
			}
		}
	}
}
