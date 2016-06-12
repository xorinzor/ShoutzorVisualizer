package com.jorinvermeulen.shoutzor.main;

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
	
}
