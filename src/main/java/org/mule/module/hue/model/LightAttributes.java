/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A representation of all light attributes including state
 */
public class LightAttributes
{
    /**
     * Details the state of the light, see the State object for more details.
     */
    @JsonProperty("state")
    private State state;

    /**
     * A fixed name describing the type of light e.g. “Extended color light”.
     */
    @JsonProperty("name")
    private String name;

    /**
     * A unique, editable name given to the light.
     */
    @JsonProperty("type")
    private String type;

    /**
     * The hardware model of the light.
     */
    @JsonProperty("modelid")
    private String modelid;

    /**
     * An identifier for the software version running on the light.
     */
    @JsonProperty("swversion")
    private String swversion;

    /**
     * This parameter is reserved for future functionality.
     */
    @JsonProperty("pointsymbol")
    private Object pointsymbol;

    public State getState()
    {
        return state;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getModelid()
    {
        return modelid;
    }

    public String getSwversion()
    {
        return swversion;
    }

    public Object getPointsymbol()
    {
        return pointsymbol;
    }
}
