package com.EvilNotch.Core.Util.Java;

import java.awt.Color;

public class ColorUtil {
	
	public static Color getColorFromMsAcess(int p_78258_4_)
	{
		
		int red = (int)(p_78258_4_ >> 16 & 255);
        int green = (int)(p_78258_4_ >> 8 & 255);
        int blue = (int)(p_78258_4_ & 255);
        int alpha = (int)(p_78258_4_ >> 24 & 255);
         return new Color(red,green,blue,alpha);
	}
	
	public static int gethex(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
	
	public static int getMs(int ms, double[] mul) 
	{
		Color c = getColorFromMsAcess(ms);
		double rmul = mul[0];
		double gmul = mul[1];
		double bmul = mul[2];
		int r = (int)(c.getRed() * rmul);
		int g = (int)(c.getGreen() * gmul);
		int b = (int)(c.getBlue() * bmul);
		if(r >= 255)
			r = 255;
		if(g >= 255)
			g = 255;
		if(b >= 255)
			b = 255;//If it's greater then white return white

		return gethex(r, g, b, 0);
	}

}
