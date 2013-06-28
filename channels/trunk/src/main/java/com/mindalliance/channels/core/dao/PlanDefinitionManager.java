// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.dao.PlanDefinition.Version;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;

/**
 * Persisted plan definitions.
 */
public class PlanDefinitionManager implements InitializingBean, Iterable<PlanDefinition> {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanDefinitionManager.class );

    /**
     * Default snapshot threshold value (10).
     */
    private static final int DEFAULT_THRESHOLD = 10;

    /**
     * Where the plan *data* is saved. Plan data will be under $dataDirectory/$uri/...
     */
    private final Resource dataDirectory;

    /**
     * The plan property file.
     * If this file is empty, it will be created and initialized with the default properties.
     */
    private final Resource planProperties;

    /**
     * The default properties. May be null, for no default plans.
     */
    private Resource defaultProperties;

    /**
     * The actual plan definitions, indexed by uri.
     */
    private final Map<String, PlanDefinition> definitions =
            Collections.synchronizedMap( new HashMap<String, PlanDefinition>() );

    /**
     * The id generator for new objects.
     */
    private IdGenerator idGenerator;

    /**
     * Number of commands journaled before a snapshot is taken on next command.
     * Default: 10
     */
    private int snapshotThreshold = DEFAULT_THRESHOLD;

    /**
     * Callback for changes on observed plan definitions.
     */
    private final Observer observer = new Observer() {
        @Override
        public void update( Observable o, Object arg ) {
            try {
                save();
            } catch ( IOException e ) {
                LOG.error( "Unable to save definitions", e );
            }
        }
    };


    //---------------------------
    public PlanDefinitionManager( Resource dataDirectory, Resource planProperties ) throws IOException {
        this.dataDirectory = dataDirectory;
        this.planProperties = planProperties;
        if ( dataDirectory != null ) {
            File file = dataDirectory.getFile();
            if ( file.mkdirs() )
                LOG.info( "Created {}", file );
        }
    }

    public PlanDefinitionManager() throws IOException {
        this( null, null );
    }

    //---------------------------

    /**
     * Get a plan definition given an uri
     *
     * @param uri the uri
     * @return the plan definition or null if not found
     */
    public PlanDefinition get( String uri ) {
        return definitions.get( uri );
    }

    /**
     * Find appropriate version of a plan.
     *
     * @param uri         the plan uri
     * @param development get the development version if true, production otherwise
     * @return the plan version or null if not found
     */
    public Version get( String uri, boolean development ) {
        PlanDefinition definition = definitions.get( uri );

        return development ? definition.getDevelopmentVersion()
                : definition.getProductionVersion();
    }

    /**
     * Find appropriate version of a plan.
     *
     * @param uri         the plan uri
     * @param v the version number
     * @return the plan version or null if not found
     */
    public Version get( String uri, int v  ) {
        PlanDefinition definition = definitions.get( uri );

        return definition.get( v );
    }


    //---------------------------

    /**
     * Load the definitions from the plan property file.
     *
     * @throws IOException on errors
     */
    public void load() throws IOException {
        for ( PlanDefinition definition : definitions.values() )
            definition.deleteObserver( observer );
        definitions.clear();

        InputStream inputStream = findInputStream();
        try {
            Properties properties = new Properties();
            if ( inputStream != null )
                properties.load( inputStream );

            synchronized ( definitions ) {
                for ( String uri : properties.stringPropertyNames() ) {
                    memorize( new PlanDefinition( ChannelsUtils.sanitize( uri ), properties.getProperty( uri ) ) );
                }

                if ( definitions.isEmpty() )
                    getOrCreate( "default", "Default Plan", "Internal" );
            }

        } finally {
            if ( inputStream != null )
                inputStream.close();
        }
    }

    /**
     * Create a new plan definition (or get a previously created one).
     *
     * @param uri    the plan uri
     * @param name   the name of the plan. If uri already exists, name will be changed.
     * @param client the client of the plan. If uri already exists, client will be changed.
     * @return the plan definition
     * @throws IOException on data initialization errors
     */
    public PlanDefinition getOrCreate( String uri, String name, String client ) throws IOException {
        PlanDefinition result;
        if ( definitions.containsKey( uri ) )
            result = definitions.get( uri );
        else {
            result = new PlanDefinition( uri );
            memorize( result );
            LOG.info( "Created plan {}", uri );
        }

        result.setName( name );
        result.setClient( client );
        return result;
    }

    /**
     * Delete a plan and its versions.
     *
     * @param uri the plan's uri
     */
    public void delete( String uri ) {
        PlanDefinition definition = get( uri );
        if ( definition != null )
            delete( definition );
    }

    private void delete( PlanDefinition definition ) {
        String uri = definition.getUri();

        definition.deleteObserver( observer );
        definitions.remove( uri );
        definition.delete();
        try {
            save();
        } catch ( IOException e ) {
            LOG.warn( "Failed to update plans.properties", e );
        }
        LOG.debug( "Deleted plan definition {}", uri );
    }

    private void memorize( PlanDefinition definition ) throws IOException {
        definition.initialize( dataDirectory );
        definition.addObserver( observer );
        definitions.put( definition.getUri(), definition );
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( planProperties != null && planProperties.exists() ) {
            File propFile = planProperties.getFile();
            if ( propFile.isFile() ) {
                LOG.debug( "Reading plan definitions from {}", propFile.getAbsolutePath() );
                inputStream = new FileInputStream( propFile );
            } else
                inputStream = null;

        } else if ( defaultProperties != null && defaultProperties.exists() ) {
            LOG.debug( "Reading default plan definitions from {}", defaultProperties.getURI() );
            inputStream = defaultProperties.getInputStream();

        } else {
            LOG.warn( "No user readable plan definitions" );
            inputStream = null;
        }
        return inputStream;
    }

    //---------------------------

    /**
     * Save definitions to the plan property file.
     *
     * @throws IOException on errors
     */
    public void save() throws IOException {
        if ( planProperties != null ) {
            Properties props = new Properties();
            synchronized ( definitions ) {
                for ( PlanDefinition plan : definitions.values() )
                    props.setProperty( plan.getUri(), plan.toString() );
            }

            FileOutputStream stream = new FileOutputStream( planProperties.getFile() );
            try {
                props.store( stream, " Active plans" );
                LOG.debug( "Wrote plan definitions to {}", planProperties.getFile().getAbsolutePath() );

            } finally {
                stream.close();
            }
        }
    }

    //---------------------------
    public Resource getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties( Resource defaultProperties ) {
        this.defaultProperties = defaultProperties;
    }

    /**
     * Get the id generator for plans generated from this definition manager.
     *
     * @return the generator as set (defaults to a SimpleIdGenerator)
     */
    public synchronized IdGenerator getIdGenerator() {
        if ( idGenerator == null )
            idGenerator = new SimpleIdGenerator();

        return idGenerator;
    }

    public synchronized void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    public int getSnapshotThreshold() {
        return snapshotThreshold;
    }

    public void setSnapshotThreshold( int snapshotThreshold ) {
        this.snapshotThreshold = snapshotThreshold;
    }

    /**
     * Load data after all properties have been set.
     */
    @Override
    public void afterPropertiesSet() throws IOException {
        load();
    }

    /**
     * Returns an iterator over a set of PlanDefinitions.
     * Note: not thread safe (definitions update may cause problems)
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<PlanDefinition> iterator() {
        // TODO remove the need for this
        return definitions.values().iterator();
    }

    /**
     * Test if a definition exists for a given plan name.
     *
     * @param name the name
     * @return true if there is such a definition
     */
    public boolean exists( String name ) {
        for ( PlanDefinition definition : definitions.values() )
            if ( name.equals( definition.getName() ) )
                return true;
        return false;
    }

    /**
     * Get all plan names.
     *
     * @return names of all plans (sorted)
     */
    public List<String> getPlanNames() {
        Set<String> set;
        synchronized ( definitions ) {
            set = new HashSet<String>( definitions.size() );
            for ( PlanDefinition plan : definitions.values() )
                set.add( plan.getName() );
        }

        List<String> answer = new ArrayList<String>( set );
        Collections.sort( answer );
        return answer;
    }

    /**
     * Get all plan uris.
     *
     * @return names of all plans (sorted)
     */
    public List<String> getPlanUris() {
        Set<String> set;
        synchronized ( definitions ) {
            set = new HashSet<String>( definitions.size() );
            for ( PlanDefinition plan : definitions.values() )
                set.add( plan.getUri() );
        }
        return new ArrayList<String>( set );
    }


    /**
     * Return the number of managed plan definitions.
     *
     * @return the number of plan definitions
     */
    public int getSize() {
        return definitions.size();
    }

    public String makeUniqueName( String prefix ) {
        List<String> namesTaken = getPlanNames();
        int count = 1;
        String uniqueName = prefix.trim();
        while ( namesTaken.contains( uniqueName ) )
            uniqueName = prefix + '(' + ++count + ')';

        return uniqueName;
    }

    /**
     * A new plan URI is valid if no other plan has the same uri even after sanitization.
     *
     * @param newUri a string
     * @return a boolean
     */
    public boolean isNewPlanUriValid( String newUri ) {
        if ( newUri == null || get( newUri ) != null ) return false;
        for ( PlanDefinition definition : definitions.values() ) {
            if ( ChannelsUtils.sanitize( definition.getUri() )
                    .equals( ChannelsUtils.sanitize( newUri ) ) )
                return false;
        }
        return true;
    }


}
