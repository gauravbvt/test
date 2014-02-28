/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core.dao;

import com.mindalliance.channels.core.command.CommandListener;
import com.mindalliance.channels.core.dao.ModelDefinition.Version;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * The plan manager.
 */
public interface ModelManager extends CommandListener {

    /**
     * Add a listener for plan manager events.
     * @param listener a listener
     */
    void addListener( ModelListener listener );

    /**
     * Remove a listener for plan manager events.
     * @param listener a listener
     */
    void removeListener( ModelListener listener );

    List<TransmissionMedium> getBuiltInMedia();

    String getDefaultSupportCommunity();

    void setDefaultSupportCommunity( String defaultSupportCommunity );

    String getDefaultCommunityCalendar();

    void setDefaultCommunityCalendar( String defaultCommunityCalendar );

    String getDefaultCommunityCalendarHost();

    void setDefaultCommunityCalendarHost( String defaultCommunityCalendarHost );

    String getDefaultCommunityCalendarPrivateTicket();

    void setDefaultCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket );

    /**
     * Get version information from a plan.
     * @param collaborationModel the plan
     * @return the version information
     */
    Version getVersion( CollaborationModel collaborationModel );

    /**
     * Get the dao wrapper for a plan.
     *
     * @param collaborationModel the plan
     * @return the dao wrapper
     */
    ModelDao getDao( CollaborationModel collaborationModel );

    /**
     * Get the dao wrapper for a plan uri. Load plan if necessary.
     *
     * @param uri the plan's uri
     * @param development true for development version; false for production version
     * @return the dao wrapper or null if not found
     */
    ModelDao getDao( String uri, boolean development );

    ModelDao getDao( String uri, int v );

    ModelDefinitionManager getModelDefinitionManager();

    /**
     * Get all loaded plans managed, development and production.
     * @return an unmodifiable list
     */
    List<CollaborationModel> getModels();

    /**
     * Get development plan given uri.
     * @param planUri a string
     * @return a plan
     */
    CollaborationModel getDevelopmentModel( String planUri );

    /**
     * Force a full save with journal flush.
     * @param collaborationModel the plan to flush
     */
    void save( CollaborationModel collaborationModel );

    /**
     * Assign default plans to users.
     */
    void assignModels();

    /**
     * Find the plans, if any, with a given uri.
     *
     * @param uri a string
     * @return a list of plans
     */
    List<CollaborationModel> getModelsWithUri( String uri );

    /**
     * Get a specific plan.
     * @param uri the plan uri
     * @param version the version
     * @return the plan or null if not found
     */
    CollaborationModel getModel( String uri, int version );

    /**
     * Import segment from browsed file.
     *
     *
     * @param userName who is doing the import
     * @param collaborationModel the plan to import into
     * @param inputStream where the segment lies
     * @return a segment, or null if not successful
     */
    Segment importSegment( String userName, CollaborationModel collaborationModel, InputStream inputStream );

    /**
     * Remove all traces of a plan.
     * @param collaborationModel the soon to be ex-model
     */
    void delete( CollaborationModel collaborationModel );

    /**
     * Make a plan in development version and make it a production version.
     * Retire previous production version of the plan if any, and
     * Create a new development version from a copy of the prior development version.
     * Substitute the new development plan as the current plan for each user where applicable.
     * Substitute the new production plan as the current plan for each user where applicable.
     *
     * @param oldDevCollaborationModel   a model
     */
    void productize( CollaborationModel oldDevCollaborationModel );

    /**
     * Find the production version of a model or null.
     *
     * @param uri a string
     * @return a model
     */
    CollaborationModel findProductionModel( String uri );

    /**
     * Find the development version of a model or null..
     *
     * @param uri a string
     * @return a model
     */
    CollaborationModel findDevelopmentModel( String uri );

    /**
     * Add a producer to the plan.
     * Returns whether all developers voted to put into production.
     *
     * @param producer     user name of developer voting to put plan in production
     * @param collaborationModel         a plan
     * @return a boolean
     */
    boolean addProducer( String producer, CollaborationModel collaborationModel );

    /**
     * Check if all producers of a plan want to productize.
     * Productize the plan if so.
     * @param collaborationModel the plan
     * @return true if the plan was productized as a result
     */
    boolean allDevelopersInFavorToPutInProduction( CollaborationModel collaborationModel );

    /**
     * Signal that the plan with given uri was versioned.
     *
     * @param uri the plan's uri
     */
    void setResyncRequired( String uri );

    /**
     * Signal that a user is now in sync with all plans versions.
     *
     * @param userName the user name
     */
    void resynced( String userName );

    /**
     * Whether a user is out of sync with a plan of given uri.
     *
     * @param userName the user name
     * @param uri  a string
     * @return a boolean
     */
    boolean isOutOfSync( String userName, String uri );

    /**
     * Return the list of plans the user has at least developer privileges to.
     *
     * @param user the user of concern
     * @return a list of plans
     */
    List<CollaborationModel> getModelsModifiableBy( ChannelsUser user );

    /**
     * Return the list of plans the user has at least user privileges to.
     *
     * @param user the user
     * @return a list of plans
     */
    List<CollaborationModel> getModelsReadableBy( ChannelsUser user );

    /**
     * Get a plan the uer can edit, else one the user can read, else the default model.
     * Also loads said model.
     * @param user a user
     * @return a plan, or null if none
     */
    CollaborationModel getDefaultModel( ChannelsUser user );

    /**
     * Get the resource directory for a given model.
     * @param collaborationModel the model
     * @return the directory
     */
    File getVersionDirectory( CollaborationModel collaborationModel );

    /**
     * Return list of uris of all plans on Channels instances
     * @return a list of uris
     */
    List<String> getModelUris();

    /**
     * Clear the cache.
     * Via AOP.
     */
    void clearCache();

    /**
     * Get server url.
     * @return a string
     */
    String getServerUrl();

    /**
     * Find all production plans.
     * @return a list of plans
     */
    List<CollaborationModel> getProductionModels();

    /**
     * Get all development plans.
     * @return a list of plans
     */
    List<CollaborationModel> getDevelopmentModels();

 }
