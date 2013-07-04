/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.module.hue;

import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.module.hue.model.Command;
import org.mule.module.hue.model.Schedule;
import org.mule.module.hue.model.State;
import org.mule.tck.MuleITCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * This is an integration test run through maven when using the 'it' profile i.e.
 * mvn -P it clean install
 *
 * Because the file ends with ITCase it is not picked up in the Unit Test cycle
 * Additionally, this test references a file called .mule-test.properties in the
 * users home directory where sensitive information is stored such as API keys
 */
public class HueConnectorITCase extends MuleITCase
{
    @Override
    protected String[] getRequiredPropertiesForTest()
    {
        return new String[]{"hue.ipAddress", "hue.username"};
    }

    @Override
    protected String getConfigResources()
    {
        return "mule-config.xml";
    }

    @Test
    public void testGetLights() throws Exception
    {

        Flow flow = lookupFlowConstruct("getLightsFlow");
        MuleEvent response = flow.process(getTestEvent(null));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }

    @Test
    public void testGetLightState() throws Exception
    {

        Flow flow = lookupFlowConstruct("getLightStateFlow");
        MuleEvent response = flow.process(getTestEvent(null));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }

    @Test
    public void testGetGroupState() throws Exception
    {

        Flow flow = lookupFlowConstruct("getGroupStateFlow");
        MuleEvent response = flow.process(getTestEvent(null));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }

    @Test
    public void testSetLightState() throws Exception
    {

        Flow flow = lookupFlowConstruct("setLightStateFlow");
        State state = new State();
        state.setHue(101);
        state.setOn(true);
        MuleEvent response = flow.process(getTestEvent(state));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }

    @Test
    public void testSetGroupState() throws Exception
    {
        Flow flow = lookupFlowConstruct("setGroupStateFlow");
        State state = new State();
        state.setHue(102);
        state.setOn(true);
        MuleEvent response = flow.process(getTestEvent(state));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }

    @Test
    public void testCreateSchedule() throws Exception
    {
        Flow flow = lookupFlowConstruct("createScheduleFlow");
        State state = new State();
        state.setHue(103);
        state.setOn(true);

        Command c = new Command();
        c.setMethod("PUT");
        c.setAddress("/api/muletest1234/lights/1/state");
        c.setBody(state);

        Schedule s = new Schedule();
        s.setName("wake");
        s.setTime("2013-12-01T12:00:00");
        s.setCommand(c);
        s.setDescription("Test schedule");

        MuleEvent response = flow.process(getTestEvent(s));
        String payload = response.getMessageAsString();
        Assert.assertFalse(payload.contains("error"));
        System.out.println(payload);
    }
}
