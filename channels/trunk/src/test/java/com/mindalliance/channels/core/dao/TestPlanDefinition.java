// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.model.CollaborationModel;
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

    private ModelDefinition modelDefinition;

    private File dataDir;

    private static final String TEST = "test";

    @Before
    public void setup() throws IOException {
        dataDir = new File( "target/channel-test-data" );
        FileUtils.deleteDirectory( dataDir );
        dataDir.mkdirs();

        modelDefinition = new ModelDefinition( TEST );
        modelDefinition.initialize( new FileSystemResource( dataDir ) );
        modelDefinition.setName( TEST );
    }

    @Test
    public void testVersion() throws IOException {
        assertNull( modelDefinition.getProductionVersion() );

        ModelDefinition.Version dev = modelDefinition.getDevelopmentVersion();
        assertNotNull( dev );
        assertEquals( 1, dev.getNumber() );
        assertFalse( dev.isPersisted() );
        assertEquals( CollaborationModel.Status.DEVELOPMENT, dev.getStatus() );
        assertEquals( "test v.1 (DEVELOPMENT)", dev.toString() );
    }

    @Test
    public void testReproductize() throws IOException {
        modelDefinition.productize();

        ModelDefinition.Version prod1 = modelDefinition.getProductionVersion();
        assertNotNull( prod1 );
        assertEquals( 1, prod1.getNumber() );
        assertEquals( CollaborationModel.Status.PRODUCTION, prod1.getStatus() );

        ModelDefinition.Version dev2 = modelDefinition.getDevelopmentVersion();
        assertNotNull( dev2 );
        assertEquals( 2, dev2.getNumber() );
        assertEquals( CollaborationModel.Status.DEVELOPMENT, dev2.getStatus() );

        modelDefinition.initialize( new FileSystemResource( dataDir ) );
    }

}
