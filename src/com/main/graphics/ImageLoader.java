package com.main.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ImageLoader.java handles loading image resources from memory.
 * NOTE: This class required no instantiation. Call statically
 */

public class ImageLoader {

	//returns a graphic of a specified image in memory
	public static Graphic getImage(String path){
		
		Graphic graphic = null;
		
		try {
			
			//get the buffered image
			BufferedImage image = ImageIO.read(ImageLoader.class.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			int[] buffer = new int[w*h];
			
			//set the graphic to the pixel buffer
			image.getRGB(0, 0, w, h, buffer, 0, w);
			graphic = new Graphic(w, h, buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return the graphic
		return graphic;
	}
		
}
