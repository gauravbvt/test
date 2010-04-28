package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Describe the parts played by an actor in an organization in a segment.
 */
public class ActorReportPanel extends Panel {

    @SpringBean
    private QueryService queryService;

    public ActorReportPanel(
            String id, Segment segment, final ResourceSpec spec, final boolean showingIssues ) {

        super( id );
        setRenderBodyOnly( true );
        add( new VCardPanel( "vcard", spec, "../" ),

             new ListView<Part>(
                     "parts",
                     getParts( segment, spec ) ) {
                    @Override
                    protected void populateItem( ListItem<Part> item ) {
                        item.add( new PartReportPanel(
                                "part",
                                item.getModel(),
                                spec.getOrganization(),
                                showingIssues ) );
                        item.add( new AttributeModifier( "class", true,
                            new Model<String>( item.getIndex() % 2 == 0 ?
                                               "task even" : "task odd" ) ) );
                    }
                } );
    }

    private List<Part> getParts( Segment segment, ResourceSpec spec ) {
        if (spec.getActor() != null ) {
            return queryService.findAllAssignedParts( segment, spec.getActor() );
        } else {
            return queryService.findAllParts( segment, spec, false );
        }
    }
}
