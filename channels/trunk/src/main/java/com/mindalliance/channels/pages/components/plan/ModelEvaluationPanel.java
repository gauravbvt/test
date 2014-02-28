/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

public class ModelEvaluationPanel extends AbstractCommandablePanel {

    /**
     * Test failed icon.
     */
    private static final String FAIL_IMAGE = "images/fail.png";

    /**
     * Test passed icon.
     */
    private static final String PASS_IMAGE = "images/pass.png";

    //-------------------------------
    public ModelEvaluationPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
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
        WebMarkupContainer validityImage = new WebMarkupContainer( "validityImage" );
        validityImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "validityImage" ) ) );
        addTipTitle( validityImage, new PropertyModel<String>( this, "validityTitle" ) );
        validityImage.setOutputMarkupId( true );
        evaluation.add( validityImage );
        Label validityLabel = new Label( "validity", new PropertyModel<String>( this, "validityLabel" ) );
        validityLabel.setOutputMarkupId( true );
        evaluation.add( validityLabel );
        // Completeness
        WebMarkupContainer completenessImage = new WebMarkupContainer( "completenessImage" );
        completenessImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "completenessImage" ) ) );
        addTipTitle( completenessImage, new PropertyModel<String>( this, "completenessTitle" ) );
        completenessImage.setOutputMarkupId( true );
        evaluation.add( completenessImage );
        Label completenessLabel = new Label( "completeness", new PropertyModel<String>( this, "completenessLabel" ) );
        completenessLabel.setOutputMarkupId( true );
        evaluation.add( completenessLabel );
        // Robustness
        WebMarkupContainer robustnessImage = new WebMarkupContainer( "robustnessImage" );
        robustnessImage.add( new AttributeModifier( "src", new PropertyModel<String>( this, "robustnessImage" ) ) );
        addTipTitle( robustnessImage, new PropertyModel<String>( this, "robustnessTitle" ) );
        robustnessImage.setOutputMarkupId( true );
        evaluation.add( robustnessImage );
        Label robustnessLabel = new Label( "robustness", new PropertyModel<String>( this, "robustnessLabel" ) );
        robustnessLabel.setOutputMarkupId( true );
        evaluation.add( robustnessLabel );
    }

    private void addIssuesSummary() {
        add( new IssuesSummaryTable( "issuesSummary", new IssueMetrics( getCommunityService() ) ) );
    }

    //-------------------------------
    /**
     * Get image url for completeness test.
     *
     * @return a string
     */
    public String getCompletenessImage() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isComplete = doctor.isComplete( getCommunityService(), getCollaborationModel() );
        return isComplete ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get label for completeness test result.
     *
     * @return a string
     */
    public String getCompletenessLabel() {
        Doctor doctor = getCommunityService().getDoctor();
        int count = doctor.countTestFailures( getCommunityService(), getCollaborationModel(), Issue.COMPLETENESS );
        return count == 0 ? "Complete" : ( "Not yet complete (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }

    /**
     * Get image title for completeness test.
     *
     * @return a string
     */
    public String getCompletenessTitle() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isComplete = doctor.isComplete( getCommunityService(), getCollaborationModel() );
        return isComplete ? "Complete" : "Not complete";
    }

    /**
     * Get image url for robustness test.
     *
     * @return a string
     */
    public String getRobustnessImage() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isRobust = doctor.isRobust( getCommunityService(), getCollaborationModel() );
        return isRobust ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get label for robustness test result.
     *
     * @return a string
     */
    public String getRobustnessLabel() {
        Doctor doctor = getCommunityService().getDoctor();
        int count = doctor.countTestFailures( getCommunityService(), getCollaborationModel(), Issue.ROBUSTNESS );
        return count == 0 ? "Robust" : ( "Not yet robust (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }

    /**
     * Get image title for robustness test.
     *
     * @return a string
     */
    public String getRobustnessTitle() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isRobust = doctor.isRobust( getCommunityService(), getCollaborationModel() );
        return isRobust ? "Robust" : "Not yet robust";
    }

    /**
     * Get image url for validity test.
     *
     * @return a string
     */
    public String getValidityImage() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isValid = doctor.isValid( getCommunityService(), getCollaborationModel() );
        return isValid ? PASS_IMAGE : FAIL_IMAGE;
    }

    /**
     * Get label for validity test result.
     *
     * @return a string
     */
    public String getValidityLabel() {
        Doctor doctor = getCommunityService().getDoctor();
        int count = doctor.countTestFailures( getCommunityService(), getCollaborationModel(), Issue.VALIDITY );
        return count == 0 ? "Valid" : ( "Not yet valid (" + count + ( count == 1 ? " issue)" : " issues)" ) );
    }

    /**
     * Get image title for validity test.
     *
     * @return a string
     */
    public String getValidityTitle() {
        Doctor doctor = getCommunityService().getDoctor();
        boolean isValid = doctor.isValid( getCommunityService(), getCollaborationModel() );
        return isValid ? "Valid" : "Not valid";
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }
}
