/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue.model;

import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.hue.ColourConverter;
import org.mule.util.StringUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The object used to describe and set the state of a light or a group of lights
 * it encompasses all the attributes of the the Hue including the different methods to
 * configure color, namely:
 * <ul>
 *     <li>CIE XYZ (xy property)</li>
 *     <li>Mired Color Temperature (ct property)</li>
 *     <li>HSB (Hue, Saturation, Brightness properties)</li>
 * </ul>
 *
 * Note the usage of the colormode parameter: There are 3 ways of setting the light color: xy, color temperature (ct)
 * or hue and saturation (hs). A light may contain different settings for xy, ct and hs, but only the mode indicated
 * by the colormode parameter will be certain to give the active light color.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class State implements CommandBody
{
    private static final Map<String, java.awt.Color> COLOR_MAP;

    static
    {
        COLOR_MAP = loadColorMap();
    }

    private static Map<String, java.awt.Color> loadColorMap()
    {
        try
        {
            final Map<String, java.awt.Color> colorMap = new HashMap<String, java.awt.Color>();
            for (final Field f : java.awt.Color.class.getFields())
            {
                if (f.getType() == java.awt.Color.class)
                {
                    final java.awt.Color c = (java.awt.Color) f.get(null);
                    colorMap.put(f.getName().toUpperCase(), c);
                }
            }
            return colorMap;
        }
        catch (final IllegalAccessException iae)
        {
            throw new RuntimeException(iae);
        }
    }

    public static enum Alert {
        /**
         * The light is not performing an alert effect.
         */
        none,
        /**
         * The light is performing one breathe cycle.
         */
        select,
        /**
         * The light is performing breathe cycles for 30 seconds or until an <code>"alert": "none"</code> command is received.
         */
        lselect
    }

    public static enum Effect {
        /**
         * no effect
         */
        none,
        /**
         * cycles through all supported colors
         */
        colorloop
    }

    /**
     * The brightness value to set the light to.
     * Brightness is a scale from 0 (the minimum the light is capable of) to 255 (the maximum). Note: a brightness of 0 is not off.
     * e.g. “brightness”: 60 will set the light to a specific brightness	Optional
     */
    @JsonProperty("bri")
    @Optional
    private Integer brightness;

    /**
     * The dynamic effect of the light. Supported options are:
     *               <ul>
     *              <li>none – no effect.</li>
     *              <li>colorloop – cycles through all supported colors</li>
     *              </ul>
     *              Other values will generate an error of type 7. Setting the effect to colorloop will cycle through all hues using the current brightness and saturation settings
     */
    @JsonProperty("effect")
    @Optional
    private Effect effect;

    /**
     * Saturation of the light. 255 is the most saturated (colored) and 0 is the least saturated (white)
     */
    @JsonProperty("sat")
    @Optional
    private Integer saturation;

    /**
     * The alert effect, is a temporary change to the bulb’s state, and has one of the following values:
     *  <ul>
     *    <li>none – The light is not performing an alert effect.</li>
     *    <li>select – The light is performing one breathe cycle.</li>
     *    <li>lselect – The light is performing breathe cycles for 30 seconds or until an <code>"alert": "none"</code> command is received.</li>
     *  </ul>
     */
    @JsonProperty("alert")
    @Optional
    private Alert alert;

    /**
     * The hue value to set light to. The hue value is a wrapping value between 0 and 65535.
     * Both 0 and 65535 are red, 25500 is green and 46920 is blue.
     * e.g. “hue”: 50000 will set the light to a specific hue.	Optional
     */
    @JsonProperty("hue")
    @Optional
    private Integer hue;


    /**
     * Whether the light is on or not
     */
    @JsonProperty("on")
    @Optional @Default("true")
    private Boolean on;

    /**
     * The Mired Color temperature of the light. 2012 connected lights are capable of 153 (6500K) to 500 (2000K)
     */
    @JsonProperty("ct")
    @Optional
    private Integer ct;

    /**
     * The x and y coordinates of a color in CIE color space.
     * The first entry is the x coordinate and the second entry is the y coordinate. Both x and y must be between 0 and 1.
     * If the specified coordinates are not in the CIE color space, the closest color to the coordinates will be chosen.	Optional
     */
    @JsonProperty("xy")
    private List<Double> xyColor;

    /**
     * The duration of the transition from the light’s current state to the new state. This is given as a multiple of 100ms
     * and defaults to 4 (400ms). For example, setting transistiontime:10 will make the transition last 1 second.	Optional
     */
//    @JsonProperty("transitiontime")
//    private int transitiontime;

    /**
     * The x and y coordinates of a color in CIE color space.
     * The first entry is the x coordinate and the second entry is the y coordinate. Both x and y must be between 0 and 1.
     * If the specified coordinates are not in the CIE color space, the closest color to the coordinates will be chosen.
     */
    @Optional @Default("0.0,0.0")
    @JsonIgnore
    private String xy;

    /**
     * Indicates if a light can be reached by the bridge. Currently always returns true, functionality will be added in a future patch.
     */
    @JsonProperty
    private Boolean reachable;

    /**
     * Indicates the color mode in which the light is working, this is the last command type it received.
     * Values are “hs” for Hue and Saturation, “xy” for XY and “ct” for Color Temperature. This parameter
     * is only present when the light supports at least one of the values.
     */
    @JsonProperty
    private String colormode;

    /**
     * A helper string variable that converts a named color or an HTML color to xy. Supported values are:
     * <ul>
     *     <li>'White'</li>
     *     <li>'LightGray'</li>
     *     <li>'Gray'</li>
     *     <li>'DarkGray'</li>
     *     <li>'Black'</li>
     *     <li>'Red'</li>
     *     <li>'Pink'</li>
     *     <li>'Orange'</li>
     *     <li>'Yellow'</li>
     *     <li>'Green'</li>
     *     <li>'Magenta'</li>
     *     <li>'Cyan'</li>
     *     <li>'Blue'</li>
     * </ul>
     * Or an HTML hex value can be set as well
     */
    @Optional
    @JsonIgnore
    private String color;

    public Integer getBrightness()
    {
        return brightness;
    }

    public void setBrightness(Integer brightness)
    {
        this.brightness = brightness;
    }

    public Effect getEffect()
    {
        return effect;
    }

    public void setEffect(Effect effect)
    {
        this.effect = effect;
    }

    public Integer getSaturation()
    {
        return saturation;
    }

    public void setSaturation(Integer saturation)
    {
        this.saturation = saturation;
    }

    public Alert getAlert()
    {
        return alert;
    }

    public void setAlert(Alert alert)
    {
        this.alert = alert;
    }

    public Integer getHue()
    {
        return hue;
    }

    public void setHue(Integer hue)
    {
        this.hue = hue;
    }

    public Boolean getOn()
    {
        return on;
    }

    public void setOn(Boolean on)
    {
        this.on = on;
    }

    public Integer getCt()
    {
        return ct;
    }

    public void setCt(Integer ct)
    {
        this.ct = ct;
    }

    public String getXy()
    {
        return xy;
    }

    public void setXy(String xy)
    {
        String[] s = xy.split(",");
        this.xy = xy;
        xyColor = new ArrayList<Double>(2);
        xyColor.add(new Double(s[0].trim()));
        xyColor.add(new Double(s[1].trim()));
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
        Color c = parseColor(color);
        double[] xyz = ColourConverter.colorToXyz(c);
        double[] xy = ColourConverter.XyzToXy(xyz);

        setXy(String.valueOf(xy[0]) + "," + xy[1]);
    }


    public static java.awt.Color parseColor(final String colorString)
    {
        if (StringUtils.equalsIgnoreCase(colorString, "on"))
        {
            return java.awt.Color.WHITE;
        }
        else if (StringUtils.equalsIgnoreCase(colorString, "off"))
        {
            return java.awt.Color.BLACK;
        }
        else if (StringUtils.startsWith(colorString, "#"))
        {
            return ColourConverter.decodeHtmlColorString(colorString);
        }

        final java.awt.Color color = COLOR_MAP.get(StringUtils.upperCase(colorString));
        if (color == null)
        {
            throw new IllegalArgumentException("Invalid color: " + colorString);
        }
        else
        {
            return color;
        }
    }

    @JsonIgnore
    public Boolean isReachable()
    {
        return reachable;
    }

    @JsonIgnore
    public String getColormode()
    {
        return colormode;
    }
}