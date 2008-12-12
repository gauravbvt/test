package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

/**
 * A collapsed flow.
 */
public class CollapsedFlowPanel extends Panel {

    public CollapsedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        final Label label = new Label( "title",                                           // NON-NLS
                new PropertyModel( flow,
                                   outcome ? "outcomeTitle" : "requirementTitle" ) );     // NON-NLS

        // Add style mods from scenario analyst.
        final ScenarioAnalyst analyst = ( (Project) getApplication() ).getScenarioAnalyst();
        final String issue = analyst.getIssuesSummary( flow, ScenarioAnalyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !issue.isEmpty() ) {
            label.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            label.add(
                new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }

        add( label );

        // TODO replace expansion links by ajaxfallbacklinks
        add( new ExternalLink( "expand", getRequest().getURL() + "&expand=" + flow.getId() ) );
    }
}
