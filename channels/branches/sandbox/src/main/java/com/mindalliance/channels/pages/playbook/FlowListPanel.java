package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A cutesy list of flows.
 */
class FlowListPanel extends AbstractUpdatablePanel {


    /** True if the flows in this panel are incoming. */
    private boolean incoming;

    FlowListPanel(
            String id, final Collection<ResourceSpec> actorSpecs, final Specable exception,
            final List<SynonymFlowSet> synsets, boolean incoming, final int offset ) {

        super( id );
        this.incoming = incoming;
        add(
            new ListView<SynonymFlowSet>( "set", synsets ) {
                @Override
                protected void populateItem( ListItem<SynonymFlowSet> item ) {
                    SynonymFlowSet set = item.getModelObject();
                    item.add( new Label( "set-name", set.getLabel() ).setRenderBodyOnly( true ),
                              new AttachmentListPanel( "attachments", set.getAttachments() ),
                              new WebMarkupContainer( "direction" )
                                .add( new Label( "dir-text", set.isIncoming() ? "<--" : "-->" ) )
                                .add( new AttributeModifier( "class", true,
                                        new Model<String>( set.isIncoming() ? "in" : "out" ) ) ),
                              createFlowCells( getFlowCells( set, actorSpecs, exception ) ) );

                    int index = offset + synsets.indexOf( set );
                    item.add( new AttributeModifier( "class", true,
                        new Model<String>( index % 2 == 0 ? "even" : "odd" ) ) );
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
                     || !flowCell.isIncoming() && flowCell.isAskedFor()
                     || !flowCell.hasActor() && !flowCell.hasFlow() )
                {
                    container = new WebMarkupContainer( "vcard" );
                    container.setRenderBodyOnly( true );
                } else
                    container = flowCell.hasActor() ?
                                new BookmarkablePageLink<VCardPage>( "vcard", VCardPage.class )
                                    .setParameter( "0", flowCell.getActorId() )
                                    .setParameter( "1", flowCell.getFlowId() )
                              : new BookmarkablePageLink<ContactPage>( "vcard", ContactPage.class )
                                    .setParameter( "0", flowCell.getFlowId() );

                item.add( container
                            .add( new Label( "delay", flowCell.getDelayString() ),
                                  new Label( "criticality", flowCell.getCriticality() ),
                                  new WebMarkupContainer( "ask" )
                                          .setVisible( flowCell.isAskedFor() ) )
                            .add( new AttributeModifier( "title", true,
                                    new Model<String>( flowCell.getDescription() ) ) ) );
            }
        };
    }

    private List<FlowCell> getFlowCells(
            SynonymFlowSet set, Collection<ResourceSpec> actorSpecs, Specable exception ) {
        List<FlowCell> flowCells = new ArrayList<FlowCell>();
        Map<ResourceSpec,Flow> map = set.getProjection( getQueryService(), exception );
        for ( Specable actorSpec : actorSpecs )
            flowCells.add( new FlowCell( actorSpec.getActor(), map.get( actorSpec ), incoming ) );
        return flowCells;
    }
}
