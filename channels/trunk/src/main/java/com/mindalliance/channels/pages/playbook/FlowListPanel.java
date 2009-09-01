package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ResourceSpec;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A cutesy list of flows.
 */
class FlowListPanel extends Panel {

    /** The query service, for spec-actor resolution. */
    @SpringBean
    private QueryService queryService;

    /** True if the flows in this panel are incoming. */
    private boolean incoming;

    FlowListPanel(
            String id, final Collection<ResourceSpec> actorSpecs, final ResourceSpec exception,
            final List<SynonymFlowSet> synsets, boolean incoming ) {

        super( id );
        this.incoming = incoming;
        add(
            new ListView<SynonymFlowSet>( "set", synsets ) {
                @Override
                protected void populateItem( ListItem<SynonymFlowSet> item ) {
                    SynonymFlowSet set = item.getModelObject();
                    item.add( new Label( "set-name", set.getLabel() ).setRenderBodyOnly( true ),
                              new AttachmentListPanel( "attachments", set.getAttachments() ),
                              new Label( "direction", set.isIncoming() ? "<--" : "-->" ),
                              createFlowCells( getFlowCells( set, actorSpecs, exception ) ) );
                }
            } );

        setVisible( !synsets.isEmpty() );
        setRenderBodyOnly( true );
    }

    private static ListView<FlowCell> createFlowCells( List<FlowCell> flowCells ) {
        return new ListView<FlowCell>( "flow", flowCells ) {
            @Override
            protected void populateItem( ListItem<FlowCell> item ) {
                FlowCell flowCell = item.getModelObject();

                WebMarkupContainer container;
                if ( flowCell.isIncoming() && !flowCell.isAskedFor() 
                     || !flowCell.hasActor() && !flowCell.hasFlow() ) {
                    container = new WebMarkupContainer( "vcard" );
                    container.setRenderBodyOnly( true );
                } else
                    container = flowCell.hasActor() ?
                                new BookmarkablePageLink<VCardPage>( "vcard", VCardPage.class )
                                    .setParameter( "0", flowCell.getActorId() )
                              : new BookmarkablePageLink<ContactPage>( "vcard", ContactPage.class )
                                    .setParameter( "0", flowCell.getFlowId() );

                item.add( container
                            .add( new Label( "delay", flowCell.getDelayString() ),
                                  new Label( "criticality", flowCell.getCriticality() ) )
                            .add( new AttributeModifier( "title", true,
                                    new Model<String>( flowCell.getDescription() ) ) ) );
            }
        };
    }

    private List<FlowCell> getFlowCells(
            SynonymFlowSet set, Collection<ResourceSpec> actorSpecs, ResourceSpec exception ) {
        List<FlowCell> flowCells = new ArrayList<FlowCell>();
        Map<ResourceSpec,Flow> map = set.getProjection( queryService, exception );
        for ( ResourceSpec actorSpec : actorSpecs )
            flowCells.add( new FlowCell( actorSpec.getActor(), map.get( actorSpec ), incoming ) );
        return flowCells;
    }
}
