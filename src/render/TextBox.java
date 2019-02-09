package render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lwjglEngine.fontMeshCreator.FontType;
import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.render.DisplayManager;

public class TextBox extends GuiTexture {

	public String name;
	private List<String> tooltip;
	private List<String> display;
	public Menu menu;
	// public boolean enabled;

	// public float origX, origY, origSizeX, origSizeY; //Public or private?
	public boolean expanded = false;
	public int[] noOrdersIfMenu = null;
	public boolean lock = false, shortcut = true;
	public boolean autoClear = true;
	public boolean monospace = false, noOverlap = false;

	// 0-255
	public Vector3f textColor = new Vector3f(255, 255, 255);
	public float borderR = -1, borderG = -1, borderB = -1; // default nostroke
	public int shape = 0; // 0 -> rectangle (default), 1 -> ellipse

	// public String textString;
	public float fontSize;
	public int textMeshVao;
	public int vertexCount;
	public float lineMaxSize;
	public FontType font;

	public boolean centerText = false;

	/**
	 * Creates a new text, loads the text's quads into a VAO, and adds the text to
	 * the screen.
	 * 
	 * @param text          - the text.
	 * @param fontSize      - the font size of the text, where a font size of 1 is
	 *                      the default size.
	 * @param font          - the font that this text should use.
	 * @param position      - the position on the screen where the top left corner
	 *                      of the text should be rendered. The top left corner of
	 *                      the screen is (0, 0) and the bottom right is (1, 1).
	 * @param maxLineLength - basically the width of the virtual page in terms of
	 *                      screen width (1 is full screen width, 0.5 is half the
	 *                      width of the screen, etc.) Text cannot go off the edge
	 *                      of the page, so if the text is longer than this length
	 *                      it will go onto the next line. When text is centered it
	 *                      is centered into the middle of the line, based on this
	 *                      line length value.
	 * @param centered      - whether the text should be centered or not.
	 */

	public void remove() {
		TextMaster.removeText(this);
	}

	public TextBox(int fontSize, FontType font, int maxLineLength, boolean centered, int texture, String text,
			String tip, float a, float b, float c, float d) {
		super(texture, new Vector2f(a, b), new Vector2f(c, d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add(tip);
		// tooltip.add(text);
		// enabled = false;

		this.fontSize = fontSize;
		// this.font = font;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		// this.lineMaxSize = c/DisplayManager.width;
		this.centerText = true;
		updateTextTextureVao();
	}

	public TextBox(int texture, String text, float a, float b, float c, float d) {
		super(texture, new Vector2f(a, b), new Vector2f(c, d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add("");
		// enabled = false;

		this.centerText = true;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		// this.lineMaxSize = c/DisplayManager.width;
		updateTextTextureVao();
	}

	public TextBox(int texture, String text, String tip, float a, float b, float c, float d) {
		super(texture, new Vector2f(a, b), new Vector2f(c, d));
		display = new ArrayList<String>();
		display.add(text);
		tooltip = new ArrayList<String>();
		tooltip.add(tip);
		// enabled = false;
		this.centerText = true;
		this.fontSize = 0.5f;
		this.lineMaxSize = 1f;
		// this.lineMaxSize = c/DisplayManager.width;
		updateTextTextureVao();
	}

	private void updateTextTextureVao() {
		if (TextMaster.init) {
			if (display == null || display.size() == 0) {
				TextMaster.removeText(this);
			} else {
				TextMaster.loadText(this);
			}
		}
	}

	public boolean equals(TextBox o) {
		return pos.equals(o.pos) && size.equals(o.size);
	}

	public int[] dimTooltip() {
		if (tooltip.size() == 0)
			return new int[] { 0, 0 };
		if (tooltip.size() == 1)
			return new int[] { 7 * tooltip.get(0).length(), 20 };
		int index = 0;
		for (int i = 0; i < tooltip.size(); i++) {
			// System.out.println(tooltip.get(i));
			if (tooltip.get(i) != null)
				if (tooltip.get(i).length() > tooltip.get(index).length())
					index = i;
		}
		return new int[] { 7 * tooltip.get(index).length(), 14 * tooltip.size() };
	}

	public void move(float x, float y) {
		pos.x = x / DisplayManager.width;
		pos.y = y / DisplayManager.height;
		origPos = new Vector2f(x, y);
		pixelPos.x = x;
		pixelPos.y = y; // pixelSize.y = y haha
		// String text = display.size() > 0 ? display.get(0) : null;
		// System.out.println("Moving: " + text + " " + pos + " " + size);
	}

	public void resize(float x, float y) {
		size.x = x / DisplayManager.width;
		size.y = y / DisplayManager.height;
		pixelSize = new Vector2f(x, y);
	}

	// Return itself for convenience
	public TextBox color(float x, float y, float z) {
		color.x = x;
		color.y = y;
		color.z = z;
		return this;
	}

	public TextBox color(float w) {
		return color(w, w, w);
	}

	public TextBox borderColor(float x, float y, float z) {
		borderR = x;
		borderG = y;
		borderB = z;
		return this;
	}

	public TextBox borderColor(float w) {
		return borderColor(w, w, w);
	}

	public void setDisplayText(List<String> text) {
		display = text;
		updateTextTextureVao();
	}

	public void setTooltipText(List<String> text) {
		tooltip = text;
		updateTextTextureVao();
	}

	public List<String> getDisplay() {
		return display;
	}

	public List<String> getTooltip() {
		return tooltip;
	}

	public void clearDisplayText() {
		display.clear();
		updateTextTextureVao();
	}

	public void clearTooltipText() {
		tooltip.clear();
		updateTextTextureVao();
	}

	public void addDisplayText(String line) {
		display.add(line);
		updateTextTextureVao();
	}

	public void addTooltipText(String line) {
		tooltip.add(line);
		updateTextTextureVao();
	}

	public void setDisplayText(int index, String line) {
		if (index >= display.size()) {
			addDisplayText(line);
		}
		else {
			display.set(index, line);
			updateTextTextureVao();
		}
	}

	public void setTooltipText(int index, String line) {
		if (index >= tooltip.size()) {
			addTooltipText(line);
		}
		else {
			tooltip.set(index, line);
			updateTextTextureVao();
		}
	}

	// Legacy methods
	public void activate(boolean a) {
		active = a;
	}

	public void orderOriginal() {
		pos = origPos;
	}

}
