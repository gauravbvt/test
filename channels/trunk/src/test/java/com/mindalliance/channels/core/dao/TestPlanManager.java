// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.ModelDao;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.dao.ModelListener;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.dao.ModelManagerImpl;
import com.mindalliance.channels.core.model.CollaborationModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

/**
 * ...
 */
public class TestPlanManager {

    private ModelManager modelManager;

    @Mock
    private ModelDefinitionManager modelDefinitionManager;

    public TestPlanManager() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );        
        modelManager = new ModelManagerImpl( modelDefinitionManager );
    }

    @Test
    public void testListeners() {
        ModelListener listener = new com.mindalliance.channels.dao.TestPlanManager.DummyModelListener();

        modelManager.addListener( listener );
        modelManager.removeListener( listener );
    }

    @Test
    public void testBadUris() {
        assertNull( modelManager.findDevelopmentModel( "bla" ) );
        assertNull( modelManager.findProductionModel( "bla" ) );
    }

    //================================
    private static class DummyModelListener implements ModelListener {

        public void aboutToProductize( CollaborationModel devCollaborationModel ) {
        }

        public void productized( CollaborationModel collaborationModel ) {
        }

        public void created( CollaborationModel devCollaborationModel ) {
        }

        public void loaded( ModelDao modelDao ) {
        }

        public void aboutToUnload( ModelDao modelDao ) {
        }

        @Override
        public void created( PlanCommunity planCommunity ) {
        }

        @Override
        public void loaded( CommunityDao communityDao ) {
        }
    }
}
