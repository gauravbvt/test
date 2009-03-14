package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.pages.ScenarioPage;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Editor on the details of a scenario (name, description, etc).
 */
public class ScenarioEditPanel extends AbstractCommandablePanel {
    /**
     * An issues panel for scenario issues.
     */
    private IssuesPanel issuesPanel;
    /**
     * Expansions.
     */
    private Set<Long> expansions;
    /**
     * The edited scenario.
     */
    private final IModel<Scenario> model;

    public ScenarioEditPanel( String id,
                              IModel<Scenario> model,
                              Set<Long> expansions ) {
        super( id );
        this.model = model;
        this.expansions = expansions;
        init();
    }

    private void init() {
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );                              // NON-NLS
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getScenario() );
            }
        } );
        add( nameField );

        TextArea<String> descField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );                       // NON-NLS
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getScenario() );
            }
        } );
        add( descField );
        issuesPanel = new IssuesPanel(
                "issues",
                new Model<ModelObject>( getScenario() ),
                expansions );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        issuesPanel.setVisible( Project.analyst().hasIssues( model.getObject(), false ) );
    }

    public IModel<Scenario> getModel() {
        return model;
    }

    private Scenario getScenario() {
        return getModel().getObject();
    }

    /**
     * Ajax update.
     *
     * @param target an ajax request target
     */
    public void updateWith( AjaxRequestTarget target, Object context ) {
        issuesPanel.setVisible( Project.analyst().hasIssues( model.getObject(), false ) );
        target.addComponent( issuesPanel );
        super.updateWith( target, context );
    }

    /**
     * Get scenario name.
     *
     * @return a string
     */
    public String getName() {
        return getScenario().getName();
    }

    /**
     * Set scenario name via command.
     *
     * @param name a string
     */
    public void setName( String name ) {
        doCommand( new UpdateProjectObject( getScenario(), "name", name ) );
    }

    /**
     * Get scenario description.
     *
     * @return a string
     */
    public String getDescription() {
        return getScenario().getDescription();
    }

    /**
     * Set scenario name via command.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdateProjectObject( getScenario(), "description", desc ) );
    }

}
