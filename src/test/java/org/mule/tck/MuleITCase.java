/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.tck;

import org.mule.api.MuleRuntimeException;
import org.mule.construct.Flow;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is an integration test run through maven when using the 'it' profile i.e.
 * <code>mvn -Pit clean install</code>
 *
 * Because the file ends with ITCase it is not picked up in the Unit Test cycle
 * Additionally, this test references a file called .mule-test.properties in the
 * users home directory where sensitive information is stored such as API keys
 */
public abstract class MuleITCase extends org.mule.tck.junit4.FunctionalTestCase
{
    /**
     * Stored in the root of the users home directory
     */
    public static final String MULE_TEST_PROPERTIES = ".mule-test.properties";

    @Override
    protected Properties getStartUpProperties()
    {
        try
        {
            InputStream is = loadResource(System.getProperty("user.home") + "/" + MULE_TEST_PROPERTIES);
            Properties p = new Properties();
            if(is!=null) p.load(is);

            validateProperties(p);
            return p;
        }
        catch (IOException e)
        {
            throw new MuleRuntimeException(e);
        }

    }

   protected void validateProperties(Properties p) {
       String[] names = getRequiredPropertiesForTest();
       StringBuilder buf = new StringBuilder();
       for (String name : names)
       {
           if(p.getProperty(name)==null) {
               buf.append("Required property '").append(name).append("' is needed for this integration test.\n");
           }
       }
       if(buf.length() > 0) {
           buf.append("You need to create/edit a file called '.mule-test.properties' in your home directory and add this property");
           throw new RuntimeException(buf.toString());
       }
   }


    protected abstract String[] getRequiredPropertiesForTest();

    /**
     * Retrieve a flow by name from the registry
     *
     * @param name Name of the flow to retrieve
     */
    protected Flow lookupFlowConstruct(String name)
    {
        return (Flow) muleContext.getRegistry().lookupFlowConstruct(name);
    }
}
