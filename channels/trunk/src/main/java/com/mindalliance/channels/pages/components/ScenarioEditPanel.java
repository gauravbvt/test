package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends Panel {

    public ScenarioEditPanel( String id, final Scenario scenario ) {
        super( id, new CompoundPropertyModel<Scenario>( scenario ) );

        final TextField<String> name = new TextField<String>( "name" );                   // NON-NLS
        add( new FormComponentLabel( "name-label", name ) );                              // NON-NLS
        add( name );

        final TextArea<String> desc = new TextArea<String>( "description" );              // NON-NLS
        add( new FormComponentLabel( "description-label", desc ) );                       // NON-NLS
        add( desc );

        final WebMarkupContainer issuesList = new WebMarkupContainer( "issues" );         // NON-NLS
        issuesList.add( new RefreshingView<DetectedIssue>( "issue" ) {                            // NON-NLS
            @SuppressWarnings( { "unchecked" } )
            @Override
            protected Iterator<IModel<DetectedIssue>> getItemModels() {
                final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
                return new TransformIterator(
                        analyst.findIssues( scenario, true ),
                        new Transformer() {
                            public Object transform( Object o ) {
                                return new Model<DetectedIssue>( (DetectedIssue) o );

                            }
                        } );
            }

            @Override
            protected void populateItem( Item<DetectedIssue> item ) {
                final DetectedIssue issue = item.getModelObject();
                item.add( new Label( "message", issue.getDescription() ) );               // NON-NLS
                item.add( new Label( "suggest", issue.getRemediation() ) );               // NON-NLS
            }
        } );

        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        issuesList.setVisible( analyst.hasIssues( scenario, true ) );
        add( issuesList );
    }
}
