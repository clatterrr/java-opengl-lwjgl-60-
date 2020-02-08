package renderEngine;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * 处理将几何数据加载进VAO的类，同时跟踪所有创建的VAO和VBO，以便在游戏关闭时将它们删除
 * @author Karl
 *
 */
public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();

	/**
	 * Creates a VAO and stores the position data of the vertices into attribute
	 * 0 of the VAO.
	 * 创建一个VAO并且将顶点的位置信息存储进VAO的attribute0
	 * @参数 位置
	 *            - 每个顶点的位置
	 * @返回 加载的模型
	 */
	public RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / 3);
	}

	/**
	 * 在游戏关闭时删除所有的VAO和VBO，这些VAO和VBO是在显存中
	 */
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
	}

	/**
	 * 创建一个VAO并返回它的ID。VAO拥有几何体的数据，被保存在GPU的显存中，
	 * 所以我们能快速的渲染
	 *
	 * 创建新的VAO使用"gen"方法，这个方法会返回一个ID。要使用一个VAO就必须先激活
	 * 这个VAO，但同一时间只能有一个被激活的VAO。为了让某个特定的VAO在激活状态
	 * 我们必须绑定它
	 *
	 * @返回 最新创建的VAO的ID
	 */
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	/**
	 * 这个方法用来将顶点的位置数据储存在VAO的attribute0中。
     * 
     * 但首先我们要将位置数据储存在VBO中。你可以认为VBO就算储存显存中的一组数据。
	 * 我们创建VBO也使用"gen"方法，并绑定以激活它。
	 * 
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
	private void storeDataInAttributeList(int attributeNumber, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);
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
