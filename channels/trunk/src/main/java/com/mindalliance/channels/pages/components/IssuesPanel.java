package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.Transformer;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.analysis.ScenarioAnalyst;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 10:34:20 AM
 */
public class IssuesPanel extends Panel {

    private ModelObject modelObject;

    public IssuesPanel( String s, IModel<ModelObject> iModel ) {
        super( s, iModel );
        modelObject = iModel.getObject();
        init();
    }

    private void init() {
        final WebMarkupContainer issuesList = new WebMarkupContainer( "issues" );     // NON-NLS
        issuesList.add( new RefreshingView<Issue>( "issue" ) {                        // NON-NLS
            @SuppressWarnings( { "unchecked" } )
            @Override
            protected Iterator<IModel<Issue>> getItemModels() {
                final Project project = (Project) getApplication();
                final ScenarioAnalyst analyst = project.getScenarioAnalyst();
                return new TransformIterator(
                        analyst.findIssues( modelObject, false ),
                        new Transformer() {
                            public Object transform( Object o ) {
                                return new Model<Issue>( (Issue) o );

                            }
                        } );
            }

            @Override
            protected void populateItem( Item<Issue> item ) {
                final Issue issue = item.getModelObject();
                item.add( new Label( "message", issue.getDescription() ) );           // NON-NLS
                item.add( new Label( "suggest", issue.getRemediation() ) );           // NON-NLS
            }
        } );

        final ScenarioAnalyst analyst = ( (Project) getApplication() ).getScenarioAnalyst();
        issuesList.setVisible( analyst.hasIssues( modelObject, false ) );
        add( issuesList );
    }
}
