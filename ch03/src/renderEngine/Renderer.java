package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Handles the rendering of a model to the screen.
 * 处理将模型渲染到屏幕上
 * @author Karl
 *
 */
public class Renderer {

	/**
	 * This method must be called each frame, before any rendering is carried
	 * out. It basically clears the screen of everything that was rendered last
	 * frame (using the glClear() method). The glClearColor() method determines
	 * the colour that it uses to clear the screen. In this example it makes the
	 * entire screen red at the start of each frame.
	 * 这个方法必须被每一帧调用。首先用glClear清除了屏幕。glClearColor决定了
	 * 用于清除屏幕的颜色。在这里是红色。
	 */
	public void prepare() {
		GL11.glClearColor(1, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Renders a model to the screen.
	 * 将模型渲染到屏幕
	 * 
	 * Before we can render a VAO it needs to be made active, and we can do this
	 * by binding it. We also need to enable the relevant attributes of the VAO,
	 * which in this case is just attribute 0 where we stored the position data.
	 * 在我们渲染VAO之前需要绑定它来让它激活，我们同样需要激活VAO的attribute，
	 * 在这里只有attribute0，也就是顶点的位置信息
	 * 
	 * The VAO can then be rendered to the screen using glDrawArrays(). We tell
	 * it what type of shapes to render and the number of vertices that it needs
	 * to render.
	 * 然后可以用glDrawArrays()来渲染到屏幕。我们需要指定渲染的形状以及顶点数。
	 * GL_TRIANGLES说明需要渲染的是三角形
	 * 
	 * After rendering we unbind the VAO and disable the attribute.
	 * 渲染完成后解绑VAO并禁用attribute
	 *
	 * @param model
	 *            - The model to be rendered.
	 *            - 需要渲染的模型
	 */
	public void render(RawModel model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
