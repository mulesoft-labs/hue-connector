/*
Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com

The software in this package is published under the terms of the CPAL v1.0
license, a copy of which has been included with this distribution in the
LICENSE.md file.
*/
package org.mule.module.hue;

import java.util.List;

import junit.framework.Assert;
import org.junit.Test;

public class ColourUtilsTestCase
{
    @Test
    public void testXYZConverter() {
        List<Double> XY = ColourUtils.getXYForColour("red");
        Assert.assertEquals(new Double(0.7350000501337899), XY.get(0));
        Assert.assertEquals(new Double(0.2649999498662102), XY.get(1));
    }
}
