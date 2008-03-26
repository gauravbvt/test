package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * ...
 */
public class ResourcesPanel extends Panel {

    public ResourcesPanel( String id ) {
        super( id );

        PlaybookSession s = (PlaybookSession) getSession();
        Ref project = s.getProject();

        add( new ContentPanel( "contents", new RefDataProvider( project, "resources" ) ) );
    }
}
