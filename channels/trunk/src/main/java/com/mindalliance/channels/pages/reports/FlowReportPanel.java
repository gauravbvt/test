package com.mindalliance.channels.pages.reports;

import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.behavior.IBehavior;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;

import java.util.List;

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
        flow = model.getObject();
        this.part = part;
        init();
    }

    private void init() {
        boolean partIsSource = flow.getSource() == part;
        WebMarkupContainer flowDiv = new WebMarkupContainer( "flow" );
        add( flowDiv );
        String styleClass = partIsSource ? "send" : "receive";
        styleClass = styleClass + "-" + ( flow.isAskedFor() ? "answer" : "notification" );
        flowDiv.add( new AttributeModifier( "class", true, new Model<String>( styleClass ) ) );
        Label informationLabel = new Label( "information", flow.getName() );
        flowDiv.add( informationLabel );
        String infoStyle = flow.isCritical() ? "critical-information" : "information";
        informationLabel.add( new AttributeModifier( "class", true, new Model<String>( infoStyle ) ) );
        Part otherPart = partIsSource ? (Part) flow.getTarget() : (Part) flow.getSource();
        ResourceSpec resource = otherPart.resourceSpec();
        flowDiv.add( new Label( "resource", resource.toString() ) );
        flowDiv.add( new ChannelsReportPanel( "channels", new Model<Channelable>( flow ) ) );
        String actorsStyle = flow.isAll() ? "all-actors" : "any-actor";
        List<Actor> actors = Project.service().findAllActors( resource );
        ListView<Actor> actorsList = new ListView<Actor>( "actors", actors ) {
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );
            }
        };
        flowDiv.add( actorsList );
        actorsList.add( new AttributeModifier( "class", true, new Model<String>( actorsStyle ) ) );
        flowDiv.add( new IssuesReportPanel( "issues", new Model<ModelObject>( flow ) ) );
    }

}
