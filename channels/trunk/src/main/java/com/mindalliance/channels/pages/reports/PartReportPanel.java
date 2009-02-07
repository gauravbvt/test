package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Part report panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 9:35:15 PM
 */
public class PartReportPanel extends Panel {
    /**
     * A part
     */
    private Part part;

    public PartReportPanel( String id, IModel<Part> model ) {
        super( id, model );
        part = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "task", part.getTask() ) );
        add( new Label( "description", part.getDescription() ) );
        Place location = part.getLocation();
        add( new Label( "location", location != null ? location.toString() : "Unspecified" ) );
        ResourceSpec resourceSpec = part.resourceSpec();
        add( new Label( "resource", resourceSpec.toString() ) );
        List<Actor> actors = Project.service().findAllActors( resourceSpec );
        add( new ListView<Actor>( "actors", actors ) {
            protected void populateItem( ListItem<Actor> item ) {
                item.add( new ActorReportPanel( "actor", item.getModel() ) );
            }
        } );
        Iterator<Flow> outcomes = part.outcomes();
        List<Flow> sends = new ArrayList<Flow>();
        while ( outcomes.hasNext() ) {
            Flow outcome = outcomes.next();
            if ( !outcome.getTarget().isConnector() ) {
                sends.add( outcome );
            }
        }
        add( new ListView<Flow>( "sends", sends ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                item.add( new FlowReportPanel( "send", new Model<Flow>( flow ), part ) );
            }
        } );
        Iterator<Flow> requirements = part.requirements();
        List<Flow> receives = new ArrayList<Flow>();
        while ( requirements.hasNext() ) {
            Flow requirement = requirements.next();
            if ( !requirement.getSource().isConnector() ) {
                receives.add( requirement );
            }
        }
        add( new ListView<Flow>( "receives", receives ) {
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                item.add( new FlowReportPanel( "receive", new Model<Flow>( flow ), part ) );
            }
        } );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( part ) ) );
    }


}
