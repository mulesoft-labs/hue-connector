/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Definition of a scene
 */
public class Scene
{
    @JsonProperty("name")
    private String name;

    @JsonProperty("lights")
    private List<String> lights;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getLights()
    {
        return lights;
    }

    public void setLights(List<String> lights)
    {
        this.lights = lights;
    }
}
