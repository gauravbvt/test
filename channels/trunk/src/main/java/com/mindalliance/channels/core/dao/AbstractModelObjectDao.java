package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.InvalidEntityKindException;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract DAO for model objects.
 * DAOs form non-circular, possibly singleton chains (dao -> dao -> dao -> dao).
 * The subDao must be fully loaded and set with the parent before the parent DAO is loaded.
 * An ID must be unique in the entire chain so that a DAO never shadows an ID in a sub-DAO.
 * ID range transposition of the DAO is done as needed on setting of the subDao.
 * ASSUMPTION: ID range never goes down, always up.
 * ID held in a DB may be out of date so they are transposed dynamically based on their date of record.
 * ASSUMPTION: Only one DAO in the chain defines immutable model objects (the MODEL)
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/23/13
 * Time: 1:02 PM
 */
public abstract class AbstractModelObjectDao {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractModelObjectDao.class );

    /**
     * ModelObjects indexed by id.
     */
    private final Map<Long, ModelObject> indexMap = Collections.synchronizedMap( new HashMap<Long, ModelObject>() );

    private AbstractModelObjectDao subDao;

    /**
     * Pending commands attached to wrapped plan.
     */
    private Journal journal = new Journal();

    private IdGenerator idGenerator;

    protected abstract <T extends ModelObject> void setContextKindOf( T object );

    public abstract ModelObjectContext getModelObjectContext();

    protected abstract void addSpecific( ModelObject object, Long id );

    public abstract void defineImmutableEntities();

    public abstract boolean isLoaded();

    protected abstract boolean isJournaled();

    protected abstract File getJournalFile() throws IOException;

    protected abstract void afterLoad();

    protected abstract long getRecordedLastAssignedId() throws IOException;

    protected abstract File getDataFile() throws IOException;

    protected abstract void beforeSnapshot() throws IOException;

    protected abstract void afterSnapshot() throws IOException;

    protected abstract void afterSaveJournal() throws IOException;

    protected abstract void beforeSaveJournal() throws IOException;

    public abstract void validate();

    protected abstract void importModelObjectContext( Importer importer, FileInputStream in ) throws IOException;


    public void setSubDao( AbstractModelObjectDao subDao ) {
        try {
            this.subDao = subDao;
            assert subDao.isLoaded();
            assert !this.isLoaded();
            // todo implement findAsOf( long id, Date date ) - to be used with all databased ids
        } catch ( Exception e ) {
            LOG.error( "Failed to set sub DAO", e );
            throw new RuntimeException( "Failed to set sub DAO" );
        }
    }

    protected AbstractModelObjectDao getSubDao() {
        return subDao;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator( IdGenerator idGenerator ) {
        this.idGenerator = idGenerator;
    }

    protected Map<Long, ModelObject> getIndexMap() {
        return indexMap;
    }

    public Journal getJournal() {
        return journal;
    }

    protected void setJournal( Journal journal ) {
        this.journal = journal;
    }

    public void add( ModelObject object ) {
        add( object, null );
    }

    public abstract void add( ModelObject object, Long id );

    protected void doAdd( ModelObject object, Long id ) {
        if ( id != null && isIdAssigned( id ) )
            throw new DuplicateKeyException();

        assignId( object, id, getIdGenerator() );
        getIndexMap().put( object.getId(), object );
        addSpecific( object, id );
    }

    public void loadingModelContextWithId( Long modelContextId ) {
        computeAndSetIdShift( modelContextId, getIdGenerator() );
    }

    private boolean isIdAssigned( Long id ) {
        return getIndexMap().containsKey( getIdGenerator().getShiftedId( id ) );
    }

    protected <T extends ModelObject> T assignId( T object, Long id, IdGenerator generator ) {
        object.setId( generator.assignId( id, getModelObjectContext().getUri() ) );
        setContextKindOf( object );
        object.setContextUri( getModelObjectContext().getUri() );
        return object;
    }

    // If object is a model context, calculate and set preset id shift
    // from subDao counterId (to keep ids from overlapping with subDao)
    // Assumes the model context always has lowest id
    private <T extends ModelObject> void computeAndSetIdShift( Long modelContextId, IdGenerator generator ) {
        if ( subDao != null ) {
            long idShift = ( subDao.getIdCounter() - modelContextId ) + 1;
            if ( idShift > 0 ) {
                getModelObjectContext().recordIdShift( idShift );
                generator.setTemporaryIdShift( idShift );
            }
        }
    }

    protected ModelObject lookUp( long id, IdGenerator generator ) {
        return getIndexMap().get( generator.getShiftedId( id ) );
    }

    @SuppressWarnings({"unchecked"})
    // finding by id is always local
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        try {
            return (T) find( id );
        } catch ( NotFoundException exc ) {
            try {
                return findUnknown( clazz, id );
            } catch ( NotFoundException e ) {
                return findUniversal( clazz, id );
            }
        }
    }

    // Delegate to subDao if not found locally
    protected ModelObject find( long id ) throws NotFoundException {
        ModelObject result = lookUp( id, getIdGenerator() );
        if ( result == null ) {
            if ( subDao != null ) {
                result = subDao.find( id );
            }
            if ( result == null )
                throw new NotFoundException();
        }
        return result;
    }

    /**
     * Find by id recorded as of a given date.
     * The id may have shifted since.
     *
     * @param clazz        a model object class
     * @param id           a possibly dated id
     * @param dateOfRecord the date the id was recorded
     * @param <T>          class parameter
     * @return a model object of class T
     * @throws NotFoundException if not found
     */
    @SuppressWarnings({"unchecked"})
    public <T extends ModelObject> T find( Class<T> clazz, long id, Date dateOfRecord ) throws NotFoundException {
        return (T) find( id, dateOfRecord );
    }

    protected ModelObject find( long id, Date dateOfRecord ) throws NotFoundException {
        long currentId = makeIdCurrent( id, dateOfRecord );
        ModelObject result = lookUp( currentId, getIdGenerator() );
        if ( result == null ) {
            if ( subDao != null ) {
                result = subDao.find( id, dateOfRecord );
            }
            if ( result == null )
                throw new NotFoundException();
        }
        return result;
    }

    private long makeIdCurrent( long id, Date dateOfRecord ) {
        return id + getModelObjectContext().getIdShiftSince( dateOfRecord );
    }


    // Finding by class and name traverses the chain of DAOs, from local to subDao
    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        for ( T object : list( clazz ) )
            if ( name.equals( object.getName() ) )
                return object;

        return null;
    }

    @SuppressWarnings({"unchecked"})
    // Listing by class traverses the chain of DAOs, from local to subDao
    public <T extends ModelObject> List<T> list( final Class<T> clazz ) {
        List<T> results = listLocal( clazz );
        if ( subDao != null ) {
            results.addAll( subDao.list( clazz ) );
        }
        return results;
    }

    abstract public <T extends ModelObject> List<T> listLocal( final Class<T> clazz );


    ////////////////////

    @SuppressWarnings("unchecked")
    public <T extends ModelEntity> List<T> listKnownEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (T) object ).isUnknown();
                    }
                }
        );
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz, boolean mustBeReferenced ) {
        return mustBeReferenced
                ? (List<T>) CollectionUtils.select(
                listReferencedEntities( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isActual();
                    }
                } )
                : listActualEntities( clazz );
    }

    @SuppressWarnings("unchecked")
    public <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isActual();
                    }
                }
        );
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select( list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return entity.isImmutable() && !entity.isUnknown()
                                || isReferenced( entity );
                    }
                } );
    }

    @SuppressWarnings("unchecked")
    public Boolean isReferenced( final ModelObject mo ) {
        // Optimized form for return this.countReferences( mo ) > 0;
        boolean hasReference = false;
        Iterator classes = ModelObject.referencingClasses().iterator();
        while ( !hasReference && classes.hasNext() ) {
            List<? extends ModelObject> mos = findAllModelObjects( (Class<? extends ModelObject>) classes.next() );
            hasReference = CollectionUtils.exists(
                    mos,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (ModelObject) object ).references( mo );
                        }
                    }
            );
        }
        return hasReference;
    }

    @SuppressWarnings("unchecked")
    public <T extends ModelObject> List<T> findAllModelObjects( Class<T> clazz ) {
        return list( clazz );
    }

    @SuppressWarnings({"unchecked"})
    public <T extends ModelEntity> List<T> listEntitiesNarrowingOrEqualTo( final T entity, final Place locale ) {
        return (List<T>) CollectionUtils.select(
                list( entity.getClass() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (ModelEntity) object ).narrowsOrEquals( entity, locale );
                    }
                }
        );
    }

    public Boolean entityExists( Class<? extends ModelEntity> clazz, String name, ModelEntity.Kind kind ) {
        ModelEntity entity = ModelEntity.getUniversalType( name, clazz );
        if ( entity == null ) entity = find( clazz, name );
        return entity != null && entity.getKind().equals( kind );
    }

    public <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name ) {
        T result = null;
        T entity = find( entityClass, name );
        if ( entity != null ) {
            if ( entity.isActual() ) result = entity;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends ModelEntity> List<T> findAllActualEntitiesMatching(
            Class<T> entityClass,
            final T entityType,
            final Place locale ) {
        assert entityType.isType();
        return (List<T>) CollectionUtils.select(
                findAllModelObjects( entityClass ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        T entity = (T) object;
                        return entity.isActual() && entity.narrowsOrEquals( entityType, locale );
                    }
                }
        );
    }

    public <T extends ModelEntity> List<T> listKnownEntities(
            Class<T> entityClass,
            Boolean mustBeReferenced,
            Boolean includeImmutables ) {
        return mustBeReferenced
                ? listReferencedEntities( entityClass, includeImmutables )
                : listKnownEntities( entityClass );
    }

    @SuppressWarnings({"unchecked"})
    private <T extends ModelEntity> List<T> listReferencedEntities( Class<T> clazz, final boolean includeImmutables ) {
        return (List<T>) CollectionUtils.select( list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        ModelEntity entity = (ModelEntity) object;
                        return includeImmutables && entity.isImmutable() && !entity.isUnknown()
                                || isReferenced( entity );
                    }
                } );
    }

    @SuppressWarnings("unchecked")
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz ) {
        return (List<T>) CollectionUtils.select(
                list( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isType();
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends ModelEntity> List<T> listTypeEntities( Class<T> clazz, Boolean mustBeReferenced ) {
        return mustBeReferenced
                ? (List<T>) CollectionUtils.select(
                listReferencedEntities( clazz ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (T) object ).isType();
                    }
                } )
                : listTypeEntities( clazz );

    }


    public <T extends ModelEntity> T retrieveEntity(
            Class<T> entityClass, Map<String, Object> state, String key ) {
        Object[] vals = ( (Collection<?>) state.get( key ) ).toArray();
        String name = (String) vals[0];
        boolean type = (Boolean) vals[1];
        if ( type ) {
            return findOrCreateType( entityClass, name );
        } else {
            return findOrCreate( entityClass, name );
        }
    }

    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name ) {
        return findOrCreateType( clazz, name, null );
    }

    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name ) {
        return findOrCreate( clazz, name, null );
    }

    public <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, Long id ) {
        T result;
        // If entity can only be a type, find or create a type.
        if ( !ModelEntity.canBeActualOrType( clazz )
                && ModelEntity.defaultKindFor( clazz ).equals( ModelEntity.Kind.Type ) )
            result = findOrCreateType( clazz, name, id );

        else if ( ModelEntity.getUniversalType( name, clazz ) == null ) {
            result = findOrCreateModelObject( clazz, name, id );
            if ( result.isType() ) {
                throw new InvalidEntityKindException(
                        clazz.getSimpleName() + ' ' + name + " is a type" );
            }
            result.setActual();

        } else
            throw new InvalidEntityKindException(
                    clazz.getSimpleName() + ' ' + name + " is a type" );

        return result;
    }


    //////////////////////


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

    // Creating by name always looks locally and in subDao(s) before creating locally if not found.
    public <T extends ModelObject> T findOrCreateModelObject( Class<T> clazz, String name, Long id ) {
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

    public <T extends ModelEntity> T findOrCreateActual( Class<T> clazz, String name, Long id ) {
        T actualEntity = (T) findOrCreate( clazz, name, id );
        if ( actualEntity.isType() )
            throw new InvalidEntityKindException( clazz.getSimpleName() + ' ' + name + " is actual" );
        actualEntity.setActual();
        return actualEntity;
    }


    public <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, Long id ) {
        T entityType = ModelEntity.getUniversalType( name, clazz );
        if ( entityType == null ) {
            entityType = findOrCreateModelObject( clazz, name, id );
            if ( entityType.isActual() )
                throw new InvalidEntityKindException( clazz.getSimpleName() + ' ' + name + " is actual" );
            entityType.setType();
        }
        return entityType;
    }

    @SuppressWarnings({"unchecked"})
    // An entity not referenced with the model object context (e.g. plan or planCommunity) will not be persisted.
    public Iterator<ModelEntity> iterateEntities() {
        Set<? extends ModelObject> referencers = getReferencingObjects();
        Class<?>[] classes = {
                TransmissionMedium.class, Actor.class, Role.class, Place.class, Organization.class, Event.class,
                Phase.class, InfoProduct.class, InfoFormat.class, Function.class
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
            referencingObjects.addAll( findAllLocalModelObjects( refClass ) );
        return referencingObjects;
    }

    @SuppressWarnings({"unchecked"})
    protected <T extends ModelObject> List<T> findAllLocalModelObjects( Class<T> clazz ) {
        return listLocal( clazz );
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
            if ( object.references( mo ) ) {
                assert referenceAllowed( object, mo );
                return true;
            }
        }
        return false;
    }

    private boolean referenceAllowed( ModelObject referencer, ModelObject referenced ) {
        return referenced.getContextType().compareTo( referencer.getContextType() ) <= 0;
    }

    public void remove( ModelObject object ) {
        getIndexMap().remove( object.getId() );
    }

    public void update( ModelObject object ) {
        // do nothing
    }

    // Persistence

    /**
     * Load persisted plan.
     *
     * @param importer what to use for importing
     * @return the loaded model object context
     * @throws IOException on error
     */
    abstract public ModelObjectContext load( Importer importer ) throws IOException;

    protected ModelObjectContext doLoad( Importer importer ) throws IOException {
        FileInputStream in = null;
        try {
            getIdGenerator().setIdCounter( getMinAssignableId(), getModelObjectContext().getUri() );
            File dataFile = getDataFile();
            if ( dataFile.exists() ) {
                LOG.info(
                        "Importing snapshot for {} from {}",
                        getModelObjectContext().getUri(), dataFile.getAbsolutePath() );
                if ( dataFile.length() > 0 ) {
                    in = new FileInputStream( dataFile );
                    importModelObjectContext( importer, in );
                }
                setJournal( loadJournal( importer ) );
            }
            afterLoad();
            validate();
        } finally {
            getIdGenerator().cancelTemporaryIdShift(); // stop shifting ids on load
            if ( in != null )
                in.close();
        }
        return getModelObjectContext();
    }

    public long getMinAssignableId() throws IOException {
        long subDaoLastId = subDao == null ? IdGenerator.MUTABLE_LOW : subDao.getIdCounter();
        return Math.max( subDaoLastId, getRecordedLastAssignedId() );
    }

    private long getIdCounter() {
        return getIdGenerator().getIdCounter( getModelObjectContext().getUri() );
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

    /**
     * Save the plan to the file repository.
     *
     * @param exporter where to export
     * @throws IOException on errors
     */
    abstract public void save( Exporter exporter ) throws IOException;

    protected void doSave( Exporter exporter ) throws IOException {
        if ( isLoaded() ) {
            beforeSnapshot();
            takeSnapshot( exporter );
            afterSnapshot();
            getJournal().reset();
        }
    }

    private void takeSnapshot( Exporter exporter ) throws IOException {
        LOG.info( "Taking snapshot of {}", getModelObjectContext().getUri() );

        // Make backup
        File dataFile = getDataFile();
        if ( dataFile.length() > 0L ) {
            String backupPath = dataFile.getAbsolutePath() + '_' + System.currentTimeMillis();
            File backup = new File( backupPath );
            dataFile.renameTo( backup );
        }
        // snap
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( getDataFile() );
            exporter.export( out );
        } finally {
            if ( out != null )
                out.close();
        }
    }

    /**
     * Save outstanding journal entries.
     *
     * @param exporter the persistence mechanism
     * @throws IOException on errors
     */
    public void saveJournal( Exporter exporter ) throws IOException {
        getJournalFile().delete();
        FileOutputStream out = null;
        try {
            beforeSaveJournal();
            out = new FileOutputStream( getJournalFile() );
            exporter.export( getJournal(), out );
            afterSaveJournal();
        } finally {
            if ( out != null )
                out.close();
        }
    }

    // /////////////////////

    @SuppressWarnings("unchecked")
    public static <T extends ModelObject> T findUnknown( Class<T> clazz, long id ) throws
            NotFoundException {
        if ( clazz.isAssignableFrom( Actor.class ) && Actor.UNKNOWN.getId() == id )
            return (T) Actor.UNKNOWN;
        else if ( clazz.isAssignableFrom( Event.class ) && Event.UNKNOWN.getId() == id )
            return (T) Event.UNKNOWN;
        else if ( clazz.isAssignableFrom( Organization.class ) && Organization.UNKNOWN.getId() == id )
            return (T) Organization.UNKNOWN;
        else if ( clazz.isAssignableFrom( Place.class ) && Place.UNKNOWN.getId() == id )
            return (T) Place.UNKNOWN;
        else if ( clazz.isAssignableFrom( Role.class ) && Role.UNKNOWN.getId() == id )
            return (T) Role.UNKNOWN;
        else if ( clazz.isAssignableFrom( TransmissionMedium.class ) && TransmissionMedium.UNKNOWN.getId() == id )
            return (T) TransmissionMedium.UNKNOWN;
        else if ( clazz.isAssignableFrom( Requirement.class ) && Requirement.UNKNOWN.getId() == id )
            return (T) Requirement.UNKNOWN;
        else if ( clazz.isAssignableFrom( InfoProduct.class ) && InfoProduct.UNKNOWN.getId() == id )
            return (T) InfoProduct.UNKNOWN;
        else if ( clazz.isAssignableFrom( InfoFormat.class ) && InfoFormat.UNKNOWN.getId() == id )
            return (T) InfoFormat.UNKNOWN;
        else if ( clazz.isAssignableFrom( Function.class ) && Function.UNKNOWN.getId() == id )
            return (T) Function.UNKNOWN;
        else
            throw new NotFoundException();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ModelObject> T findUniversal( Class<T> clazz, long id ) throws NotFoundException {
        if ( clazz.isAssignableFrom( Actor.class )
                && ModelEntity.getUniversalTypeFor( Actor.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Actor.class );
        else if ( clazz.isAssignableFrom( Event.class )
                && ModelEntity.getUniversalTypeFor( Event.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Event.class );
        else if ( clazz.isAssignableFrom( Organization.class )
                && ModelEntity.getUniversalTypeFor( Organization.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Organization.class );
        else if ( clazz.isAssignableFrom( Place.class )
                && ModelEntity.getUniversalTypeFor( Place.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Place.class );
        else if ( clazz.isAssignableFrom( Role.class )
                && ModelEntity.getUniversalTypeFor( Role.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Role.class );
        else if ( clazz.isAssignableFrom( TransmissionMedium.class )
                && ModelEntity.getUniversalTypeFor( TransmissionMedium.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( TransmissionMedium.class );
        else if ( clazz.isAssignableFrom( InfoFormat.class )
                && ModelEntity.getUniversalTypeFor( InfoFormat.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( InfoFormat.class );
        else if ( clazz.isAssignableFrom( InfoProduct.class )
                && ModelEntity.getUniversalTypeFor( InfoProduct.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( InfoProduct.class );
        else if ( clazz.isAssignableFrom( Function.class )
                && ModelEntity.getUniversalTypeFor( Function.class ).getId() == id )
            return (T) ModelEntity.getUniversalTypeFor( Function.class );
        else {
            LOG.debug( "Universal " + clazz.getName() + " " + id + " not found" );
            throw new NotFoundException();
        }
    }


    public boolean cleanup( Class<? extends ModelObject> clazz, String name ) {
        ModelObject mo = find( clazz, name.trim() );
        if ( mo == null || !mo.isEntity() || mo.isUnknown()
                || mo.isImmutable() || !mo.isUndefined()
                || isReferenced( mo ) )
            return false;

        LOG.info( "Removing unused " + mo.getClass().getSimpleName() + ' ' + mo );
        remove( mo );
        return true;
    }

    public <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id ) {
        String root = sanitizeEntityName( name );
        if ( root != null && !root.isEmpty() ) {
            if ( !name.equals( root ) ) {
                LOG.warn( "\"" + name + "\""
                        + " of actual " + clazz.getSimpleName()
                        + "[" + id + "]"
                        + " stripped to \"" + root + "\"" );
            }
            String candidateName = root;
            int i = 1;
            while ( i < 10 ) {
                try {
                    return findOrCreate( clazz, candidateName, id );
                } catch ( InvalidEntityKindException ignored ) {
                    LOG.warn( "Entity name conflict creating actual " + candidateName );
                    candidateName = root + " (" + i + ')';
                    i++;
                }
            }
            LOG.warn( "Unable to create actual " + root );
        }
        return null;

    }

    public <T extends ModelEntity> T safeFindOrCreateType( Class<T> clazz, String name, Long id ) {
        String root = sanitizeEntityName( name );
        T entityType = null;
        if ( root != null && !root.isEmpty() ) {
            if ( !name.equals( root ) ) {
                LOG.warn( "\"" + name + "\""
                        + " of type " + clazz.getSimpleName()
                        + "[" + id + "]"
                        + " stripped to \"" + root + "\"" );
            }
            String candidateName = root;
            boolean success = false;
            int i = 0;
            while ( !success ) {
                try {
                    entityType = findOrCreateType( clazz, candidateName, id );
                    success = true;
                } catch ( InvalidEntityKindException ignored ) {
                    LOG.warn( "Entity name conflict creating type {}", candidateName );
                    candidateName = root.trim() + " type";
                    if ( i > 0 ) candidateName = candidateName + " (" + i + ")";
                    i++;
                }
            }
        }
        return entityType;
    }


    private String sanitizeEntityName( String name ) {
        return StringUtils.abbreviate( ChannelsUtils.cleanUpName( name ), ModelEntity.MAX_NAME_SIZE );
    }

    public long getLastAssignedId() {
        return getIdGenerator().getIdCounter( getModelObjectContext().getUri() );
    }

    public <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name ) {
        T result = null;
        T entity = find( entityClass, name );
        if ( entity != null ) {
            if ( entity.isType() ) result = entity;
        }
        return result;

    }
}
