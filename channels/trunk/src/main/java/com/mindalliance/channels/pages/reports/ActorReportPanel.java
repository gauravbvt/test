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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

import java.util.List;

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
        String description = spec.getDescription();
        List<Part> parts = queryService.findAllParts( scenario, spec );

        add( new Label( "title", getActorTitle( spec ) ),
             new Label( "name", spec.getReportTitle() ),                                  // NON-NLS

             new Label( "description", description ).setVisible( !description.isEmpty() ),

             new ChannelsBannerPanel( "channels", spec, null, null ),

             new ListView<Part>( "parts", parts ) {
                    @Override
                    protected void populateItem( ListItem<Part> item ) {
                        item.add( new PartReportPanel( "part",                            // NON-NLS
                                                       item.getModel(), true, showingIssues ) );
                        item.add( new AttributeModifier( "class", true,
                            new Model<String>( item.getIndex() % 2 == 0 ?
                                               "task even" : "task odd" ) ) );
                    }
                } );
    }

    private String getActorTitle( ResourceSpec spec ) {
        Actor actor = spec.getActor();
        String actorTitle;
        if ( actor == null || Actor.UNKNOWN.equals( actor ) )
            actorTitle = "";
        else {
            String t = queryService.getTitle( actor );
            if ( t.isEmpty() )
                actorTitle = "";
            else
                actorTitle = t + ", ";
        }

        String s = spec.toString();
        int as = s.indexOf( " as " );
        String s1 = as >= 0 ? s.substring( as + 4 ) : s ;
        return actorTitle + s1;
    }
}
