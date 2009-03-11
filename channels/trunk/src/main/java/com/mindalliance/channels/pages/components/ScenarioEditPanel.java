package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends AbstractCommandablePanel {
    /**
     * An issues panel for scenario issues.
     */
    private IssuesPanel scenarioIssuesPanel;
    /**
     * The edited scenario.
     */
    private Scenario scenario;

    public ScenarioEditPanel( String id,
                              Scenario scenario ) {
        super( id );
        this.scenario = scenario;
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );                              // NON-NLS
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                 ( (ScenarioPage) getPage() ).updateExceptScenarioEditPanel(target);
            }
        } );
        add( nameField );

        TextArea<String> descField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );                       // NON-NLS
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                ( (ScenarioPage) getPage() ).updateExceptScenarioEditPanel(target);
             }
        } );
        add( descField );
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

    /**
     * Get scenario name.
     *
     * @return a string
     */
    public String getName() {
        return scenario.getName();
    }

    /**
     * Set scenario name via command.
     * @param name a string
     */
    public void setName( String name ) {
        doCommand( new UpdateProjectObject( scenario, "name", name ) );
    }

    /**
     * Get scenario description.
     *
     * @return a string
     */
    public String getDescription() {
        return scenario.getDescription();
    }

    /**
     * Set scenario name via command.
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdateProjectObject( scenario, "description", desc ) );
    }

}
