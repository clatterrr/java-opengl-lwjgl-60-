package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * This class contains all the methods needed to set-up, maintain, and close a LWJGL display.
 * 这个类包含了用来启动，持续以及关闭 LWJGL 展示的方法.
 * 
 * @author Karl
 *
 */
public class DisplayManager {

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 60;
	private static final String TITLE = "Our First Display";

	/**
	 * Creates a display window on which we can render our game. The dimensions
	 * of the window are determined by setting the display mode. By using
	 * "glViewport" we tell OpenGL which part of the window we want to render
	 * our game onto. We indicated that we want to use the entire window.
	 * 创建显示窗口来渲染我们的游戏。使用"glViewport"来告诉opengl我们想在窗口上
	 * 哪一部分来渲染我们的游戏。在这里我们渲染整个窗口
	 */
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}

	/**
	 * This method is used to update the display at the end of every frame. When
	 * we have set up a rendering process this method will display whatever
	 * we've been rendering onto the screen. The "sync" method is used here to
	 * cap the frame rate. Without this the computer would just try to run the
	 * game as fast as it possibly can, doing more work than it needs to.
	 * 这个方法用来在每一帧结束时重新渲染。sync方法用来同步时钟，让电脑在在
	 * 相同时间里渲染指定的次数，而不是要多快有多快
	 */
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
	}

	/**
	 * This closes the window when the game is closed.
	 * 当游戏结束，这个方法用来关闭窗口
	 */
	public static void closeDisplay() {
		Display.destroy();
	}

}
