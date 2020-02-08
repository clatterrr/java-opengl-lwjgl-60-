package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;

/**
 * This class contains the main method and is used to test the engine.
 * 这个类包含了用于启动游戏的方法
 * @author Karl
 *
 */
public class MainGameLoop {


	/**
	 * Loads up the position data for two triangles (which together make a quad)
	 * into a VAO. This VAO is then rendered to the screen every frame.
	 * 从顶点位置信息加载两个三角形到VAO，这加起来成一个正方形。VAO在每帧都还被渲染到屏幕上
	 * @param args
	 */
	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		float[] vertices = {
				// Left bottom triangle左下角的三角形
				-0.5f, 0.5f, 0f,
				-0.5f, -0.5f, 0f,
				0.5f, -0.5f, 0f,
				// Right top triangle右上角的三角形
				0.5f, -0.5f, 0f,
				0.5f, 0.5f, 0f,
				-0.5f, 0.5f, 0f
		};
		
		RawModel model = loader.loadToVAO(vertices);

		while (!Display.isCloseRequested()) {
			// game logic
			renderer.prepare();
			renderer.render(model);
			DisplayManager.updateDisplay();
		}

		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
