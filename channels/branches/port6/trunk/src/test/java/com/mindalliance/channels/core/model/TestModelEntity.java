// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Leftover coverage tests...
 */
public class TestModelEntity {

    @Test
    public void testGetUniversalTypeFor() {
        Assert.assertNotNull( ModelEntity.getUniversalTypeFor( Phase.class ) );
    }

    @Test( expected = RuntimeException.class )
    public void testGetUniversalTypeFor2() {
        Assert.assertNotNull( ModelEntity.getUniversalTypeFor( ModelEntity.class ) );
    }
}
