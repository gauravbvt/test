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

    private PlanDefinitionManager planDefinitionManager;

    private File props;

    private File dataDir;

    private SimpleIdGenerator generator;

    @Before
    public void setUp() throws IOException {
        dataDir = new File( "target/channel-test-data" );
        FileUtils.deleteDirectory( dataDir );
        props = new File( dataDir, "plans.properties" );

        planDefinitionManager = new PlanDefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( props ) );
        generator = new SimpleIdGenerator();
        planDefinitionManager.setIdGenerator( generator );
    }

    @Test
    public void testEmpty() throws IOException {
        planDefinitionManager.afterPropertiesSet();
        assertTrue( dataDir.exists() );
        assertTrue( props.exists() );
        assertTrue( planDefinitionManager.exists( "Default Plan" ) );
        assertFalse( planDefinitionManager.exists( "bla" ) );

        assertEquals( 1, planDefinitionManager.getPlanNames().size() );
        assertNotNull( planDefinitionManager.get( "default" ) );
        assertNotNull( planDefinitionManager.get( "default", true ) );
        assertNull( planDefinitionManager.get( "default", false ) );

    }

    @Test
    public void testAutosave() throws IOException {
        planDefinitionManager.afterPropertiesSet();

        PlanDefinition definition = planDefinitionManager.getOrCreate( "default", "New name", "Noone" );
        assertEquals( "Noone", definition.getClient() );
        assertTrue( planDefinitionManager.exists( "New name" ) );
        assertFalse( planDefinitionManager.exists( "Default Plan" ) );

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
        planDefinitionManager.afterPropertiesSet();
        planDefinitionManager.load();
        assertNotNull( planDefinitionManager.get( "default" ) );
    }

    @Test
    public void testLoadDefaults() throws IOException {
        planDefinitionManager.setDefaultProperties( new FileSystemResource( "src/main/webapp/WEB-INF/samples/plans.properties" ) );
        planDefinitionManager.afterPropertiesSet();
        assertNotNull( planDefinitionManager.get( "mindalliance.com/channels/plans/demo" ) );
    }

    @Test
    public void testBadProps() throws IOException {
        planDefinitionManager = new PlanDefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( dataDir ) );
        planDefinitionManager.load();
    }

    @Test
    public void testAccessors() {
        planDefinitionManager.setSnapshotThreshold( 5 );
        assertEquals( 5, planDefinitionManager.getSnapshotThreshold() );

        assertSame( generator, planDefinitionManager.getIdGenerator() );

        Resource resource = new InMemoryResource( "bla" );
        planDefinitionManager.setDefaultProperties( resource );
        assertSame( resource, planDefinitionManager.getDefaultProperties() );
        assertFalse( planDefinitionManager.iterator().hasNext() );
    }
}
