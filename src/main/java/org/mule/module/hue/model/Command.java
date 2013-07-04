/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Representation of a command that that can be sent to the scheduling
 * API to execute the command at a specific time
 * i.e. 'turn the lights on in the morning at 7am'
 *
 * A command represents a resource address, the HTTP verb and the body as the data to
 * send to the resource.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Command
{
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    /**
     * The actions of the command. The only thing that seems to make sense for this
     * feature right now is to change the state of a light or a group, so the only
     * valid object for body is {@link org.mule.module.hue.model.State}
     */
    @JsonProperty("body")
    private CommandBody	body;

    /**
     * The URN of the resource to invoke i.e. '/api/[username]/groups/0/action'
     */
    @JsonProperty("address")
    private String	address;

    /**
     * The HTTP method to invoke. Either GET, POST, PUT or DELETE
     */
    @JsonProperty("method")
    private Method method;

    public CommandBody getBody()
    {
        return body;
    }

    public void setBody(CommandBody body)
    {
        this.body = body;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Method getMethod()
    {
        return method;
    }

    public void setMethod(Method method)
    {
        this.method = method;
    }
}
