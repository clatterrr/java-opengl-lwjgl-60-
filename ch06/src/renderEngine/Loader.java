package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

/**
 * 处理将几何数据加载进VAO的类，同时跟踪所有创建的VAO和VBO，以便在游戏关闭时将它们删除
 * @author Karl
 *
 */
public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	//定义纹理List
	private List<Integer> textures = new ArrayList<Integer>();

	/**
	 * Creates a VAO and stores the position data of the vertices into attribute
	 * 0 of the VAO. The indices are stored in an index buffer and bound to the
	 * VAO.
	 * 创建一个VAO并且将顶点的位置信息存储进VAO的attribute0。索引将会储存进索引
	 * 缓存并与VAO关联起来
	 * @param positions
	 *            - The 3D positions of each vertex in the geometry (in this
	 *            example a quad).
	 *            - 每个顶点的位置
	 * @param indices
	 *            - The indices of the model that we want to store in the VAO.
	 *            The indices indicate how the vertices should be connected
	 *            together to form triangles.
	 *            - 我们先想要存储进VAO的模型索引。索引会说明三角形将会被如何创建
	 * @return The loaded model.
	 */

	public RawModel loadToVAO(float[] positions,float[] textureCoords, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	//读取纹理
	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName +".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	/**
	 * Deletes all the VAOs and VBOs when the game is closed. VAOs and VBOs are
	 * located in video memory.
	 * 在游戏关闭时删除所有的VAO和VBO，这些VAO和VBO是在显存中
	 */
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		//删除纹理
		for (int texture:textures)
		{
			GL11.glDeleteTextures(texture);
		}
	}

	/**
	 * Creates a new VAO and returns its ID. A VAO holds geometry data that we
	 * can render and is physically stored in memory on the GPU, so that it can
	 * be accessed very quickly during rendering.
	 * 创建一个VAO并返回它的ID。VAO拥有几何体的数据，被保存在GPU的显存中，
	 * 所以我们能快速的渲染
	 * 
	 * Like most objects in OpenGL, the new VAO is created using a "gen" method
	 * which returns the ID of the new VAO. In order to use the VAO it needs to
	 * be made the active VAO. Only one VAO can be active at a time. To make
	 * this VAO the active VAO (so that we can store stuff in it) we have to
	 * bind it.
	 * 创建新的VAO使用"gen"方法，这个方法会返回一个ID。要使用一个VAO就必须先激活
	 * 这个VAO，但同一时间只能有一个被激活的VAO。为了让某个特定的VAO在激活状态
	 * 我们必须绑定它
	 * 
	 * @return The ID of the newly created VAO.
	  * @返回 最新创建的VAO的ID
	 */
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	/**
	 * Stores the position data of the vertices into attribute 0 of the VAO. To
	 * do this the positions must first be stored in a VBO. You can simply think
	 * of a VBO as an array of data that is stored in memory on the GPU for easy
	 * access during rendering.
	 * 这个方法用来将顶点的位置数据储存在VAO的attribute0中。
	 * 但首先我们要将位置数据储存在VBO中。你可以认为VBO就算储存显存中的一组数据。
	 * 
	 * Just like with the VAO, we create a new VBO using a "gen" method, and
	 * make it the active VBO (so that we do stuff to it) by binding it.
	 * 我们创建VBO也使用"gen"方法，并绑定以激活它。
	 * 
	 * We then store the positions data in the active VBO by using the
	 * glBufferData method. We also indicate using GL_STATIC_DRAW that this data
	 * won't need to be changed. If we wanted to edit the positions every frame
	 * (perhaps to animate the quad) then we would use GL_DYNAMIC_DRAW instead.
	 * 使用glBufferData将位置数据存储进激活的VBO中。使用GL_STATIC_DRAW说明这些
	 * 位置数据以后将不会被改变。如果需要改变则使用GL_DYNAMIC_DRAW。
	 	 * 
	 * We the connect the VBO to the VAO using the glVertexAttribPointer()
	 * method. This needs to know the attribute number of the VAO where we want
	 * to put the data, the number of floats used for each vertex (3 floats in
	 * this case, because each vertex has a 3D position, an x, y, and z value),
	 * the type of data (in this case we used floats) and then some other more
	 * complicated stuff for storing the data in more fancy ways. Don't worry
	 * about the last 3 parameters for now, we don't need them here.
	 * 使用glVertexAttribPointer方法将VAO与VBO关联起来。为此我们需要指出我们
	 * 要放进VAO的attribute的数量。在这里是3个，即x,y,z位置坐标。无需担心这个方法
	 * 最后三个参数，我们还用不到它们。
	 * 
	 * Now that we've finished using the VBO we can unbind it. This isn't
	 * totally necessary, but I think it's good practice to unbind the VBO when
	 * you're done using it.
	 * 使用完VBO我们就可以解除绑定。这不是必需的，但仍是一个好习惯。
	 * 
	 * @param attributeNumber
	 *            - VAO的attribute的数量，用以存放数据
	 * @param data
	 *            - 被存放进VAO的几何体数据。这里是顶点的位置坐标
	 */
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		//改动
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Unbinds the VAO after we're finished using it. If we want to edit or use
	 * the VAO we would have to bind it again first.
	 * 使用完VAO后接触绑定。如果以后需要使用则以后再绑定
	 */
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	/**
	 * Creates an index buffer, binds the index buffer to the currently active
	 * VAO, and then fills it with our indices.
	 * 创建索引缓存，将索引缓存绑定到当前激活的VAO中，然后填写索引
	 * 
	 * The index buffer is different from other data that we might store in the
	 * attributes of the VAO. When we stored the positions we were storing data
	 * about each vertex. The positions were "attributes" of each vertex. Data
	 * like that is stored in an attribute list of the VAO.
	 * 索引婚车与VAO中其他attributes不同。当我们存储位置信息时，这些位置信息包含
	 * 很多个attrubute。
	 * 
	 * The index buffer however does not contain data about each vertex. Instead
	 * it tells OpenGL how the vertices should be connected. Each VAO can only
	 * have one index buffer associated with it. This is why we don't store the
	 * index buffer in a certain attribute of the VAO; each VAO has one special
	 * "slot" for an index buffer and simply binding the index buffer binds it
	 * to the currently active VAO. When the VAO is rendered it will use the
	 * index buffer that is bound to it.
	 * 索引缓存与特定顶点没什么关系，只是告诉OpenGL这些顶点应该如何被连接。每个VAO
	 * 有个特殊的槽来管理这些索引缓存，只能关联一个索引缓存。当每个VAO被渲染时，会
	 * 使用与之关联的所有缓存。
	 * 
	 * This is also why we don't unbind the index buffer, as that would unbind
	 * it from the VAO.
	 * 这就是为什么我们不用解绑索引缓存
	 * 
	 * Note that we tell OpenGL that this is an index buffer by using
	 * "GL_ELEMENT_ARRAY_BUFFER" instead of "GL_ARRAY_BUFFER". This is how
	 * OpenGL knows to bind it as the index buffer for the current VAO.
	 * 我们使用"GL_ELEMENT_ARRAY_BUFFER" 而不是"GL_ARRAY_BUFFER"来告诉OpenGL
	 * 这是一个索引缓存，这样OpenGL就知道了这个VAO要绑定的是一个索引缓存
	 * 
	 * @param indices
	 */
	private void bindIndicesBuffer(int[] indices) {
		int vboId = GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	/**
	 * Converts the indices from an int array to an IntBuffer so that they can
	 * be stored in a VBO. Very similar to the storeDataInFloatBuffer() method
	 * below.
	 * 将索引从一个数组转变为一个IntBuffer来将其存储进VBO，就像下面的
	 * storeDataInFloatBuffer()方法所做的
	 * 
	 * @param data
	 *            - The indices in an int[].
	 *            - int数组里的索引
	 * @return The indices in a buffer.
	 *         缓存里的索引
	 */
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
/**
	 * Before we can store data in a VBO it needs to be in a certain format: in
	 * a buffer. In this case we will use a float buffer because the data we
	 * want to store is float data. If we were storing int data we would use an
	 * IntBuffer.
	 * 要将数据存放进VBO，这些数据必须在缓存中。于是我们使用浮点缓存，因为这些
	 * 是浮点数据。
	 * 
	 * First and empty buffer of the correct size is created. You can think of a
	 * buffer as basically an array with a pointer. After putting the necessary
	 * data into the buffer the pointer will have increased so that it points at
	 * the first empty element of the array. This is so that we could add more
	 * data to the buffer if we wanted and it wouldn't overwrite the data we've
	 * already put in. However, we're done with storing data and we want to make
	 * the buffer ready for reading. To do this we need to make the pointer
	 * point to the start of the data, so that OpenGL knows where in the buffer
	 * to start reading. The "flip()" method does just that, putting the pointer
	 * back to the start of the buffer.
	 * 第一个空的并且大小正确的缓存被创建好了。你可以认为缓存就是一个数组加指针。
	 * 放入数据后，这个指针将会增加，一直指向这些数据的第一个空元素，这样我们才
	 * 能把数据放入这个空元素。如果数据存放完毕，需要开始读取数据，我们将指针
	 * 指向这组数据的起点，这样OpenGL就知道从哪里开始读了，这就是filp方法所做的。
	 * 
	 * @param data
	 *            - The float data that is going to be stored in the buffer.
	 *            - 将会被存储在缓存中的浮点数据
	 * @return The FloatBuffer containing the data. This float buffer is ready
	 *         to be loaded into a VBO.
	 *         存储了数据的浮点缓存。这些浮点缓存可以被加载进VBO中。
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

}
