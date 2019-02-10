package lwjglEngine.fontMeshCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import render.TextBox;

public class TextMeshCreator {

	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private MetaFile metaData;

	protected TextMeshCreator(File metaFile) {
		metaData = new MetaFile(metaFile);
	}

	protected TextMeshData createTextMesh(TextBox text) {
		List<Line> lines = createStructure(text);
		TextMeshData data = createQuadVertices(text, lines);
		return data;
	}

	private List<Line> createStructure(TextBox text) {
		List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < text.getDisplay().size(); i++) {
			char[] chars = text.getDisplay().get(i).toCharArray();
			Line currentLine = new Line(metaData.getSpaceWidth(), text.fontSize, text.lineMaxSize);
			Word currentWord = new Word(text.fontSize);
			for (char c : chars) {
				int ascii = (int) c;
				if (ascii == SPACE_ASCII) {
					boolean added = currentLine.attemptToAddWord(currentWord);
					if (!added) {
						lines.add(currentLine);
						currentLine = new Line(metaData.getSpaceWidth(), text.fontSize, text.lineMaxSize);
						currentLine.attemptToAddWord(currentWord);
					}
					currentWord = new Word(text.fontSize);
					continue;
				}
				Character character = metaData.getCharacter(ascii);
				currentWord.addCharacter(character);
			}
			completeStructure(lines, currentLine, currentWord, text);
		}
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, TextBox text) {
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.fontSize, text.lineMaxSize);
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(TextBox text, List<Line> lines) {
		double cursorX = 0f;
		double cursorY = 0f;
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		for (Line line : lines) {
			if (text.centerText) {
				cursorX = (line.getMaxLength() - line.getLineLength()) / 2;
			}
			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(cursorX, cursorY, letter, text.fontSize, vertices);
					addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					cursorX += letter.getxAdvance() * text.fontSize;
				}
				cursorX += metaData.getSpaceWidth() * text.fontSize;
			}
			cursorX = 0;
			cursorY += LINE_HEIGHT * text.fontSize;
		}
		return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
	}

	private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize,
			List<Float> vertices) {
		double x = cursorX + (character.getxOffset() * fontSize);
		double y = cursorY + (character.getyOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y + (character.getSizeY() * fontSize);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}

	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}

	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

}
