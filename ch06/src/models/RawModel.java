package models;

/**
 * Represents a loaded model. It contains the ID of the VAO that contains the
 * model's data, and holds the number of vertices in the model.
 * 展示一个加载好的模型。包含了VAO的ID，VAO里的模型数据，以及模型的顶点数量
 * @author Karl
 *
 */
public class RawModel {

	private int vaoID;
	private int vertexCount;

	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 *         VAO的ID，VAO包含了模型的所有数据
	 */
	public int getVaoID() {
		return vaoID;
	}

	/**
	 * @return The number of vertices in the model.
	 *         模型的顶点数量
	 */
	public int getVertexCount() {
		return vertexCount;
	}

}
