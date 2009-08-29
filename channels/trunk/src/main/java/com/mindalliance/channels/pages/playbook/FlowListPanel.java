package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Actor;
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

    FlowListPanel(
            String id, final Collection<ResourceSpec> actorSpecs, final ResourceSpec exception,
            final List<SynonymFlowSet> synsets ) {

        super( id );
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
                if ( flowCell.hasFlow() )
                    item.add( new WebMarkupContainer( "vcard" )
                                .add( new Label( "delay", "" ),
                                      new Label( "criticality", "" ) )
                                .setRenderBodyOnly( true ) );
                else {
                    Actor actor = flowCell.getActor();
                    long value = actor == null ? 0L : actor.getId();
                    item.add( new BookmarkablePageLink<VCardPage>( "vcard", VCardPage.class )
                                .setParameter( "0", value )
                                .add( new Label( "delay", flowCell.getDelayString() ),
                                      new Label( "criticality", flowCell.getCriticality() ) )
                                .add( new AttributeModifier( "title", true,
                                        new Model<String>( flowCell.getDescription() ) ) ) );
                }
            }
        };
    }

    private List<FlowCell> getFlowCells(
            SynonymFlowSet set, Collection<ResourceSpec> actorSpecs, ResourceSpec exception ) {
        List<FlowCell> flowCells = new ArrayList<FlowCell>();
        Map<ResourceSpec,Flow> map = set.getProjection( queryService, exception );
        for ( ResourceSpec actorSpec : actorSpecs )
            flowCells.add( new FlowCell( actorSpec.getActor(), map.get( actorSpec ) ) );
        return flowCells;
    }
}
