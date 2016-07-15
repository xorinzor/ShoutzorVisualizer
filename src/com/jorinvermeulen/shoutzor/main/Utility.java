package com.jorinvermeulen.shoutzor.main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jme3.math.ColorRGBA;

public class Utility {

	/**
	 * Returns a ColorRGBA object from an RGB value
	 * @param r
	 * @param g
	 * @param b
	 * @param opacity
	 * @return
	 */
	public static ColorRGBA rgbToRGBA(float r, float g, float b, float opacity) {
		return new ColorRGBA(r / 255f, g / 255f, b / 255f, opacity);
	}
	
	
	/**
	 * Returns an ColorRGBA object from a HEX value (ie: #212121)
	 * @param colorStr
	 * @return
	 */
	public static ColorRGBA hexToRGBA(String colorStr) {
	    return rgbToRGBA(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ), 
	            1f);
	}
	
	public static String readFile(String path, Charset encoding) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
}
