package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends Panel {
    /**
     * An issues panel for scenario issues.
     */
    private IssuesPanel scenarioIssuesPanel;

    public ScenarioEditPanel( String id,
                              final Scenario scenario ) {
        super( id, new CompoundPropertyModel<Scenario>( scenario ) );
        final TextField<String> name = new TextField<String>( "name" );                   // NON-NLS
        add( new FormComponentLabel( "name-label", name ) );                              // NON-NLS
        name.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( ( (ScenarioPage) getPage() ).getScenarioNameLabel() );
                target.addComponent( ( (ScenarioPage) getPage() ).getScenarioDropDownChoice() );
            }
        } );
        add( name );

        final TextArea<String> desc = new TextArea<String>( "description" );              // NON-NLS
        add( new FormComponentLabel( "description-label", desc ) );                       // NON-NLS
        desc.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( ( (ScenarioPage) getPage() ).getScenarioDescriptionLabel() );
            }
        } );
        add( desc );
        scenarioIssuesPanel = new IssuesPanel( "issues", new Model<ModelObject>( scenario ) );
        scenarioIssuesPanel.setOutputMarkupId( true );
        add( scenarioIssuesPanel );
    }

    /**
     * Ajax update.
     *
     * @param target an ajax request target
     */
    public void update( AjaxRequestTarget target ) {
        target.addComponent( scenarioIssuesPanel );
    }
}
