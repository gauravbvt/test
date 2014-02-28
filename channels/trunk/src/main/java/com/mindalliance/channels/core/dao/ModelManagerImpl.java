/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.ModelDefinition.Version;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Persistent store for plans.
 */
public class ModelManagerImpl implements ModelManager {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelManagerImpl.class );

    /**
     * Manager for plan manager event listeners.
     */
    private final Listeners listeners = new Listeners();

    /**
     * Plan persistence manager.
     */
    private final ModelDefinitionManager modelDefinitionManager;
    /**
     * All the plans, indexed by version uri (uri:version).
     */
    private final Map<Version, ModelDao> daoIndex =
            Collections.synchronizedMap(
                    new HashMap<Version, ModelDao>() );

    /**
     * For each plan uri, usernames of users who are not in sync with its current version.
     * {uri => username}
     */
    private final Map<String, List<String>> outOfSyncUsers = new HashMap<String, List<String>>();

    /**
     * Pre-defined and immutable transmission media.
     */
    private List<TransmissionMedium> builtInMedia = new ArrayList<TransmissionMedium>();

    @Autowired
    private UserRecordService userRecordService;

    private ImportExportFactory importExportFactory;
    /**
     * Name of the default support community.
     */
    private String defaultSupportCommunity;
    /**
     * URI of the default community calendar host.
     */
    private String defaultCommunityCalendarHost;
    /**
     * Name of the default community calendar.
     */
    private String defaultCommunityCalendar;
    /**
     * Name of the default community calendar private ticket.
     */
    private String defaultCommunityCalendarPrivateTicket;

    private String serverUrl;

    /**
     * Required for AOP decorations.
     */
    public ModelManagerImpl() {
        this( null );
    }

    public ModelManagerImpl( ModelDefinitionManager modelDefinitionManager ) {
        this.modelDefinitionManager = modelDefinitionManager;
    }

    @Override
    public void addListener( ModelListener listener ) {
        listeners.addListener( listener );
    }

    @Override
    public void removeListener( ModelListener listener ) {
        listeners.removeListener( listener );
    }

    public UserRecordService getUserRecordService() {
        return userRecordService;
    }

    public void setUserRecordService( UserRecordService userRecordService ) {
        this.userRecordService = userRecordService;
    }

    public ImportExportFactory getImportExportFactory() {
        return importExportFactory;
    }

    public void setImportExportFactory( ImportExportFactory importExportFactory ) {
        this.importExportFactory = importExportFactory;
    }

    @Override
    public List<TransmissionMedium> getBuiltInMedia() {
        return builtInMedia;
    }

    public void setBuiltInMedia( List<TransmissionMedium> builtInMedia ) {
        this.builtInMedia = builtInMedia;
    }

    @Override
    public String getDefaultSupportCommunity() {
        return defaultSupportCommunity;
    }

    @Override
    public void setDefaultSupportCommunity( String defaultSupportCommunity ) {
        this.defaultSupportCommunity = defaultSupportCommunity;
    }

    @Override
    public String getDefaultCommunityCalendar() {
        return defaultCommunityCalendar;
    }

    @Override
    public void setDefaultCommunityCalendar( String defaultCommunityCalendar ) {
        this.defaultCommunityCalendar = defaultCommunityCalendar;
    }

    @Override
    public String getDefaultCommunityCalendarHost() {
        return defaultCommunityCalendarHost;
    }

    @Override
    public void setDefaultCommunityCalendarHost( String defaultCommunityCalendarHost ) {
        this.defaultCommunityCalendarHost = defaultCommunityCalendarHost;
    }

    @Override
    public String getDefaultCommunityCalendarPrivateTicket() {
        return defaultCommunityCalendarPrivateTicket;
    }

    @Override
    public void setDefaultCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket ) {
        this.defaultCommunityCalendarPrivateTicket = defaultCommunityCalendarPrivateTicket;
    }

    @Override
    public Version getVersion( CollaborationModel collaborationModel ) {
        return modelDefinitionManager.get( collaborationModel.getUri(), collaborationModel.isDevelopment() );
    }

    @Override
    public ModelDao getDao( CollaborationModel collaborationModel ) {
        return getDao( collaborationModel.getUri(), collaborationModel.isDevelopment() );
    }

    @Override
    public ModelDao getDao( String uri, boolean development ) {
        synchronized ( daoIndex ) {
            Version version = modelDefinitionManager.get( uri, development );
            if ( version == null )
                return null;

            ModelDao result = daoIndex.get( version );
            if ( result == null ) {
                result = createDao( version );
                daoIndex.put( version, result );
            }

            return result;
        }
    }

    @Override
    public ModelDao getDao( String uri, int v ) {
        synchronized ( daoIndex ) {
            Version version = modelDefinitionManager.get( uri, v );
            if ( version == null )
                return null;

            ModelDao result = daoIndex.get( version );
            if ( result == null ) {
                result = createDao( version );
                daoIndex.put( version, result );
            }

            return result;
        }
    }


    private ModelDao createDao( Version version ) {
        try {
            ModelDao dao = new ModelDao( version );
            dao.setUserDetailsService( userRecordService );
            dao.setIdGenerator( modelDefinitionManager.getIdGenerator() );
            dao.resetModel();
            dao.defineImmutableEntities();
            dao.defineImmutableMedia( builtInMedia );
            if ( importExportFactory != null )
                dao.load( importExportFactory.createImporter( "daemon", dao ) );
            else
                dao.validate();
            listeners.fireLoaded( dao );
            return dao;

        } catch ( IOException e ) {
            LOG.error( "Unable to load model " + version, e );
            return null;
        }
    }

    @Override
    public ModelDefinitionManager getModelDefinitionManager() {
        return modelDefinitionManager;
    }

    @Override
    public List<CollaborationModel> getModels() {
        List<CollaborationModel> result = new ArrayList<CollaborationModel>( daoIndex.size() );
        synchronized ( modelDefinitionManager ) {
            for ( ModelDefinition definition : modelDefinitionManager ) {
                String uri = definition.getUri();
                ModelDao devDao = getDao( uri, true );
                if ( devDao != null )
                    result.add( devDao.getCollaborationModel() );

                ModelDao prodDao = getDao( uri, false );
                if ( prodDao != null )
                    result.add( prodDao.getCollaborationModel() );
            }
        }
        Collections.sort( result );
        return Collections.unmodifiableList( result );
    }

    @Override
    public CollaborationModel getDevelopmentModel( String planUri ) {
        return (CollaborationModel) CollectionUtils.find(
                getModelsWithUri( planUri ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CollaborationModel) object ).isDevelopment();
                    }
                }
        );
    }


    @Override
    public void save( CollaborationModel collaborationModel ) {
        try {
            ModelDao dao = getDao( collaborationModel );
            dao.save( importExportFactory.createExporter( "daemon", dao ) );

        } catch ( IOException e ) {
            throw new RuntimeException( "Failed to save journal", e );
        }
    }

    @Override
    public synchronized void assignModels() {

        // Assign default plan to users
        if ( userRecordService != null )
            for ( ChannelsUser user : userRecordService.getAllEnabledUsers() ) {
                CollaborationModel collaborationModel = user.getCollaborationModel();
                if ( collaborationModel == null )
                    user.setCollaborationModel( getDefaultModel( user ) );
                else {
                    String uri = collaborationModel.getUri();
                    if ( collaborationModel.isRetired() ) {
                        // User was connected to an old production plan
                        user.setCollaborationModel( findProductionModel( uri ) );

                    } else if ( collaborationModel.isProduction() && user.isDeveloperOrAdmin( uri ) )
                        // Plan was put in production
                        user.setCollaborationModel( findDevelopmentModel( uri ) );
                }
            }

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollaborationModel> getModelsWithUri( final String uri ) {
        return (List<CollaborationModel>) CollectionUtils.select( getModels(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (CollaborationModel) object ).getUri().equals( uri );
            }
        } );
    }

    @Override
    public CollaborationModel getModel( String uri, int v ) {

        ModelDefinition definition = modelDefinitionManager.get( uri );
        if ( definition != null ) {
            Version version = definition.get( v );
            if ( version != null )
//                return getDao( uri, v.isDevelopment() ).getPlan(); // todo - WRONG: only dev or prod, no retired version
                return getDao( uri, v ).getCollaborationModel();
        }

        return null;
    }

    @Override
    public Segment importSegment( String userName, CollaborationModel collaborationModel, InputStream inputStream ) {
        // Import and switch to segment
        LOG.debug( "Importing segment" );
        try {
            ModelDao dao = getDao( collaborationModel );

            Importer importer = importExportFactory.createImporter( userName, dao );
            Segment imported = importer.importSegment( inputStream );

            dao.save( importExportFactory.createExporter( userName, dao ) );
            LOG.info( "Imported segment {}", imported.getName() );
            return imported;

        } catch ( IOException e ) {
            // TODO redirect to a proper error screen... user has to know...
            String s = "Import error";
            LOG.error( s, e );
            throw new RuntimeException( s, e );
        }
    }

    @Override
    public void delete( CollaborationModel collaborationModel ) {
        String uri = collaborationModel.getUri();

        for ( ChannelsUser user : userRecordService.getAllEnabledUsers() )
            user.getUserRecord().clearAccess( uri );

        assignModels();

        synchronized ( daoIndex ) {
            Set<Entry<Version, ModelDao>> entries =
                    new HashSet<Entry<Version, ModelDao>>( daoIndex.entrySet() );
            for ( Entry<Version, ModelDao> entry : entries ) {
                Version version = entry.getKey();
                ModelDao dao = entry.getValue();
                if ( uri.equals( version.getPlanDefinition().getUri() ) ) {
                    listeners.fireAboutToUnload( dao );
                    daoIndex.remove( version );
                }
            }
        }

        modelDefinitionManager.delete( uri );
        LOG.info( "Deleted {}", collaborationModel );
    }

    @Override
    public void productize( CollaborationModel oldDevCollaborationModel ) {

        // Stop issue scanning
        listeners.fireAboutToProductize( oldDevCollaborationModel );

        // Make sure journal is flushed
        oldDevCollaborationModel.setWhenVersioned( new Date() );
        save( oldDevCollaborationModel );

        // Mark loaded production version of plan retired
        CollaborationModel oldProductionCollaborationModel = findProductionModel( oldDevCollaborationModel.getUri() );
        if ( oldProductionCollaborationModel != null ) {
            oldProductionCollaborationModel.setRetired();
            //daoIndex.remove( oldProductionPlan.getUri() );
        }

        // Create development plan from copy of old dev plan
        CollaborationModel newDevCollaborationModel = makeNewDevPlan( oldDevCollaborationModel );

        // Mark loaded development version of plan as production
        oldDevCollaborationModel.setProduction();

        assignModels();

        // Restart issue scanning
        listeners.fireCreated( newDevCollaborationModel );
        listeners.fireProductized( oldDevCollaborationModel );

        LOG.info( "Productized {}", oldDevCollaborationModel );
    }

    @Override
    public CollaborationModel findProductionModel( String uri ) {
        ModelDao modelDao = getDao( uri, false );
        return modelDao == null ? null : modelDao.getCollaborationModel();
    }

    @Override
    public CollaborationModel findDevelopmentModel( String uri ) {
        ModelDao modelDao = getDao( uri, true );
        return modelDao == null ? null : modelDao.getCollaborationModel();
    }

    private CollaborationModel makeNewDevPlan( CollaborationModel oldDevCollaborationModel ) {
        try {
            // Create new persisted dev version
            Version oldVersion = getVersion( oldDevCollaborationModel );
            oldVersion.getPlanDefinition().productize();
            ModelDao newDao = getDao( oldDevCollaborationModel.getUri(), true );

            // TODO - remove initial data_???.xml backup file since it has producers set.
            return newDao.getCollaborationModel();

        } catch ( IOException e ) {
            throw new RuntimeException(
                    "Failed to make new development version of " + oldDevCollaborationModel, e );
        }
    }

    @Override
    public boolean addProducer( String producer, CollaborationModel collaborationModel ) {
        if ( !collaborationModel.isDevelopment() )
            throw new IllegalStateException(
                    "Model " + collaborationModel + " is not a development version" );

        collaborationModel.addProducer( producer );

        return allDevelopersInFavorToPutInProduction( collaborationModel );
    }

    @Override
    public boolean allDevelopersInFavorToPutInProduction( CollaborationModel collaborationModel ) {
        final List<String> producers = collaborationModel.getProducers();
        List<ChannelsUser> developers = userRecordService.getStrictlyDevelopers( collaborationModel.getUri() );
        return !developers.isEmpty() &&
                !CollectionUtils.exists(
                        developers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return !producers.contains( ( (ChannelsUser) object ).getUsername() );
                            }
                        }
                );
    }

    @Override
    public synchronized void setResyncRequired( String uri ) {
        outOfSyncUsers.put( uri, userRecordService.getUsernames() );
    }

    @Override
    public synchronized void resynced( String userName ) {
        for ( Entry<String, List<String>> stringListEntry : outOfSyncUsers.entrySet() ) {
            List<String> usernames = stringListEntry.getValue();
            if ( usernames != null )
                usernames.remove( userName );
        }
    }

    @Override
    public synchronized boolean isOutOfSync( String userName, String uri ) {
        List<String> usernames = outOfSyncUsers.get( uri );
        return usernames != null && usernames.contains( userName );
    }

    @Override
    public List<CollaborationModel> getModelsModifiableBy( ChannelsUser user ) {
        List<CollaborationModel> collaborationModelList = getModels();
        List<CollaborationModel> result = new ArrayList<CollaborationModel>( collaborationModelList.size() );

        for ( CollaborationModel p : collaborationModelList )
            if ( user.isDeveloperOrAdmin( p.getUri() ) )
                result.add( p );

        return result;
    }

    @Override
    public List<CollaborationModel> getModelsReadableBy( ChannelsUser user ) {
        List<CollaborationModel> collaborationModelList = getModels();
        List<CollaborationModel> result = new ArrayList<CollaborationModel>( collaborationModelList.size() );

        for ( CollaborationModel object : collaborationModelList )
            if ( user.hasAccessTo( object.getUri() ) )
                result.add( object );

        return result;
    }

    @Override
    public CollaborationModel getDefaultModel( ChannelsUser user ) {
        for ( ModelDefinition modelDefinition : modelDefinitionManager ) {
            String uri = modelDefinition.getUri();
            if ( user.isDeveloperOrAdmin( uri ) )
                return getDao( uri, true ).getCollaborationModel();
        }

        for ( ModelDefinition modelDefinition : modelDefinitionManager ) {
            String uri = modelDefinition.getUri();
            ModelDao dao = getDao( uri, false );
            if ( dao != null && user.isParticipant( uri ) )
                return dao.getCollaborationModel();
        }

        LOG.warn( "No default collaboration model for user {}", user.getUsername() );
        return null;
    }

    @Override
    public File getVersionDirectory( CollaborationModel collaborationModel ) {
        Version version = getVersion( collaborationModel );
        return version.getVersionDirectory();
    }

    @Override
    public List<String> getModelUris() {
        List<String> uris = new ArrayList<String>();
        for ( CollaborationModel collaborationModel : getDevelopmentModels() ) {
            uris.add( collaborationModel.getUri() );
        }
        return uris;
    }

    public String getServerUrl() {
        return serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollaborationModel> getProductionModels() {
        return (List<CollaborationModel>) CollectionUtils.select(
                getModels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CollaborationModel) object ).isProduction();
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollaborationModel> getDevelopmentModels() {
        return (List<CollaborationModel>) CollectionUtils.select(
                getModels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (CollaborationModel) object ).isDevelopment();
                    }
                }
        );
    }

    public void setServerUrl( String serverUrl ) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void commandDone( Commander commander, Command command, Change change ) {
        if ( !commander.isReplaying() && command.isTop() && !change.isNone() )
            onAfterCommand( commander.getPlanService().getCollaborationModel(), command );
    }

    @Override
    public void commandUndone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void commandRedone( Commander commander, Command command, Change change ) {
        commandDone( commander, command, change );
    }

    @Override
    public void started( Commander commander ) {
    }

    @Override
    public void clearCache() {
        // clearing done via aspect
    }

    /**
     * Callback after a command was executed.
     *
     * @param collaborationModel    the plan
     * @param command the command
     */
    private void onAfterCommand( CollaborationModel collaborationModel, JournalCommand command ) {
        if ( command != null && command.isMemorable() )
            try {
                ModelDao dao = getDao( collaborationModel );
                Exporter exporter = importExportFactory.createExporter( "daemon", dao );
                synchronized ( dao ) {
                    Journal journal = dao.getJournal();
                    if ( command.forcesSnapshot()
                            || journal.size() >= getModelDefinitionManager().getSnapshotThreshold()
                            || collaborationModel.isProduction() )
                        dao.save( exporter );
                    else {
                        journal.addCommand( command );
                        dao.saveJournal( exporter );
                    }
                }

            } catch ( IOException e ) {
                throw new RuntimeException( "Failed to save journal", e );
            }
    }

    /**
     * Listener event management.
     */
    private static final class Listeners {

        /**
         * Whoever cares about plan manager events.
         */
        private final List<ModelListener> modelListeners =
                Collections.synchronizedList( new ArrayList<ModelListener>() );

        private Listeners() {
        }

        public void addListener( ModelListener modelListener ) {
            modelListeners.add( modelListener );
        }

        public void removeListener( ModelListener modelListener ) {
            modelListeners.remove( modelListener );
        }

        public void fireAboutToProductize( CollaborationModel collaborationModel ) {
            synchronized ( modelListeners ) {
                for ( ModelListener modelListener : modelListeners )
                    modelListener.aboutToProductize( collaborationModel );
            }
        }

        public void fireAboutToUnload( ModelDao modelDao ) {
            synchronized ( modelListeners ) {
                for ( ModelListener modelListener : modelListeners )
                    modelListener.aboutToUnload( modelDao );
            }
        }

        public void fireProductized( CollaborationModel collaborationModel ) {
            synchronized ( modelListeners ) {
                for ( ModelListener modelListener : modelListeners )
                    modelListener.productized( collaborationModel );
            }
        }

        public void fireCreated( CollaborationModel collaborationModel ) {
            synchronized ( modelListeners ) {
                for ( ModelListener modelListener : modelListeners )
                    modelListener.created( collaborationModel );
            }
        }

        public void fireLoaded( ModelDao modelDao ) {
            synchronized ( modelListeners ) {
                for ( ModelListener modelListener : modelListeners )
                    modelListener.loaded( modelDao );
            }
        }
    }
}
