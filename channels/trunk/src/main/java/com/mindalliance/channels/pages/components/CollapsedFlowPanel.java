package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Flow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends Panel {

    /** The flow displayed by this panel. */
    private Flow flow;

    public CollapsedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        this.flow = flow;
        setDefaultModel( new CompoundPropertyModel<Flow>( flow ) );

        add( new Label( "title", new PropertyModel( flow,                                 // NON-NLS
                            outcome ? "outcomeTitle" : "requirementTitle" ) ) );          // NON-NLS

        // TODO replace expansion links by ajaxfallbacklinks
        add( new ExternalLink( "expand", getRequest().getURL()+"&expand=" + flow.getId() ));
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow( Flow flow ) {
        this.flow = flow;
    }
}
