/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue;

import java.awt.*;

/**
 * A set of utilities to convert between CIE XYX and RGB
 */
public class ColourConverter
{

    final static double[] scaleValues(double min, double max, double... vals) {
        double[] result = new double[vals.length];
        double scaleFactor = max - min;
        for (int x = 0; x < vals.length; x++) {
            result[x] = ((vals[x] - min) / scaleFactor);
        }
        return result;
    }

    public static double[] colorToXyz(Color color) {
        return rgbToXyz(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static double[] rgbToXyz(int r, int g, int b) {

        double[] rgb = scaleValues(0, 255, r, g, b);
        double red = rgb[0];
        double green = rgb[1];
        double blue = rgb[2];

        //Apply a gamma correction to the RGB values, which makes the color more vivid
        double _red = (red > 0.04045f) ? Math.pow((red + 0.055f) / (1.0f + 0.055f), 2.4f) : (red / 12.92f);
        double _green = (green > 0.04045f) ? Math.pow((green + 0.055f) / (1.0f + 0.055f), 2.4f) : (green / 12.92f);
        double _blue = (blue > 0.04045f) ? Math.pow((blue + 0.055f) / (1.0f + 0.055f), 2.4f) : (blue / 12.92f);

        //Convert the RGB values to XYZ using the Wide RGB D65 conversion formula
        double X = _red * 0.649926f + _green * 0.103455f + _blue * 0.197109f;
        double Y = _red * 0.234327f + _green * 0.743075f + _blue * 0.022598f;
        double Z = _red * 0.0000000f + _green * 0.053077f + _blue * 1.035763f;
        
        return new double[]{X, Y, Z};
    }
    
    public static double[] XyzToXy(double... vals)
    {
        double[] result = new double[2];

        result[0] = vals[0] / (vals[0] + vals[1] + vals[2]);
        result[1] = vals[1] / (vals[0] + vals[1] + vals[2]);
        return result;
    }

    /**
     * Decode an HTML color string like '#F567BA;' into a {@link Color}
     *
     * @param colorString The string to decode
     * @return The decoded color
     * @throws IllegalArgumentException if the color sequence is not valid
     */
    public static Color decodeHtmlColorString(String colorString)
    {
        Color color;

        if (colorString.startsWith("#"))
        {
            colorString = colorString.substring(1);
        }
        if (colorString.endsWith(";"))
        {
            colorString = colorString.substring(0, colorString.length() - 1);
        }

        int red, green, blue;
        switch (colorString.length())
        {
            case 6 :
                red = Integer.parseInt(colorString.substring(0, 2), 16);
                green = Integer.parseInt(colorString.substring(2, 4), 16);
                blue = Integer.parseInt(colorString.substring(4, 6), 16);
                color = new Color(red, green, blue);
                break;
            case 3 :
                red = 17 * Integer.parseInt(colorString.substring(0, 1), 16);
                green = 17 * Integer.parseInt(colorString.substring(1, 2), 16);
                blue = 17 * Integer.parseInt(colorString.substring(2, 3), 16);
                color = new Color(red, green, blue);
                break;
            case 1 :
                red = green = blue = 17 * Integer.parseInt(colorString.substring(0, 1), 16);
                color = new Color(red, green, blue);
                break;
            default :
                throw new IllegalArgumentException("Invalid color: " + colorString);
        }
        return color;
    }
}
