package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
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
        add( new VCardPanel( "vcard", spec, "../" ),

             new ListView<Part>( "parts", queryService.findAllParts( scenario, spec ) ) {
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
}
