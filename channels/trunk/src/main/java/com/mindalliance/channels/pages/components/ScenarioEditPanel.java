package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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
        setOutputMarkupId( true );
        TextField<String> nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );                              // NON-NLS
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "name" ) );
            }
        } );
        add( nameField );

        TextArea<String> descField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );                       // NON-NLS
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getScenario(), "description" ) );
            }
        } );
        add( descField );
        issuesPanel = new IssuesPanel(
                "issues",
                new Model<ModelObject>( getScenario() ),
                expansions );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, Project.analyst().hasIssues( model.getObject(), false ) );
    }

    public IModel<Scenario> getModel() {
        return model;
    }

    private Scenario getScenario() {
        return getModel().getObject();
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

    /**
     * {@inheritDoc}
     */
    public void changed( AjaxRequestTarget target, Change change ) {
        makeVisible( target, issuesPanel, Project.analyst().hasIssues( model.getObject(), false ) );
        super.changed( target, change );
    }

    /**
     * Change visibility.
     * @param target an ajax request target
     * @param visible a boolean
     */
    public void setVisibility( AjaxRequestTarget target, boolean visible ) {
        makeVisible( target, this, visible );
        if (visible)
            makeVisible( issuesPanel, Project.analyst().hasIssues( model.getObject(), false ) );
    }
}
