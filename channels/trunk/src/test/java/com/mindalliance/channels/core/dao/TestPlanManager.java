// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.dao.PlanListener;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Plan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

/**
 * ...
 */
public class TestPlanManager {

    private PlanManager planManager;

    @Mock
    private DefinitionManager definitionManager;

    public TestPlanManager() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );        
        planManager = new PlanManager( definitionManager );
    }

    @Test
    public void testListeners() {
        PlanListener listener = new DummyPlanListener();

        planManager.addListener( listener );
        planManager.removeListener( listener );
    }

    @Test
    public void testBadUris() {
        assertNull( planManager.findDevelopmentPlan( "bla" ) );
        assertNull( planManager.findProductionPlan( "bla" ) );
    }

    //================================
    private static class DummyPlanListener implements PlanListener {

        public void aboutToProductize( Plan devPlan ) {
        }

        public void productized( Plan plan ) {
        }

        public void created( Plan devPlan ) {
        }

        public void loaded( PlanDao planDao ) {
        }

        public void aboutToUnload( PlanDao planDao ) {
        }
    }
}
