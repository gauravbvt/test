package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.pages.Channels;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Reference to an uploaded resource for the current plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/6/12
 * Time: 11:44 AM
 */
public class UploadedReference extends ResourceReference {

    public UploadedReference(  ) {
        super(UploadedResource.class, "UploadedResource");
    }



    @Override
    public IResource getResource() {
            try {
                return new UploadedResource( getUploadsDirectoryPath() );
            } catch (Exception e){
                throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
            }
    }

    private String getUploadsDirectoryPath() {
        AttachmentManager attachmentManager = ( (Channels) Channels.get() ).getAttachmentManager();
        File directory = ChannelsUser.current().getPlanCommunityUri() != null
                ? attachmentManager.getUploadDirectory( ChannelsUser.current().getPlanCommunityUri() )
                : attachmentManager.getUploadDirectory( ChannelsUser.current().getCollaborationModel() );
        return directory.getAbsolutePath();
    }

}
