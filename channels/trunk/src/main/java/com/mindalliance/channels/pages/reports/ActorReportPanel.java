package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Describe the parts played by an actor in an organization in a scenario.
 */
public class ActorReportPanel extends Panel {

    @SpringBean
    private QueryService queryService;

    public ActorReportPanel( String id, Scenario scenario, ResourceSpec spec ) {

        super( id );
        setRenderBodyOnly( true );

        add( new Label( "sc-name", scenario.getName() ) );                                // NON-NLS

        Actor actor = spec.getActor();
        Label title = new Label(
                "title", actor == null ? "" : queryService.getTitle( actor ) );
        title.setVisible( actor != null && !Actor.UNKNOWN.equals( actor ) );
        add( title );

        add( new Label( "org", spec.getOrganizationName() ) );                            // NON-NLS
        add( new Label( "name", spec.getReportTitle() ) );                                // NON-NLS

        String desc = spec.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new ChannelsBannerPanel( "channels", spec, null, null ) );
        add( new ListView<Part>( "parts", queryService.findAllParts( scenario, spec ) ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel( "part", item.getModel(), true ) );         // NON-NLS
            }
        } );
    }
}
