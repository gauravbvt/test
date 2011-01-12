// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.dao;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.util.file.File;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.util.InMemoryResource;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * ...
 */
public class TestDefinitionManager {

    private DefinitionManager definitionManager;

    private File props;

    private File dataDir;

    private SimpleIdGenerator generator;

    @Before
    public void setUp() throws IOException {
        dataDir = new File( "target/channel-test-data" );
        FileUtils.deleteDirectory( dataDir );
        props = new File( dataDir, "plans.properties" );

        definitionManager = new DefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( props ) );
        generator = new SimpleIdGenerator();
        definitionManager.setIdGenerator( generator );
    }

    @Test
    public void testEmpty() throws IOException {
        definitionManager.afterPropertiesSet();
        assertTrue( dataDir.exists() );
        assertTrue( props.exists() );
        assertTrue( definitionManager.exists( "Default Plan" ) );
        assertFalse( definitionManager.exists( "bla" ) );

        assertEquals( 1, definitionManager.getPlanNames().size() );
        assertNotNull( definitionManager.get( "default" ) );
        assertNotNull( definitionManager.get( "default", true ) );
        assertNull( definitionManager.get( "default", false ) );

    }

    @Test
    public void testAutosave() throws IOException {
        definitionManager.afterPropertiesSet();

        PlanDefinition definition = definitionManager.getOrCreate( "default", "New name", "Noone" );
        assertEquals( "Noone", definition.getClient() );
        assertTrue( definitionManager.exists( "New name" ) );
        assertFalse( definitionManager.exists( "Default Plan" ) );

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
        definitionManager.afterPropertiesSet();
        definitionManager.load();
        assertNotNull( definitionManager.get( "default" ) );
    }

    @Test
    public void testLoadDefaults() throws IOException {
        definitionManager.setDefaultProperties( new FileSystemResource( "src/main/webapp/WEB-INF/samples/plans.properties" ) );
        definitionManager.afterPropertiesSet();
        assertNotNull( definitionManager.get( "mindalliance.com/channels/plans/demo" ) );
    }

    @Test
    public void testBadProps() throws IOException {
        definitionManager = new DefinitionManager( new FileSystemResource( dataDir ), new FileSystemResource( dataDir ) );
        definitionManager.load();
    }

    @Test
    public void testAccessors() {
        definitionManager.setSnapshotThreshold( 5 );
        assertEquals( 5, definitionManager.getSnapshotThreshold() );

        assertSame( generator,definitionManager.getIdGenerator() );

        Resource resource = new InMemoryResource( "bla" );
        definitionManager.setDefaultProperties( resource );
        assertSame( resource, definitionManager.getDefaultProperties() );
        assertFalse( definitionManager.iterator().hasNext() );
    }
}
