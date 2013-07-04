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
 * This is a read-only response describing the state of a group of lights
 */
public class GroupAttributes
{
    /**
     * A unique, editable name given to the group.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The last command that was sent to the whole group.
     * Note this is not necessarily the current state of the group.
     */
    @JsonProperty("action")
    private State state;

    /**
     * The IDs of the lights that are in the group.
     */
    @JsonProperty("lights")
    List<String> lights;

    /**
     * Reserved for future?
     */
    @JsonProperty("scenes")
    List<Scene> scenes;

    public String getName()
    {
        return name;
    }

    public State getState()
    {
        return state;
    }

    public List<String> getLights()
    {
        return lights;
    }

    public List<Scene> getScenes()
    {
        return scenes;
    }
}
