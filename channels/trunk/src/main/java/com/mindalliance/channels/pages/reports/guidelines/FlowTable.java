// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports.guidelines;

import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage.AggregatedFlow;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage.ContactSpec;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * ...
 */
public class FlowTable extends Panel {

    public FlowTable( String id, List<AggregatedFlow> flows ) {
        super( id );

        add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        final AggregatedFlow flow = item.getModelObject();
                        item.add(
                                new Label( "flowName2", flow.getFormattedLabel() ),
                                new ListView<ContactSpec>( "flowTargets", flow.getSpecList() ) {
                                    @Override
                                    protected void populateItem( ListItem<ContactSpec> specItem ) {
                                        ContactSpec spec = specItem.getModelObject();
                                        int size = getViewSize();
                                        int index = specItem.getIndex();
                                        String label = ( flow.isAll() ? "every " : "any " )
                                                + spec.toString();
                                        specItem.add(
                                                new Label( "flowTarget", label )
                                                        .add( new AttributeModifier( "href", true,
                                                                new Model<String>( '#' + spec.getLink() ) ) ),
                                                new Label( "flowSep", index == 0 ? "" :
                                                        size > 1 && index == size - 1 ? " or "
                                                                : ", " )
                                                        .setRenderBodyOnly( true )
                                        );
                                        specItem.setRenderBodyOnly( true );
                                    }
                                }.setRenderBodyOnly( true ),
                                new Label( "flowTiming", ChannelsUtils.lcFirst( flow.getTiming() ) )
                                        .setVisible( !flow.getTiming().isEmpty() ),
                                new WebMarkupContainer( "critical" )
                                        .setVisible( flow.isCritical() ),
                                new UserFeedbackPanel( "outgoingFlowFeedback",
                                        flow.getBasis(),
                                        "Send feedback",
                                        Feedback.GUIDELINES ),
                                new WebMarkupContainer( "eoisRow" )
                                        .add( GuidelinesPage.newEoiList( flow.getElementsOfInformation() ) )
                                        .setRenderBodyOnly( true )
                                        .setVisible( flow.hasEois() ),
                                new WebMarkupContainer( "flowEnding" )
                                        .setVisible( flow.isTerminatingToSource() ),

                                instructions( flow ) );                                
                    }
                }.setRenderBodyOnly( true )
        );
        setRenderBodyOnly( true );

    }

    private WebMarkupContainer instructions( AggregatedFlow flow ) {
        WebMarkupContainer instructions = new WebMarkupContainer( "instructions" );
        instructions.setVisible( hasInstructions( flow ) );
        // no context?
        Label noContext = new Label( "noContext", flow.getReferenceContextInstructions() );
        noContext.setVisible( flow.isCantReferenceEventPhase() );
        instructions.add( noContext );
        // Confirm receipt?
        Label confirmReceipt = new Label( "confirmReceipt", flow.getConfirmReceiptInstructions() );
        confirmReceipt.setVisible( flow.isReceiptConfirmationRequested() );
        instructions.add(  confirmReceipt );
        // can bypass intermediate?
        Label canBypass = new Label( "bypass", flow.getBypassInstructions() );
        canBypass.setVisible( flow.isCanBypassIntermediate() );
        instructions.add(  canBypass );
        // other instructions?
        Label otherInstructions = new Label( "other", flow.getOtherInstructions() );
        otherInstructions.setVisible( !flow.getOtherInstructions().isEmpty() );
        instructions.add(  otherInstructions );
        return instructions;
    }

    private boolean hasInstructions( AggregatedFlow flow ) {
        return flow.isCantReferenceEventPhase()
                || flow.isReceiptConfirmationRequested()
                || flow.isCanBypassIntermediate()
                || !flow.getOtherInstructions().isEmpty();
    }


}
