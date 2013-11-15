package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.community.CommunityService;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;

/**
 * User file upload service.
 * Note: User is modified but not saved.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/18/12
 * Time: 9:09 AM
 */
public interface UserUploadService {

    static final String SQUARED = "_squared";

    static final String ICON = "_icon";

    // Photo must be null for upload to happen.
    boolean uploadUserPhoto( ChannelsUser user, FileUpload upload, CommunityService communityService );

    String getSquareUserIconURL( ChannelsUser user );

    File findSquaredUserPhoto( String photoName );

    // Remove all unreferenced photos
   void cleanUpPhotos();
}
