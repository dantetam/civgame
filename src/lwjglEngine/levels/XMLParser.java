package lwjglEngine.levels;

import processing.core.PApplet;
import processing.data.XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XMLParser {
	
	private double minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
	
	public static void main(String[] args)
	{
		File test = new XMLParser().loadXMLModel("/data/moredata/ant4.rbxm");
	}
	
	public XMLParser() {
		
	}
	
	public File loadXMLModel(String fileName)
	{	
		XML xml = null;
		ArrayList<String> data;
		
		//xml = XML.parse("/moredata/ant4.rbxm");
		try {xml = XML.parse(fileName);} catch (Exception e) {e.printStackTrace();}
		
		XML child = xml.getChild("Item"); //workspace?
		XML[] children = child.getChildren("Item");
		data = new ArrayList<String>();

		for (int i = 0; i < children.length; i++) 
		{
			if (children[i].getString("class").equals("Part"))
				XMLToText(children[i], data);
			else if (children[i].getString("class").equals("Model"))
			{
				for (int j = 0; j < children[i].getChildCount(); j++)
					if (children[i].getString("class").equals("Part"))
						XMLToText(children[i].getChild(j), data);
			}
		}
		data.add(0,Math.abs(maxX-minX)/2 + "," + maxX + "," + Math.abs(maxY-minY)/2 + "," + maxY + "," + Math.abs(maxZ-minZ)/2 + "," + maxZ);
		
		String[] temp = new String[data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			temp[i] = data.get(i);
		}
		
		File file = new File(fileName);
		try
		{
			FileWriter fw = new FileWriter(file);
			for (String stringy: temp)
				fw.write(stringy);
			//fw.flush();
			fw.close();
		} catch (IOException e) {e.getStackTrace();}
		
		System.out.println("Done, refresh for files.");
		
		return file;
	}

	private XML findChild(XML xml, String name)
	{
		for (int i = 0; i < xml.getChildren().length; i++)
		{
			if (xml.getChildren()[i].hasChildren())
			{
				//println(xml.getChildren()[i]);
				if (xml.getChildren()[i].hasAttribute("name"))
					if (xml.getChildren()[i].getString("name").equals(name))
						return xml.getChildren()[i];
			}
		}
		return null;
	}

	private void XMLToText(XML xml, ArrayList<String> data)
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
		
		data.add(posX + "," + posY + "," + posZ + "," + rotX + "," + rotY + "," + rotZ + "," + sizeX + "," + sizeY + "," + sizeZ + "," + brickColor + "," + name);
	}

	private double round(double num)
	{
		/*if (Math.abs(num - Math.floor(num)) < 0.001)
			return Math.floor(num);
		else if (Math.abs(num - Math.ceil(num)) < 0.001)
			return Math.ceil(num);
		else if (Math.abs(Math.floor(num) - num) < 0.01)
			return Math.floor(num);
		else if (Math.abs(Math.ceil(num) - num) < 0.01)
			return Math.ceil(num);
		else
			return num;*/
		return (double)Math.round(num * 10000) / 10000;
	}

}
