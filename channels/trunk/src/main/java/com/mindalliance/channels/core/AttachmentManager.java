/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.CollaborationModel;

import java.io.File;
import java.util.List;

// TODO Make interface depend on InputStream instead of File to allow non file-based implementation

/**
 * A thing that keeps track of the associations of model objects and their file attachments.
 */
public interface AttachmentManager {

     public static final String USERS = "users";

    /**
     * Add an attachment to an attachable object.
     *
     * @param attachment the attachment
     * @param attachable the object
     */
    void addAttachment( Attachment attachment, Attachable attachable );

    /**
     * URL points to a document.
     *
     * @param communityService a community service
     * @param url a url
     * @return a boolean
     */
    boolean exists( CommunityService communityService, String url );

    boolean exists( CollaborationModel collaborationModel, String url );

    boolean exists( PlanCommunity planCommunity, String url );


    /**
     * Get display label for an attachment.
     *
     * @param communityService a community service
     * @param attachment an attachment
     * @return a string
     */
    String getLabel( CommunityService communityService, Attachment attachment );

    /**
     * Get all media reference attachments.
     *
     * @param object the attachable object
     * @return a list of attachments
     */
    List<Attachment> getMediaReferences( Attachable object );

    /**
     * Get upload directory.
     *
     * @param communityService a community service
     * @return a string
     */
    File getUploadDirectory( CommunityService communityService );

    /**
     * Get upload directory.
     *
     * @param collaborationModel a plan
     * @return a string
     */
    File getUploadDirectory( CollaborationModel collaborationModel );

    /**
     * Get upload directory.
     *
     * @param planCommunityUri a community uri
     * @return a string
     */
    File getUploadDirectory( String planCommunityUri );


    /**
     * Make full file path name from plan version-relative path.
     *
     * @param communityService a community service
     * @param planRelativePath a string
     * @return a file
     */
    File getUploadedFile( CommunityService communityService, String planRelativePath );

    File getUploadedFile( CollaborationModel collaborationModel, String url );

    /**
     * Whether url points to an image.
     *
     * @param url a string
     * @return a boolean
     */
    boolean hasImageContent( String url );

    /**
     * Whether url points to a video.
     *
     * @param url a string
     * @return a boolean
     */
    boolean hasVideoContent( String url );

    /**
     * Whether the attachment is a reference image.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isImageReference( Attachment attachment );

    /**
     * Whether the attachment is an image or video reference.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isMediaReference( Attachment attachment );

    /**
     * Whether url points to uploaded file.
     *
     * @param url a string
     * @return a boolean
     */
    boolean isUploadedFileDocument( String url );

    /**
     * Whether the attachment is a reference movie.
     *
     * @param attachment the attachment
     * @return a boolean
     */
    boolean isVideoReference( Attachment attachment );

    /**
     * Remove an url attached to a plan.
     *
     * @param communityService a community service
     * @param url the document url
     */
    void remove( CommunityService communityService, String url );

    /**
     * Remove an attachment from an attachable object.
     *
     * @param attachment the attachment to remove
     * @param attachable the object
     */
    void removeAttachment( Attachment attachment, Attachable attachable );

    /**
     * Upload a file and get an attachment to a model object.
     *
     * @param communityService a community service
     * @param upload what to upload
     * @return an attachment or null if failed
     */
    Attachment upload( CommunityService communityService, Upload upload );

}
