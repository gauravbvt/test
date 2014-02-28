package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.dao.IdGenerator;
import com.mindalliance.channels.core.dao.SimpleIdGenerator;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
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
 * Community definition manager.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/4/13
 * Time: 3:29 PM
 */
public class CommunityDefinitionManager implements InitializingBean, Iterable<CommunityDefinition> {

    // TODO - extract a common abstract superclass for this and DefinitionManager.

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityDefinitionManager.class );

    /**
     * Default snapshot threshold value (10).
     */
    private static final int DEFAULT_THRESHOLD = 10;

    /**
     * Where the community *data* is saved. Community data will be under $dataDirectory/$uri/...
     */
    private Resource dataDirectory;
    /**
     * The communities property file.
     * If this file is empty, it will be created empty.
     */
    private Resource communityProperties;

    private int snapshotThreshold = DEFAULT_THRESHOLD;

    /**
     * The actual plan definitions, indexed by uri.
     */
    private final Map<String, CommunityDefinition> definitions =
            Collections.synchronizedMap( new HashMap<String, CommunityDefinition>() );

    /**
     * The id generator for new objects.
     */
    private IdGenerator idGenerator;


    public CommunityDefinitionManager( Resource dataDirectory, Resource communityProperties ) throws IOException {
        this.dataDirectory = dataDirectory;
        this.communityProperties = communityProperties;
        if ( dataDirectory != null ) {
            File file = dataDirectory.getFile();
            if ( file.mkdirs() )
                LOG.info( "Created {}", file );
        }
    }

    public CommunityDefinitionManager( ) throws IOException {
        this( null, null );
    }


    /**
     * Callback for changes on observed community definitions.
     */
    private final Observer observer = new Observer() {
        @Override
        public void update( Observable o, Object arg ) {
            try {
                save();
            } catch ( IOException e ) {
                LOG.error( "Unable to save definition", e );
            }
        }
    };

    public int getSnapshotThreshold() {
        return snapshotThreshold;
    }

    public void setSnapshotThreshold( int snapshotThreshold ) {
        this.snapshotThreshold = snapshotThreshold;
    }

    /**
     * Get a plan definition given an uri
     *
     * @param uri the uri
     * @return the plan definition or null if not found
     */
    public CommunityDefinition get( String uri ) {
        return definitions.get( uri );
    }

    /**
     * Load the definitions from the plan property file.
     *
     * @throws IOException on errors
     */
    public void load() throws IOException {
        for ( CommunityDefinition definition : definitions.values() )
            definition.deleteObserver( observer );
        definitions.clear();

        InputStream inputStream = findInputStream();
        try {
            Properties properties = new Properties();
            if ( inputStream != null )
                properties.load( inputStream );

            synchronized ( definitions ) {
                for ( String uri : properties.stringPropertyNames() ) {
                    memorize( new CommunityDefinition( ChannelsUtils.sanitize( uri ), properties.getProperty( uri ) ) );
                }
            }

        } finally {
            if ( inputStream != null )
                inputStream.close();
        }
    }

    /**
     * Create a new community definition (or get a previously created one).
     *
     * @param uri    the plan uri
     * @param planUri the plan's uri
     * @param planVersion the plan's version
     * @return the plan definition
     * @throws IOException on data initialization errors
     */
    public CommunityDefinition getOrCreate( String uri,
                                            String planUri,
                                            int planVersion ) throws IOException {
        CommunityDefinition result;
        if ( definitions.containsKey( uri ) )
            result = definitions.get( uri );
        else {
            result = new CommunityDefinition( uri, planUri, planVersion );
            memorize( result );
            LOG.info( "Created community {}", uri );
        }

        result.notifyObservers(); // triggers saving if new definition created
        return result;
    }

    /**
     * Delete a plan and its versions.
     *
     * @param uri the plan's uri
     */
    public void delete( String uri ) {
        CommunityDefinition definition = get( uri );
        if ( definition != null )
            delete( definition );
    }

    private void delete( CommunityDefinition definition ) {
        String uri = definition.getUri();

        definition.deleteObserver( observer );
        definitions.remove( uri );
        definition.delete();
        try {
            save();
        } catch ( IOException e ) {
            LOG.warn( "Failed to update plans.properties", e );
        }
        LOG.debug( "Deleted definition {}", uri );
    }

    private void memorize( CommunityDefinition definition ) throws IOException {
        definition.initialize( dataDirectory );
        definition.addObserver( observer );
        definitions.put( definition.getUri(), definition );
    }

    private InputStream findInputStream() throws IOException {
        InputStream inputStream;
        if ( communityProperties != null && communityProperties.exists() ) {
            File propFile = communityProperties.getFile();
            if ( propFile.isFile() ) {
                LOG.debug( "Reading community definitions from {}", propFile.getAbsolutePath() );
                inputStream = new FileInputStream( propFile );
            } else
                inputStream = null;

        } else {
            LOG.warn( "No readable community definitions" );
            inputStream = new ByteArrayInputStream( "".getBytes() );

        }
        return inputStream;
    }

    /**
     * Save definitions to the plan property file.
     *
     * @throws IOException on errors
     */
    public void save() throws IOException {
        if ( communityProperties != null ) {
            Properties props = new Properties();
            synchronized ( definitions ) {
                for ( CommunityDefinition communityDefinition : definitions.values() )
                    props.setProperty( communityDefinition.getUri(), communityDefinition.toString() );
            }

            FileOutputStream stream = new FileOutputStream( communityProperties.getFile() );
            try {
                props.store( stream, " Active communities" );
                LOG.debug( "Wrote community definitions to {}", communityProperties.getFile().getAbsolutePath() );

            } finally {
                stream.close();
            }
        }
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


    /**
     * Returns an iterator over a set of CommunityDefinitions.
     * Note: not thread safe (definitions update may cause problems)
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<CommunityDefinition> iterator() {
        // TODO remove the need for this
        return definitions.values().iterator();
    }

    /**
     * Get all community uris.
     *
     * @return names of all plans (sorted)
     */
    public List<String> getCommunityUris() {
        Set<String> set;
        synchronized ( definitions ) {
            set = new HashSet<String>( definitions.size() );
            for ( CommunityDefinition communityDefinition : definitions.values() )
                set.add( communityDefinition.getUri() );
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

    /**
     * A new community URI is valid if no other community has the same uri even after sanitization.
     *
     * @param newUri a string
     * @return a boolean
     */
    public boolean isNewCommunityUriValid( String newUri ) {
        if ( get( newUri ) != null ) return false;
        for ( CommunityDefinition definition : definitions.values() ) {
            if ( ChannelsUtils.sanitize( definition.getUri() )
                    .equals( ChannelsUtils.sanitize( newUri ) ) )
                return false;
        }
        return true;
    }

    /**
     * Load data after all properties have been set.
     */
    @Override
    public void afterPropertiesSet() throws IOException {
        load();
    }


    public CommunityDefinition create( String planUri, int planVersion ) {
        try {
             return getOrCreate(
                    makeNewCommunityUri( planUri ),
                    planUri,
                    planVersion
            );
        } catch ( IOException e ) {
           throw new RuntimeException( "Failed to create new plan community", e );
        }
    }

    private String makeNewCommunityUri( String planUri ) {
        int index = 1;
        String uri = planUri + "_" + index;
        List<String> allUris = getCommunityUris();
        while( allUris.contains( uri ) ) {
            index++;
            uri = planUri + "_" + index;
        }
        return uri;
    }

    /**
     * Counts how many plan communities were created based on a given plan.
     * @param planUri a plan's uri
     * @return an integer count
     */
    public int countCommunitiesFor( final String planUri ) {
        return CollectionUtils.select(
            definitions.values(),
            new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return ((CommunityDefinition) object).getPlanUri().equals( planUri );
                }
            }
        ).size();
    }

    /**
     * Update the plan community's definition to a new plan version and persist.
     * @param planCommunity a plan community
     * @param version a plan version
     * @return the old community definition
     */
    public synchronized CommunityDefinition updateToPlanVersion( PlanCommunity planCommunity, int version ) throws IOException {
        CommunityDefinition oldDefinition = definitions.remove( planCommunity.getUri() );
        getOrCreate(
                planCommunity.getUri(),
                planCommunity.getModelUri(),
                version
        );
        return oldDefinition;
    }
}
