package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.engine.command.Change;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Message panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2010
 * Time: 11:40:48 AM
 */
public class MessagePanel extends AbstractCommandablePanel {
    private IModel<String> messageModel;

    public MessagePanel( String id, IModel<String> messageModel ) {
        super( id );
        this.messageModel = messageModel;
        init();
    }

    private void init() {
        addClose();
        addMessage();
    }

    private void addMessage() {
        Label messageLabel = new Label( "message", messageModel );
        add( messageLabel );
    }

    private void addClose() {
        AjaxFallbackLink closeLink = new AjaxFallbackLink( "close" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.None );
                change.setMessage( "" );
                update( target, change );
            }
        };
        add( closeLink );
    }


}
