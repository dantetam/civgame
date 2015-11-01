package lwjglEngine.fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lwjglEngine.fontMeshCreator.FontType;
import lwjglEngine.fontMeshCreator.GUIText;
import lwjglEngine.fontMeshCreator.TextMeshData;
import lwjglEngine.render.Loader;
import render.TextBox;

public class TextMaster {
	
	private static Loader loader;
	private static Map<FontType, List<TextBox>> texts = new HashMap<FontType, List<TextBox>>();
	private static FontRenderer renderer;
	
	public static void init(Loader theLoader){
		renderer = new FontRenderer();
		loader = theLoader;
	}
	
	public static void render(){
		renderer.render(texts);
	}
	
	public static void loadText(TextBox text){
		TextMeshData data = text.font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.textMeshVao = vao;
		text.vertexCount = data.getVertexCount();
		List<TextBox> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<TextBox>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	public static void removeText(TextBox text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(texts.get(text.getFont()));
		}
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}

}
