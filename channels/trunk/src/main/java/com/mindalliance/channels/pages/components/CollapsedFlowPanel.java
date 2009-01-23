package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.MessageFormat;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends Panel implements DeletableFlow {

    /** True when flow should be deleted. */
    private boolean markedForDeletion;

    /** The underlying flow. */
    private Flow flow;

    public CollapsedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        this.flow = flow;

        final Label label = new Label( "title",                                           // NON-NLS
                new PropertyModel( flow,
                                   outcome ? "outcomeTitle" : "requirementTitle" ) );     // NON-NLS

        final String c = flow.getChannel();
        final Label channel = new Label( "channel", new AbstractReadOnlyModel() {         // NON-NLS
            @Override
            public Object getObject() {
                return c != null && c.isEmpty() ? ""
                                   : MessageFormat.format( "({0})", c );
            }
        } );
        channel.setVisible( c != null && !c.isEmpty() );
        add( channel );

        // Add style mods from scenario analyst.
        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        final String issue = analyst.getIssuesSummary(
                flow, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !issue.isEmpty() ) {
            label.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            label.add(
                new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }

        add( label );

        // TODO replace expansion links by ajaxfallbacklinks
        add( new ExternalLink( "expand", getRequest().getURL() + "&expand=" + flow.getId() ) );

        add( new CheckBox( "delete",                                                      // NON-NLS
                           new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );   // NON-NLS
    }

    /** {@inheritDoc} */
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /** {@inheritDoc} */
    public void setMarkedForDeletion( boolean delete ) {
        markedForDeletion = delete;
    }

    public Flow getFlow() {
        return flow;
    }
}
