/*
Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com

The software in this package is published under the terms of the CPAL v1.0
license, a copy of which has been included with this distribution in the
LICENSE.md file.
*/
package org.mule.module.hue.model;

/**
 * Error response object
 */
public class Error
{
    private int type;
    private String address;
    private String description;

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
