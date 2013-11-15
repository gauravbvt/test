package com.mindalliance.channels.pages.components;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Channels modal window.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/30/13
 * Time: 9:45 AM
 */
public class ChannelsModalWindow extends ModalWindow {

    public ChannelsModalWindow( String id ) {
        super( id );
        setOutputMarkupId( true );
    }

    public ChannelsModalWindow( String id, IModel<?> model ) {
        super( id, model );
        setOutputMarkupId( true );
    }

    @Override
    protected ResourceReference newCssResource() {
        return null;
    }

}
