package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Place;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.MessageFormat;

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
     * A part.
     */
    private Part part;

    public PartReportPanel( String id, IModel<Part> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        part = model.getObject();
        init();
    }

    private void init() {
        add( new Label( "task", part.getTask() ) );
        add( new Label( "description", part.getDescription() ) );
        Place location = part.getLocation();
        add( new Label( "location", location != null ? location.toString() : "Unspecified" ) );
        Label completionTimeLabel;
        if ( part.isSelfTerminating() ) {
            completionTimeLabel = new Label( "completion-time", part.getCompletionTime().toString() );
        } else {
            completionTimeLabel = new Label( "completion-time", "" );
            completionTimeLabel.setVisible( false );
        }
        add( completionTimeLabel );
        Label repeatsEveryLabel;
        if ( part.isRepeating() ) {
            repeatsEveryLabel = new Label( "repeats-every", part.getRepeatsEvery().toString() );
        } else {
            repeatsEveryLabel = new Label( "repeats-every", "" );
            repeatsEveryLabel.setVisible( false );
        }
        add( repeatsEveryLabel );
        Iterator<Flow> outcomes = part.outcomes();
        List<Flow> sends = new ArrayList<Flow>();
        while ( outcomes.hasNext() ) {
            Flow outcome = outcomes.next();
            if ( !outcome.getTarget().isConnector() ) {
                sends.add( outcome );
            }
        }
        add( new ListView<Flow>( "sends", sends ) {
            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(
                        MessageFormat.format( "{0}-{1}",
                                "send",
                                flow.isAskedFor() ? "answer" : "notification" ) ) ) );

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
            @Override
            protected void populateItem( ListItem<Flow> item ) {
                Flow flow = item.getModelObject();
                item.add( new AttributeModifier( "class", true, new Model<String>(
                        MessageFormat.format( "{0}-{1}",
                                "receive",
                                flow.isAskedFor() ? "answer" : "notification" ) ) ) );
                item.add( new FlowReportPanel( "receive", new Model<Flow>( flow ), part ) );
            }
        } );
        add( new IssuesReportPanel( "issues", new Model<ModelObject>( part ) ) );
    }


}
