package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachable;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Attachment.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Attachable implementation.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 8, 2010
 * Time: 9:54:48 AM
 */
public class AbstractAttachable implements Attachable {

    /**
     * List of attachments.
     */
    private List<Attachment> attachments = new ArrayList<Attachment>();

    @Override
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments( List<Attachment> attachments ) {
        this.attachments = new ArrayList<Attachment>( attachments );
    }

    @Override
    public void addAttachment( Attachment attachment ) {
        if ( !attachments.contains( attachment ) )
            attachments.add( attachment );
    }

    @Override
    public List<Type> getAttachmentTypes(  ) {
        List<Type> types = new ArrayList<Type>();
        types.add( Type.Reference );
        types.add( Type.Policy );
        return types;
    }

    @Override
    public void removeAttachment( Attachment attachment ) {
        attachments.remove( attachment );
    }

    @Override
    public List<Type> getAttachmentTypes( String attachablePath ) {
        return getAttachmentTypes();
    }

    
}
