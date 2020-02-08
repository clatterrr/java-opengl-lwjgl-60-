package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();
		
		float[] vertices = {			
				-0.5f,0.5f,0,	//V0
				-0.5f,-0.5f,0,	//V1
				0.5f,-0.5f,0,	//V2
				0.5f,0.5f,0		//V3
		};
		
		int[] indices = {
				0,1,3,	//Top left triangle (V0,V1,V3)
				3,1,2	//Bottom right triangle (V3,V1,V2)
		};
		
		//纹理UV坐标，加载全部的纹理
		
		float[] textureCoords = 
			{
					0,0, //v0
					0,1, //V1
					1,1, //V2
					1,0  //V3
			};
		
		RawModel model = loader.loadToVAO(vertices,textureCoords,indices);
		//定义有纹理的模型
		ModelTexture texture = new ModelTexture(loader.loadTexture("image"));
		TexturedModel texturedModel = new TexturedModel(model,texture);
		
		while(!Display.isCloseRequested()){
			renderer.prepare();
			shader.start();
			//加载有纹理的模型
			renderer.render(texturedModel);
			shader.stop();
			DisplayManager.updateDisplay();			
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
