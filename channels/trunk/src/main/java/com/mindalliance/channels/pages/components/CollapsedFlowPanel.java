package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends Panel {

    public CollapsedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        
        add( new Label( "title", new PropertyModel( flow,                                 // NON-NLS
                            outcome ? "outcomeTitle" : "requirementTitle" ) ) );          // NON-NLS

        // TODO replace expansion links by ajaxfallbacklinks
        add( new ExternalLink( "expand", getRequest().getURL()+"&expand=" + flow.getId() ));
    }
}
