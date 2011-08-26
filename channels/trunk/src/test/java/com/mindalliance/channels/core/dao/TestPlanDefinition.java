// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.util.file.File;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;

/**
 * ...
 */
public class TestPlanDefinition {

    private PlanDefinition planDefinition;

    private File dataDir;

    private static final String TEST = "test";

    @Before
    public void setup() throws IOException {
        dataDir = new File( "target/channel-test-data" );
        FileUtils.deleteDirectory( dataDir );
        dataDir.mkdirs();

        planDefinition = new PlanDefinition( TEST );
        planDefinition.initialize( new FileSystemResource( dataDir ) );
        planDefinition.setName( TEST );
    }

    @Test
    public void testVersion() throws IOException {
        assertNull( planDefinition.getProductionVersion() );

        PlanDefinition.Version dev = planDefinition.getDevelopmentVersion();
        assertNotNull( dev );
        assertEquals( 1, dev.getNumber() );
        assertFalse( dev.isPersisted() );
        assertEquals( Plan.Status.DEVELOPMENT, dev.getStatus() );
        assertEquals( "test v.1 (DEVELOPMENT)", dev.toString() );
    }

    @Test
    public void testReproductize() throws IOException {
        planDefinition.productize();

        PlanDefinition.Version prod1 = planDefinition.getProductionVersion();
        assertNotNull( prod1 );
        assertEquals( 1, prod1.getNumber() );
        assertEquals( Plan.Status.PRODUCTION, prod1.getStatus() );

        PlanDefinition.Version dev2 = planDefinition.getDevelopmentVersion();
        assertNotNull( dev2 );
        assertEquals( 2, dev2.getNumber() );
        assertEquals( Plan.Status.DEVELOPMENT, dev2.getStatus() );

        planDefinition.initialize( new FileSystemResource( dataDir ) );
    }

}
