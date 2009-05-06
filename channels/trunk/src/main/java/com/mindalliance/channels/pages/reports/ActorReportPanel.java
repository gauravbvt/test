package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Describe the parts played by an actor in an organization in a scenario.
 */
public class ActorReportPanel extends Panel {

    /** The actor. */
    private Actor actor;

    /** The scenario. */
    private Scenario scenario;

    /** The organization. */
    private Organization organization;

    @SpringBean
    private QueryService queryService;

    public ActorReportPanel(
            String id, Actor actor, Scenario scenario, Organization organization ) {
        super( id );
        setRenderBodyOnly( true );

        this.actor = actor;
        this.organization = organization;
        this.scenario = scenario;

        add( new Label( "sc-name", scenario.getName() ) );                                // NON-NLS
        add( new Label( "title", queryService.getTitle( actor ) ) );                      // NON-NLS
        add( new Label( "org", organization.toString() ) );                               // NON-NLS
        add( new Label( "name", actor.getName() ) );                                      // NON-NLS

        String desc = actor.getDescription();
        Label descLabel = new Label( "description", desc );                               // NON-NLS
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        ResourceSpec spec = new ResourceSpec();
        spec.setActor( actor );
        spec.setOrganization( organization );
        add( new ChannelsBannerPanel( "channels", spec, null, null ) );

        List<Part> parts = getParts();
        add( new ListView<Part>( "parts", parts ) {                                       // NON-NLS
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( new PartReportPanel( "part", item.getModel(), true ) );         // NON-NLS
            }
        } );
    }

    private List<Part> getParts() {
        List<Part> result = new ArrayList<Part>();
        Iterator<Part> partIterator = scenario.parts();
        while ( partIterator.hasNext() ) {
            Part part = partIterator.next();
            if ( queryService.findAllActors( part.resourceSpec() ).contains( actor ) )
                result.add( part );
        }

        return result;
    }
}
