package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.social.feedback.AllUserFeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Floating panel with all user feedback.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/22/12
 * Time: 12:01 PM
 */
public class AllFeedbackFloatingPanel extends AbstractFloatingCommandablePanel {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    private boolean showProfile;
    private AllUserFeedbackPanel userFeedbackPanel;

    public AllFeedbackFloatingPanel( String id, Model<CollaborationModel> planModel, boolean showProfile ) {
        super( id, planModel, null );
        this.showProfile = showProfile;
        init( planModel );
    }

    @Override
    public String getHelpSectionId() {
        return "learning";
    }

    @Override
    public String getHelpTopicId() {
        return "feedback";
    }

    private void init( IModel<CollaborationModel> planModel ) {
        addHeading();
        addUserFeedback( planModel );
    }

    private void addUserFeedback( IModel<CollaborationModel> planModel ) {
        userFeedbackPanel = new AllUserFeedbackPanel(
                "allUserFeedback",
                planModel,
                showProfile,
                false,
                false );
        getContentContainer().add( userFeedbackPanel );
    }

    private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Feedback from all users" ) );
    }

    @Override
    protected String getTitle() {
        return "Feedback";
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Feedback.UNKNOWN );
        update( target, change );
    }

    public void select( Feedback feedback ) {
        userFeedbackPanel.select( feedback );
    }
}
