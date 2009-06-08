package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan edit details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 5:31:25 AM
 */
public class PlanEditDetailsPanel extends AbstractCommandablePanel {

    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;
    /**
     * Test passed icon.
     */
    private static final String PASS_IMAGE = "images/pass.png";
    /**
     * Test failed icon.
     */
    private static final String FAIL_IMAGE = "images/fail.png";

    public PlanEditDetailsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addTestResults();
        addIdentityFields();
        addIssuesPanel();
        add( new AttachmentPanel( "attachments", new Model<ModelObject>( getPlan() ) ) );
        adjustComponents();
    }

    private void addTestResults() {
        // Validity
        Image validityImage = new Image( "validityImage" );
        validityImage.add( new AttributeModifier("src", new PropertyModel<String>(this, "validityImage") ));
        validityImage.add( new AttributeModifier("title", new PropertyModel<String>(this, "validityTitle") ));
        validityImage.setOutputMarkupId( true );
        add( validityImage );
        Label validityLabel = new Label("validity", new PropertyModel<String>(this, "validityLabel"));
        validityLabel.setOutputMarkupId( true );
        add( validityLabel );
        // Completeness
        Image completenessImage = new Image( "completenessImage" );
        completenessImage.add( new AttributeModifier("src", new PropertyModel<String>(this, "completenessImage") ));
        completenessImage.add( new AttributeModifier("title", new PropertyModel<String>(this, "completenessTitle") ));
        completenessImage.setOutputMarkupId( true );
        add( completenessImage );
        Label completenessLabel = new Label("completeness", new PropertyModel<String>(this, "completenessLabel"));
        completenessLabel.setOutputMarkupId( true );
        add( completenessLabel );
        // Robustness
        Image robustnessImage = new Image( "robustnessImage" );
        robustnessImage.add( new AttributeModifier("src", new PropertyModel<String>(this, "robustnessImage") ));
        robustnessImage.add( new AttributeModifier("title", new PropertyModel<String>(this, "robustnessTitle") ));
        robustnessImage.setOutputMarkupId( true );
        add( robustnessImage );
        Label robustnessLabel = new Label("robustness", new PropertyModel<String>(this, "robustnessLabel"));
        robustnessLabel.setOutputMarkupId( true );
        add( robustnessLabel );
    }

    /**
     * Get image url for validity test.
     * @return a string
     */
    public String getValidityImage() {
        boolean isValid = getAnalyst().isValid( getPlan() );
        return isValid ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for validity test.
     * @return a string
     */
    public String getValidityTitle() {
        boolean isValid = getAnalyst().isValid( getPlan() );
        return isValid ? "Valid" : "Not valid";
    }

    /**
     * Get label for validity test result.
     * @return a string
     */
    public String getValidityLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.VALIDITY );
        return count == 0 ? "Valid" : ("Not yet valid (" + count + (count == 1 ? " issue)" : " issues)") );
    }

    /**
     * Get image url for completeness test.
     * @return a string
     */
    public String getCompletenessImage() {
        boolean isComplete = getAnalyst().isComplete( getPlan() );
        return isComplete ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for completeness test.
     * @return a string
     */
    public String getCompletenessTitle() {
        boolean isComplete = getAnalyst().isComplete( getPlan() );
        return isComplete ? "Complete" : "Not complete";
    }

    /**
     * Get label for completeness test result.
     * @return a string
     */
    public String getCompletenessLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.COMPLETENESS );
        return count == 0 ? "Complete" : ("Not yet complete (" + count + (count == 1 ? " issue)" : " issues)") );
    }

    /**
     * Get image url for robustness test.
     * @return a string
     */
    public String getRobustnessImage() {
        boolean isRobust = getAnalyst().isRobust( getPlan() );
        return isRobust ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for robustness test.
     * @return a string
     */
    public String getRobustnessTitle() {
        boolean isRobust = getAnalyst().isRobust( getPlan() );
        return isRobust ? "Robust" : "Not yet robust";
    }

    /**
     * Get label for robustness test result.
     * @return a string
     */
    public String getRobustnessLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.ROBUSTNESS );
        return count == 0 ? "Robust" : ("Not robust (" + count + (count == 1 ? " issue)" : " issues)") );
    }

    private void addIdentityFields() {
        final List<String> choices = getUniqueNameChoices( getPlan() );
        TextField<String> nameField = new AutoCompleteTextField<String>( "name",
                new PropertyModel<String>( this, "plan.name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "name" ) );
            }
        } );
        add( nameField );
        TextArea<String> descriptionField = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "description" ) );
            }
        } );
        add( descriptionField );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "plan" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
    }

    protected void adjustComponents() {
        makeVisible( issuesPanel, getAnalyst().hasIssues( getPlan(), false ) );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan) getModel().getObject();
    }

    /**
     * Get the model object's name
     *
     * @return a string
     */
    public String getName() {
        return getPlan().getName();
    }

    /**
     * Set the model object's unique new name.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null ) {
            String oldName = getPlan().getName();
            String uniqueName = name.trim();
            if ( !isSame( oldName, name ) ) {
                List<String> namesTaken = getQueryService().findAllNames( Plan.class );
                int count = 2;
                while ( namesTaken.contains( uniqueName ) ) {
                    uniqueName = name + "(" + count++ + ")";
                }
                doCommand(
                        new UpdatePlanObject(
                                getPlan(),
                                "name",
                                uniqueName,
                                UpdateObject.Action.Set
                        )
                );
            }
        }
    }

    /**
     * Get the model object's description
     *
     * @return a string
     */
    public String getDescription() {
        return getPlan().getDescription();
    }

    /**
     * Set the model object's description.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        if ( desc != null )
            doCommand(
                    new UpdatePlanObject(
                            getPlan(),
                            "description",
                            desc,
                            UpdateObject.Action.Set ) );
    }


    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isUpdated() || change.getSubject() instanceof Issue ) {
            adjustComponents();
            target.addComponent( this );
        }
        super.updateWith( target, change );
    }

}
