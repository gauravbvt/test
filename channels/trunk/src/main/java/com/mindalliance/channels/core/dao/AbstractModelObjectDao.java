package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.InvalidEntityKindException;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.IteratorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract DAO for model objects.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/23/13
 * Time: 1:02 PM
 */
public abstract class AbstractModelObjectDao {   // todo - COMMUNITY - create CommunityDao extends this

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractModelObjectDao.class );

    /**
     * ModelObjects indexed by id.
     */
    private final Map<Long, ModelObject> indexMap = Collections.synchronizedMap( new HashMap<Long, ModelObject>() );

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    private IdGenerator idGenerator;

    protected abstract ModelObjectContext getModelObjectContext();

    protected abstract void addSpecific( ModelObject object, Long id );

    public abstract void defineImmutableEntities();

    public abstract boolean isLoaded();

    protected abstract boolean isJournaled();

    protected abstract File getJournalFile() throws IOException;

    protected abstract void afterLoad();

    protected abstract long getLastAssignedId() throws IOException ;

    protected abstract File getDataFile()  throws IOException;

    abstract void validate();

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    protected Map<Long, ModelObject> getIndexMap() {
        return indexMap;
    }

    public synchronized Journal getJournal() {
        return journal;
    }

    protected void setJournal( Journal journal ) {
        this.journal = journal;
    }

    public void add( ModelObject object ) {
        add( object, null );
    }

    public void add( ModelObject object, Long id ) {
        synchronized ( getIndexMap() ) {
            if ( id != null && getIndexMap().containsKey( id ) )
                throw new DuplicateKeyException();

            assignId( object, id, getIdGenerator() );
            getIndexMap().put( object.getId(), object );
            addSpecific( object, id );
        }
    }

    protected <T extends ModelObject> T assignId( T object, Long id, IdGenerator generator ) {
        object.setId( generator.assignId( id, getModelObjectContext().getUri() ) );
        return object;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) )
            if ( name.equals( object.getName() ) )
                return object;

        return null;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        synchronized ( getIndexMap() ) {
            return (List<T>) CollectionUtils.select( getIndexMap().values(), new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return clazz.isAssignableFrom( object.getClass() );
                }
            } );
        }
    }


    protected ModelObject find( long id ) throws NotFoundException {
        ModelObject result = getIndexMap().get( id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /**
     * Find urls of all attachments.
     *
     * @return a list of strings
     */
    public List<String> findAllAttached() {
        Set<String> allAttachedUrls = new HashSet<String>();
        for ( Attachable attachable : findAllAttachables() )
            for ( Attachment attachment : attachable.getAttachments() )
                allAttachedUrls.add( attachment.getUrl() );

        return new ArrayList<String>( allAttachedUrls );
    }

    protected Set<Attachable> findAllAttachables() {
        return new HashSet<Attachable>( list( ModelObject.class ) );
    }

    public List<UserIssue> findAllUserIssues( ModelObject modelObject ) {
        List<UserIssue> foundIssues = new ArrayList<UserIssue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().getId() == modelObject.getId() )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }

    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name, Long id ) {
        T result = null;

        if ( name != null && !name.isEmpty() ) {

            result = find( clazz, name );
            boolean newId = false;
            if ( result == null && id != null )
                try {
                    ModelObject modelObject = find( id );
                    if ( modelObject.getClass().isAssignableFrom( clazz ) )
                        result = (T) modelObject;
                    else
                        newId = true;
                } catch ( NotFoundException ignored ) {
                    // fall through and create new
                }

            if ( result == null )
                try {
                    // Create new entity with name
                    result = clazz.getConstructor().newInstance();
                    result.setName( name );
                    add( result, newId ? null : id );
                } catch ( InstantiationException e ) {
                    throw new RuntimeException( e );
                } catch ( IllegalAccessException e ) {
                    throw new RuntimeException( e );
                } catch ( NoSuchMethodException e ) {
                    throw new RuntimeException( e );
                } catch ( InvocationTargetException e ) {
                    throw new RuntimeException( e );
                }
        }

        return result;
    }

    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = ModelEntity.getUniversalType( name, clazz );
        if ( entityType == null ) {
            entityType = findOrCreate( clazz, name, id );
            if ( entityType.isActual() )
                throw new InvalidEntityKindException( clazz.getSimpleName() + ' ' + name + " is actual" );
            entityType.setType();
        }
        return entityType;
    }

    @SuppressWarnings({"unchecked"})
    public Iterator<ModelEntity> iterateEntities() {
        Set<? extends ModelObject> referencers = getReferencingObjects();
        Class<?>[] classes = {
                TransmissionMedium.class, Actor.class, Role.class, Place.class, Organization.class, Event.class,
                Phase.class
        };

        Iterator<? extends ModelEntity>[] iterators = new Iterator[classes.length];
        for ( int i = 0; i < classes.length; i++ ) {
            Class<? extends ModelEntity> clazz = (Class<? extends ModelEntity>) classes[i];
            iterators[i] = listReferencedEntities( clazz, referencers ).iterator();
        }
        return (Iterator<ModelEntity>) new IteratorChain( iterators );
    }

    @SuppressWarnings({"unchecked"})
    private Set<? extends ModelObject> getReferencingObjects() {
        Set<? extends ModelObject> referencingObjects = new HashSet<ModelObject>();
        for ( Class refClass : ModelObject.referencingClasses() )
            referencingObjects.addAll( findAllModelObjects( refClass ) );
        return referencingObjects;
    }

    @SuppressWarnings({"unchecked"})
    protected <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        return list( clazz );
    }

    private <T extends ModelEntity> List<T> listReferencedEntities(
            Class<T> clazz, Set<? extends ModelObject> referencers ) {

        Collection<T> inputCollection = list( clazz );
        List<T> answer = new ArrayList<T>( inputCollection.size() );

        for ( T item : inputCollection )
            if ( item.isImmutable() && !item.isUnknown() || isReferenced( item, referencers ) )
                answer.add( item );

        return answer;
    }

    @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
    protected boolean isReferenced( ModelObject mo, Set<? extends ModelObject> referencingObjects ) {
        for ( ModelObject object : referencingObjects ) {
            if ( object.references( mo ) )
                return true;
        }
        return false;
    }

    public void remove( ModelObject object ) {
        getIndexMap().remove( object.getId() );
    }

    public void update( ModelObject object ) {
    }

    // Persistence

    /**
     * Load persisted plan.
     *
     * @param importer what to use for importing
     * @return the loaded model object context
     * @throws IOException on error
     */
    public synchronized ModelObjectContext load( Importer importer ) throws IOException {
        FileInputStream in = null;
        try {
            getIdGenerator().setLastAssignedId( getLastAssignedId(), getModelObjectContext().getUri() );
            File dataFile = getDataFile();
            if ( dataFile.exists() ) {
                LOG.info( "Importing snapshot for {} from {}", getModelObjectContext().getUri(), dataFile.getAbsolutePath() );
                in = new FileInputStream( dataFile );
                importer.importPlan( in );

                setJournal( loadJournal( importer ) );
            }
            afterLoad();
            validate();
        } finally {
            if ( in != null )
                in.close();
        }
        return getModelObjectContext();
    }

    private Journal loadJournal( Importer importer ) throws IOException {
        FileInputStream inputStream = null;
        if ( isJournaled() && importer != null )
            try {
                File journalFile = getJournalFile();
                if ( journalFile.length() > 0L ) {
                    inputStream = new FileInputStream( journalFile );
                    return importer.importJournal( inputStream );
                }
            } finally {
                if ( inputStream != null )
                    inputStream.close();
            }

        return new Journal();
    }


}
