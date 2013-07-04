/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue.model;

import org.mule.api.annotations.param.Optional;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Defines a schedule that will be executed from the bridge. It includes an identifier
 * at time and a command that will be executed
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Schedule
{
    /**
     * Name for the new schedule. If a name is not specified then the default name, “schedule”, is used.
     * If the name is already taken a space and number will be appended by the bridge, e.g. “schedule 1”.
     */
    @JsonProperty("name")
    private String	name;

    /**
     * Description of the new schedule. If the description is not specified it will be empty.
     */
    @JsonProperty("description")
    @Optional
    private String	description;

    /**
     * Command to execute when the scheduled event occurs. If the command is not valid then an error of type 7 will be raised.
     * Tip: Stripping unnecessary whitespace can help to keep commands within the 90 character limit.
     */
    @JsonProperty("command")
    private Command	command;

    /**
     * Time when the scheduled event will occur in ISO 8601:2004 format.
     * The bridge measures time in UTC and only accepts extended format, non-recurring, local time (YYYY-MM-DDThh:mm:ss).
     * Incorrectly formatted dates will raise an error of type 7. If the time is in the past an error 7 will also be raised.
     */
    @JsonProperty("time")
    private String	time;

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Command getCommand()
    {
        return command;
    }

    public void setCommand(Command command)
    {
        this.command = command;
    }

}