package shaders;

public class StaticShader extends ShaderProgram{
	
	//导入文件
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

    //将attribute0指定为位置信息
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	

}
