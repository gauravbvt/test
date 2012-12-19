package com.mindalliance.channels.core.dao.user;

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
    // Photo must be null for upload to happen.
    boolean uploadUserPhoto( ChannelsUser user, FileUpload upload );

    String getSquareUserIconURL( ChannelsUser user );

    File findSquaredUserPhoto( String fileName );

   // boolean removeUserPhoto( ChannelsUser user );
}
