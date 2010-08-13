// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Plan;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * ...
 */
public class TestPlanManager {

    private PlanManager planManager;

    @Mock
    private DefinitionManager definitionManager;

    @Before
    public void setUp() {
        planManager = new PlanManager( definitionManager );
    }

    @Test
    public void testListeners() {
        PlanListener listener = new PlanListener() {
            public void aboutToProductize( Plan devPlan ) {
            }

            public void productized( Plan plan ) {
            }

            public void created( Plan devPlan ) {
            }

            public void loaded( Plan plan ) {
            }

            public void aboutToUnload( Plan plan ) {
            }
        };

        planManager.addListener( listener );
        planManager.removeListener( listener );
    }

    public void testBadUris() {
        Assert.assertNull( planManager.findDevelopmentPlan( "bla" ) );
        Assert.assertNull( planManager.findProductionPlan( "bla" ) );
    }

}
