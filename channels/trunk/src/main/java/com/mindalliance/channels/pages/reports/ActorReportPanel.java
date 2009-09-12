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

    public ActorReportPanel(
            String id, Scenario scenario, ResourceSpec spec, final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );

        Actor actor = spec.getActor();
        String actorTitle = actor == null ? "" : queryService.getTitle( actor );
        String description = spec.getDescription();

        add( new Label( "sc-name", scenario.getName() ),                                  // NON-NLS
             new Label( "title", actorTitle )
                     .setVisible( !( actor == null || Actor.UNKNOWN.equals( actor ) ) ),

             new Label( "org", spec.getOrganizationName() ),                              // NON-NLS
             new Label( "name", spec.getReportTitle() ),                                  // NON-NLS

             new Label( "description", description ).setVisible( !description.isEmpty() ),

             new ChannelsBannerPanel( "channels", spec, null, null ),

             new ListView<Part>( "parts", queryService.findAllParts( scenario, spec ) ) {
                    @Override
                    protected void populateItem( ListItem<Part> item ) {
                        item.add( new PartReportPanel( "part",                            // NON-NLS
                                                       item.getModel(), true, showingIssues ) );
                    }
                } );
    }
}
