package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 9:51:11 AM
 */
public class IssuesReportPanel extends Panel {
    /**
     *  The model object with issues
     */
    private ModelObject modelObject;

    public IssuesReportPanel( String id, IModel<ModelObject> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        modelObject = model.getObject();
        init( Channels.analyst() );
    }

    private void init( Analyst analyst ) {
        List<Issue> issues = analyst.listUnwaivedIssues( modelObject, true );
        add( new ListView<Issue>( "issues", issues ) {
            @Override
            protected void populateItem( ListItem<Issue> item ) {
                Issue issue = item.getModelObject();
                item.add( new Label( "reported-by", issue.getReportedBy() ) );
                item.add( new Label( "description", issue.getDescription() ) );
                item.add( new Label( "suggestion", issue.getRemediation() ) );
                String styleClass = issue.getSeverity().toString().toLowerCase();
                item.add( new AttributeModifier( "class", true,
                                                     new Model<String>( styleClass ) ) );
            }
        } );

        if ( issues.isEmpty() )
            setVisible( false );
    }
}
