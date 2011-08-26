package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 15, 2009
 * Time: 4:37:46 PM
 */
public class PlanEvaluationPanel  extends AbstractCommandablePanel {

    /**
      * Test passed icon.
      */
     private static final String PASS_IMAGE = "images/pass.png";
     /**
      * Test failed icon.
      */
     private static final String FAIL_IMAGE = "images/fail.png";

    public PlanEvaluationPanel(
            String id,
            IModel<? extends Identifiable> iModel,
            Set<Long> expansions ) {
        super( id, iModel, expansions  );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }



    private void init() {
        addEvaluation();
        addIssuesSummary();
    }

    private void addEvaluation() {
        WebMarkupContainer evaluation = new WebMarkupContainer( "evaluation" );
        evaluation.setOutputMarkupId( true );
        addOrReplace( evaluation );
        // Validity
        Image validityImage = new Image( "validityImage" );
        validityImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "validityImage" ) ) );
        validityImage.add( new AttributeModifier( "title", new PropertyModel<String>( this, "validityTitle" ) ) );
        validityImage.setOutputMarkupId( true );
        evaluation.add( validityImage );
        Label validityLabel = new Label( "validity", new PropertyModel<String>( this, "validityLabel" ) );
        validityLabel.setOutputMarkupId( true );
        evaluation.add( validityLabel );
        // Completeness
        Image completenessImage = new Image( "completenessImage" );
        completenessImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "completenessImage" ) ) );
        completenessImage.add( new AttributeModifier( "title", new PropertyModel<String>( this, "completenessTitle" ) ) );
        completenessImage.setOutputMarkupId( true );
        evaluation.add( completenessImage );
        Label completenessLabel = new Label( "completeness", new PropertyModel<String>( this, "completenessLabel" ) );
        completenessLabel.setOutputMarkupId( true );
        evaluation.add( completenessLabel );
        // Robustness
        Image robustnessImage = new Image( "robustnessImage" );
        robustnessImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "robustnessImage" ) ) );
        robustnessImage.add( new AttributeModifier( "title", new PropertyModel<String>( this, "robustnessTitle" ) ) );
        robustnessImage.setOutputMarkupId( true );
        evaluation.add( robustnessImage );
        Label robustnessLabel = new Label( "robustness", new PropertyModel<String>( this, "robustnessLabel" ) );
        robustnessLabel.setOutputMarkupId( true );
        evaluation.add( robustnessLabel );
    }

    private void addIssuesSummary() {
        add(  new IssuesSummaryTable( "issuesSummary" ) );
    }

    /**
     * Get image url for validity test.
     *
     * @return a string
     */
    public String getValidityImage() {
        boolean isValid = getAnalyst().isValid( getPlan() );
        return isValid ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for validity test.
     *
     * @return a string
     */
    public String getValidityTitle() {
        boolean isValid = getAnalyst().isValid( getPlan() );
        return isValid ? "Valid" : "Not valid";
    }

    /**
     * Get label for validity test result.
     *
     * @return a string
     */
    public String getValidityLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.VALIDITY );
        return count == 0 ? "Valid" : ( "Not yet valid (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }

    /**
     * Get image url for completeness test.
     *
     * @return a string
     */
    public String getCompletenessImage() {
        boolean isComplete = getAnalyst().isComplete( getPlan() );
        return isComplete ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for completeness test.
     *
     * @return a string
     */
    public String getCompletenessTitle() {
        boolean isComplete = getAnalyst().isComplete( getPlan() );
        return isComplete ? "Complete" : "Not complete";
    }

    /**
     * Get label for completeness test result.
     *
     * @return a string
     */
    public String getCompletenessLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.COMPLETENESS );
        return count == 0 ? "Complete" : ( "Not yet complete (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }

    /**
     * Get image url for robustness test.
     *
     * @return a string
     */
    public String getRobustnessImage() {
        boolean isRobust = getAnalyst().isRobust( getPlan() );
        return isRobust ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get image title for robustness test.
     *
     * @return a string
     */
    public String getRobustnessTitle() {
        boolean isRobust = getAnalyst().isRobust( getPlan() );
        return isRobust ? "Robust" : "Not yet robust";
    }

    /**
     * Get label for robustness test result.
     *
     * @return a string
     */
    public String getRobustnessLabel() {
        int count = getAnalyst().countTestFailures( getPlan(), Issue.ROBUSTNESS );
        return count == 0 ? "Robust" : ( "Not yet robust (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }




}
