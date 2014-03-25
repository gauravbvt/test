package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.db.services.communities.UserParticipationConfirmationService;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.CollaborationPlanAnalyst;
import com.mindalliance.channels.engine.analysis.Doctor;

import java.util.Date;
import java.util.List;

/**
 * Community service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/12
 * Time: 8:56 AM
 */
public interface CommunityService {

    UserParticipationService getUserParticipationService();

    UserParticipationConfirmationService getUserParticipationConfirmationService();

    OrganizationParticipationService getOrganizationParticipationService();

    FeedbackService getFeedbackService();

    ModelService getModelService();

    void setModelService( ModelService modelService );

    Analyst getAnalyst();

    AbstractModelObjectDao getDao();

    void setPlanCommunity( PlanCommunity planCommunity );

    <T extends ModelObject> T find( Class<T> clazz, long id, Date dateOfRecord ) throws NotFoundException;

    boolean exists( Class<? extends ModelObject> clazz, Long id, Date dateOfRecord );

    boolean canHaveParentAgency( final String name, String parentName );

    List<Agency> findAncestors( String agencyName );

    boolean isCustodianOf( ChannelsUser user, Organization placeholder );

    CommunityCommitments getAllCommitments( Boolean includeToSelf );

    CommunityCommitments findAllCommitments( Flow flow, Boolean includeToSelf );

    CommunityAssignments getAllAssignments();

    CommunityCommitments findAllBypassCommitments( final Flow flow );

    void clearCache();

    void onDestroy();

    PlanCommunity getPlanCommunity();

    CollaborationModel getPlan();

    ParticipationManager getParticipationManager();

    UserRecordService getUserRecordService();

    RegisteredOrganizationService getRegisteredOrganizationService();

    CollaborationPlanAnalyst getCollaborationPlanAnalyst();

    UserUploadService getUserUploadService();

    Boolean isCommunityPlanner( ChannelsUser user );

    List<ChannelsUser> getCommunityPlanners( );

    List<String> getCommunityPlannerUsernames();


    /**
     * Find all user issues about a model object
     *
     * @param identifiable an object with an id
     * @return list of issues
     */
    List<UserIssue> findAllUserIssues( Identifiable identifiable );

    <T extends Identifiable> List<T> listKnownIdentifiables( Class<T> clazz );

    <T extends ModelObject> List<T> list( Class<T> clazz );

    <T extends ModelEntity> List<T> listActualEntities( Class<T> clazz, boolean mustBeReferenced );

    <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name );

    <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException;

    <T extends ModelEntity> T findOrCreate( Class<T> clazz, String name, long id );

    <T extends ModelEntity> T findOrCreateType( Class<T> clazz, String name, long id );


    void update( ModelObject mo );

    /**
     * Find an actual entity by given name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name );

    /**
     * Find an actual entity by given name. If none, create it for given domain,
     * renaming it to avoid conflicts if needed.
     * If id is not null, assign the entity the given id if created.
     *
     * @param clazz the kind of model object
     * @param name  the name
     * @param id    an id
     * @param <T>   a subclass of model object
     * @return the object or null if name is null or empty
     */
    <T extends ModelEntity> T safeFindOrCreate( Class<T> clazz, String name, Long id );

    /**
     * List all user issues about a given model object.
     * @param modelObject a model object
     * @return a list of issues.
     */
    List<Issue> listUserIssues( ModelObject modelObject );

    /**
     * Persist a model object at a given id.
     * @param modelObject a model object
     * @param id an id
     */
    void add( ModelObject modelObject, Long id );

    /**
     * Find an entity type by name, if it exists.
     *
     * @param entityClass a model entity class
     * @param name        a string
     * @param <T>         a subclass of model entity
     * @return a model entity or null
     */
    <T extends ModelEntity> T findEntityType( Class<T> entityClass, String name );

    /**
     * Find an actual entity by name, if it exists.
     *
     * @param entityClass a model entity class
     * @param name        a string
     * @param <T>         a subclass of model entity
     * @return a model entity or null
     */
    <T extends ModelEntity> T findActualEntity( Class<T> entityClass, String name );

    /**
     * Whether the community service is for a domain community.
     * @return a boolean
     */
    boolean isForDomain();

    /**
     * Remove a persistent model object.
     *
     * @param object the object
     */
    void remove( ModelObject object );

    /**
     * Find all placeholder places not bound by the plan community.
     * @return a list of places
     */
    List<Place> findUnboundLocationPlaceholders();

    /**
     * resolve a place to itself or its bound location if bound.
     * @param place a place
     * @return the resolved place
     */
    Place resolveLocation( Place place );

    /**
     * Get the appropriate doctor.
     * @return a doctor
     */
    Doctor getDoctor();

    /**
     * Remove obsolete data.
     */
    void cleanUp();

    /**
     * Find all media for which contact info is required from the user to fulfill participation requirements.
     * @param user a Channels user
     * @return a list of transmission media
     */
    List<TransmissionMedium> findMissingContactInfoMedia( ChannelsUser user );

    /**
     * Make plan community url.
     * @return a string
     */
    String makePlanCommunityUrl();

    /**
     * Make plan community participation url.
     * @return a string
     */
    String makePlanCommunityParticipationUrl();

 }
