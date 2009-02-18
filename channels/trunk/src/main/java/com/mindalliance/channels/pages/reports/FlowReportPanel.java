package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Flow report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 2:06:55 PM
 */
public class FlowReportPanel extends Panel {
    /**
     * A flow
     */
    private Flow flow;
    /**
     * The part from which perspective the flow is reported on
     */
    private Part part;

    public FlowReportPanel( String id, Model<Flow> model, Part part ) {
        super( id, model );
        setRenderBodyOnly( true );
        flow = model.getObject();
        this.part = part;
        init();
    }

    private void init() {
        boolean partIsSource = flow.getSource() == part;

        Label informationLabel = new Label( "information",
                   partIsSource ? flow.getOutcomeTitle() : flow.getRequirementTitle() );
        informationLabel.add( new AttributeModifier( "class", true, new Model<String>(
                flow.isCritical() ? "critical-information" : "information" ) ) );
        add( informationLabel );

        add( new Label( "urgency", flow.getMaxDelay().toString() ) );

        String desc = flow.getDescription();
        Label descLabel = new Label( "description", desc );
        descLabel.setVisible( desc != null && !desc.isEmpty() );
        add( descLabel );

        add( new ChannelsReportPanel( "channels", new Model<Channelable>( flow ) ) );

//        Part otherPart = partIsSource ? (Part) flow.getTarget() : (Part) flow.getSource();
//        List<Actor> actors = Project.service().findAllActors( otherPart.resourceSpec() );
//        ListView<Actor> actorsList = new ListView<Actor>( "actors", actors ) {
//            @Override
//            protected void populateItem( ListItem<Actor> item ) {
//                item.add( new ActorReportPanel( "actor", item.getModel() ) );
//            }
//        };
//        actorsList.add( new AttributeModifier( "class", true, new Model<String>(
//                flow.isAll() ? "all-actors" : "any-actor" ) ) );
//        add( actorsList );

        add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

}
