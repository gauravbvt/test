package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

/**
 * Abstract social list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 22, 2010
 * Time: 5:00:18 PM
 */
public class AbstractSocialListPanel extends AbstractUpdatablePanel {
    private final boolean collapsible;

    public AbstractSocialListPanel( String id, boolean collapsible ) {
        super( id );
        this.collapsible = collapsible;
    }

    protected void init() {
        addHideSocial();
    }

    private void addHideSocial() {
        AjaxFallbackLink hideSocialLink = new AjaxFallbackLink( "hideAll" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Channels.SOCIAL_ID );
                change.setMessage( "To re-open, select Planners in the top Show menu." );
                update( target, change );
            }
        };
        addTipTitle( hideSocialLink, "Hide this panel" );
        add( hideSocialLink );
        hideSocialLink.setVisible( collapsible );
    }

}
