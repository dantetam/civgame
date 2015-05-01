package lwjglEngine.levels;

import processing.data.XML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RobloxXMLTest {

	private static XML xml;
	private static double minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;

	public static void main(String[] args)
	{
		loadModel("res/obj/islands.rbxm");
		System.out.println("Done, refresh for files.");
	}
	
	private static XML findChild(XML xml, String name)
	{
		for (int i = 0; i < xml.getChildren().length; i++)
		{
			if (xml.getChildren()[i].hasChildren())
				if (xml.getChildren()[i].hasAttribute("name"))
					if (xml.getChildren()[i].getString("name").equals(name))
						return xml.getChildren()[i];
		}
		return null;
	}

	private static String XMLToText(XML xml)
	{
		XML properties = xml.getChild("Properties");
		XML size = findChild(properties,"size");
		double posX = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("X").getContent()));
		double posY = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("Y").getContent()));
		double posZ = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("Z").getContent()));

		minX = posX < minX ? posX : minX;
		minY = posY < minY ? posY : minY;
		minZ = posZ < minZ ? posZ : minZ;
		maxX = posX > maxX ? posX : maxX;
		maxY = posY > maxY ? posY : maxY;
		maxZ = posZ > maxZ ? posZ : maxZ;

		double r1 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R00").getContent()));
		double r2 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R01").getContent()));
		double r3 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R02").getContent()));
		double r4 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R10").getContent()));
		double r5 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R11").getContent()));
		double r6 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R12").getContent()));
		double r7 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R20").getContent()));
		double r8 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R21").getContent()));
		double r9 = round(Double.parseDouble(properties.getChild("CoordinateFrame").getChild("R22").getContent()));
		double rotX = round(Math.atan2(-r6, r9));
		double rotY = round(Math.asin(r3));
		double rotZ = round(Math.atan2(-r2, r1));
		double sizeX = round(Double.parseDouble(size.getChild("X").getContent()));
		double sizeY = round(Double.parseDouble(size.getChild("Y").getContent()));
		double sizeZ = round(Double.parseDouble(size.getChild("Z").getContent()));
		int brickColor = Integer.parseInt(properties.getChild("int").getContent());

		String name = properties.getChild("string").getContent();

		return new String(posX + "," + posY + "," + posZ + "," + rotX + "," + rotY + "," + rotZ + "," + sizeX + "," + sizeY + "," + sizeZ + "," + brickColor + "," + name);
	}

	private static double round(double num)
	{
		return (double)Math.round(num * 10000) / 10000; //10000s intentionally left as integers
	}

	public static void loadModel(String fileName)
	{
		try {
			xml = new XML(new File(fileName));
		} catch (Exception e) {e.printStackTrace();} 
		XML child = xml.getChild("Item"); //workspace?
		XML[] children = child.getChildren("Item");
		ArrayList<String> temp = new ArrayList<String>();

		for (int i = 0; i < children.length; i++) 
		{
			if (children[i].getString("class").equals("Part"))
				temp.add(XMLToText(children[i]));
			else if (children[i].getString("class").equals("Model"))
			{
				for (int j = 0; j < children[i].getChildCount(); j++)
					if (children[i].getString("class").equals("Part"))
						temp.add(XMLToText(children[i].getChild(j)));
			}
		}
		temp.add(0,Math.abs(maxX-minX)/2 + "," + maxX + "," + Math.abs(maxY-minY)/2 + "," + maxY + "," + Math.abs(maxZ-minZ)/2 + "," + maxZ);
		/*String[] toFile = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++)
			toFile[i] = temp.get(i);*/
		saveStrings("res/parsedObj/islands",temp);
	}

	private static void saveStrings(String fileName, ArrayList<String> files)
	{
		FileOutputStream fileOut = null;
		File file = null;
		try {
			file = new File(fileName);
			fileOut = new FileOutputStream(file);
			if (!file.exists()) file.createNewFile();

			for (int i = 0; i < files.size(); i++)
			{
				files.set(i, files.get(i) + "\n");
				byte[] contentInBytes = files.get(i).getBytes();
				fileOut.write(contentInBytes);
			}
			fileOut.flush();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {if (fileOut != null) fileOut.close();} catch (Exception e) {e.printStackTrace();}
		}
		System.out.println("Made file " + file.getName() + " at path: " + file.getAbsolutePath());
	}

}
