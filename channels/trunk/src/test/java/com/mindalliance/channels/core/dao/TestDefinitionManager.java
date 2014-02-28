// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.util.file.File;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.util.InMemoryResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * ...
 */
public class TestDefinitionManager {

    private ModelDefinitionManager modelDefinitionManager;

    private File props;

    private File dataDir;

    private SimpleIdGenerator generator;

    @Before
    public void setUp() throws IOException {
        dataDir = new File( "target/channel-test-data" );
        FileUtils.deleteDirectory( dataDir );
        props = new File( dataDir, "plans.properties" );

        modelDefinitionManager = new ModelDefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( props ) );
        generator = new SimpleIdGenerator();
        modelDefinitionManager.setIdGenerator( generator );
    }

    @Test
    public void testEmpty() throws IOException {
        modelDefinitionManager.afterPropertiesSet();
        assertTrue( dataDir.exists() );
        assertTrue( props.exists() );
        assertTrue( modelDefinitionManager.exists( "Default Model" ) );
        assertFalse( modelDefinitionManager.exists( "bla" ) );

        assertEquals( 1, modelDefinitionManager.getPlanNames().size() );
        assertNotNull( modelDefinitionManager.get( "default" ) );
        assertNotNull( modelDefinitionManager.get( "default", true ) );
        assertNull( modelDefinitionManager.get( "default", false ) );

    }

    @Test
    public void testAutosave() throws IOException {
        modelDefinitionManager.afterPropertiesSet();

        ModelDefinition definition = modelDefinitionManager.getOrCreate( "default", "New name", "Noone" );
        assertEquals( "Noone", definition.getClient() );
        assertTrue( modelDefinitionManager.exists( "New name" ) );
        assertFalse( modelDefinitionManager.exists( "Default Model" ) );

        assertEquals( "New name|Noone", readProps().getProperty( "default" ) );

        definition.setName( "bla" );
        assertEquals( "bla|Noone", readProps().getProperty( "default" ) );
        definition.setClient( "joe" );
        assertEquals( "bla|joe", readProps().getProperty( "default" ) );
    }

    private Properties readProps() throws IOException {
        Properties newProps = new Properties();
        FileInputStream in = new FileInputStream( props );
        newProps.load( in );
        in.close();
        return newProps;
    }

    @Test
    public void testReload() throws IOException {
        modelDefinitionManager.afterPropertiesSet();
        modelDefinitionManager.load();
        assertNotNull( modelDefinitionManager.get( "default" ) );
    }

    @Test
    public void testLoadDefaults() throws IOException {
        modelDefinitionManager.setDefaultProperties( new FileSystemResource( "src/main/webapp/WEB-INF/samples/plans.properties" ) );
        modelDefinitionManager.afterPropertiesSet();
        assertNotNull( modelDefinitionManager.get( "mindalliance.com/channels/plans/demo" ) );
    }

    @Test
    public void testBadProps() throws IOException {
        modelDefinitionManager = new ModelDefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( dataDir ) );
        modelDefinitionManager.load();
    }

    @Test
    public void testAccessors() {
        modelDefinitionManager.setSnapshotThreshold( 5 );
        assertEquals( 5, modelDefinitionManager.getSnapshotThreshold() );

        assertSame( generator, modelDefinitionManager.getIdGenerator() );

        Resource resource = new InMemoryResource( "bla" );
        modelDefinitionManager.setDefaultProperties( resource );
        assertSame( resource, modelDefinitionManager.getDefaultProperties() );
        assertFalse( modelDefinitionManager.iterator().hasNext() );
    }
}
