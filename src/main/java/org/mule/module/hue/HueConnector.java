/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Category;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Optional;
import org.mule.module.hue.model.Command;
import org.mule.module.hue.model.GroupAttributes;
import org.mule.module.hue.model.LightAttributes;
import org.mule.module.hue.model.Schedule;
import org.mule.module.hue.model.State;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Connects to the Philips Hue smart lighting system.
 * This connector does not cover the full API rather the more common things that
 * a developer would automate from Mule.
 *
 * @author ross@mulesoft.com
 */
@Connector(name = "hue", schemaVersion = "1.0", friendlyName = "Philips Hue", minMuleVersion = "3.4.0")
@Category(name = "org.mule.tooling.ui.modules.core.miscellaneous", description = "Miscellaneous")
public class HueConnector
{
    /**
     * The expected date format by this service - used to create scheduled commands
     */
    public static final String ISO86012004Format = "yyyy-MM-ddThh:mm:ss";

    /**
     * The local ip address for the hue base station
     */
    @Configurable
    private String ipAddress;

    /**
     * the username that has access to the hue base station
     */
    @Configurable
    @ConnectionKey
    private String username;

    private Client httpClient = Client.create();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * The local ip address for the hue base station
     *
     * @return The local ip address for the hue base station
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * Sets the local ip address for the hue base station
     *
     * @param ipAddress the local ip address for the hue base station
     */
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the username that has access to the hue base station
     *
     * @return the username that has access to the hue base station
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username that has access to the hue base station
     *
     * @param userName the username that has access to the hue base station
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Connect
     *
     * @throws ConnectionException
     */
    @Start
    public void connect() throws ConnectionException
    {
        String res = httpClient.resource(getBaseUri()).get(String.class);
        if (res.contains("error"))
        {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "unauthorized user", res);
        }
    }

    /**
     * Sets the state of an individual light
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:set-light-state}
     *
     * @param lightId the identifier for the light to change
     * @param state   the new state to apply to the light
     * @return The new state of the light
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public State setLightState(String lightId, State state) throws IOException
    {
        WebResource resource = httpClient.resource(getBaseUri() + "/lights/" + lightId + "/state");
        String st = mapper.writeValueAsString(state);
        String response = resource.put(String.class, st);
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        return state;
    }

    /**
     * Sets the state of a light group.  All lights in the group will be given the same state
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:set-group-state}
     *
     * @param groupId the identifier for the group to change. The bridge has a default group is 0 which includes all lights known by the bridge
     * @param state   the new state to apply to the group
     * @return The new state of the group
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public State setGroupState(String groupId,  @Placement(group = "State") State state) throws IOException
    {
        WebResource resource = httpClient.resource(getBaseUri() + "/groups/" + groupId + "/action");
        String response = resource.put(String.class, mapper.writeValueAsString(state));
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        return state;
    }

    /**
     * Gets a list of all lights that have been discovered by the bridge
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:get-lights}
     *
     * @return a list of all lights in the system, each light has a name and unique identification number.
     *         If there are no lights in the system then the bridge will return an empty object, {}.
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public String getLights() throws IOException
    {
        WebResource resource = httpClient.resource(getBaseUri() + "/lights");
        String response = resource.get(String.class);
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        return response;
    }

    /**
     * Creates a scheduled command tht will execute at a specific date and time
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:create-schedule}
     *
     * @param command     Representation of a command that that can be sent to the scheduling
     *                    API to execute the command at a specific time
     *                    i.e. 'turn the lights on in the morning at 7am'
     *                    A command represents a resource address, the HTTP verb and the body as the data to
     *                    send to the resource.
     * @param scheduleName for the new schedule. If a name is not specified then the default name, “schedule”, is used.
     *                    If the name is already taken a space and number will be appended by the bridge, e.g. “schedule 1”.
     * @param time        Time when the scheduled event will occur in ISO 8601:2004 format.
     *                    The bridge measures time in UTC and only accepts extended format, non-recurring, local time (YYYY-MM-DDThh:mm:ss).
     *                    Incorrectly formatted dates will raise an error of type 7. If the time is in the past an error 7 will also be raised.
     * @param description Description of the new schedule. If the description is not specified it will be empty.
     *
     * @return a string json response with either success message.  If there is an error returned then an IOExecption is thrown
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public String createSchedule(@Placement(group = "Schedule", order = 1) @FriendlyName("Name") String scheduleName,
                                 @Placement(group = "Schedule", order = 2) String time,
                                 @Placement(group = "Schedule", order = 3) @Optional String description,
                                 @Placement(group = "Command to Execute") Command command) throws IOException
    {
        Schedule s = new Schedule();
        s.setCommand(command);
        s.setName(scheduleName);
        s.setTime(time);
        s.setDescription(description);

        WebResource resource = httpClient.resource(getBaseUri() + "/schedules");
        String response = resource.post(String.class, mapper.writeValueAsString(s));
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        return response;

    }

    /**
     * Gets the current state of an individual light
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:get-light-state}
     *
     * @param lightId the identifier of the light to query state
     *
     * @return The complete state of the light
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public LightAttributes getLightState(String lightId) throws IOException
    {
        WebResource resource = httpClient.resource(getBaseUri() + "/lights/" + lightId);
        String response = resource.get(String.class);
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        System.out.println("This:" + response);
        return mapper.readValue(response, LightAttributes.class);
    }

    /**
     * Gets the current state of an individual light
     * <p/>
     * {@sample.xml ../../../doc/Hue-connector.xml.sample hue:get-group-state}
     *
     * @param groupId the identifier of the group to query state
     *
     * @return The complete state of a group
     * @throws IOException if there is a connection error or if the call returns an error response
     */
    @Processor
    public GroupAttributes getGroupState(String groupId) throws IOException
    {
        WebResource resource = httpClient.resource(getBaseUri() + "/groups/" + groupId);
        String response = resource.get(String.class);
        if (response.contains("error"))
        {
            throw new IOException(response);
        }
        return mapper.readValue(response, GroupAttributes.class);
    }

    public final String getBaseUri()
    {
        return "http://" + ipAddress + "/api/" + username;
    }
}
