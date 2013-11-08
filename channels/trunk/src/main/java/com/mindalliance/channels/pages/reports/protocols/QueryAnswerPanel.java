package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.RequestData;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Query answer panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/10/13
 * Time: 2:24 PM
 */
public class QueryAnswerPanel extends AbstractDataPanel {

    private final RequestData requestData;
    private final int index;
    private WebMarkupContainer queryContainer;
    private boolean showingMore = false;
    private AjaxLink<String> moreLessButton;
    private CommitmentDataPanel commitmentDataPanel;

    public QueryAnswerPanel( String id, RequestData requestData, int index, ProtocolsFinder finder ) {
        super( id, finder );
        this.requestData = requestData;
        this.index = index;
        init();
    }

    private void init() {
        addQueryContainer();
        addAnswer();
        addFeedbackPanel();
        addMoreLessButton();
        addCommitment();
    }

    private void addQueryContainer() {
        queryContainer = new WebMarkupContainer( "query" );
        String cssClasses = index % 2 == 0 ? "data-table step even-step" : "data-table step odd-step";
        queryContainer.add( new AttributeModifier( "class", cssClasses ) );
        add( queryContainer );
    }

    private void addAnswer() {
        Flow.Intent intent =getSharing().getIntent();
        String message = getSharing().getName();
        if ( message == null ) message = "something";
        String label = "I give ";
        label += intent == null
                ? "information"
                : intent.getLabel().toLowerCase();
        label += " \"" + message + "\"";
        queryContainer.add( new Label( "answer", label ) );
    }

    private Flow getSharing() {
        return requestData.getSharing();
    }

    private void addFeedbackPanel() {
        UserFeedbackPanel feedbackPanel = new UserFeedbackPanel(
                "feedback",
                requestData.flow(),
                "Feedback",
                Feedback.CHECKLISTS
        );
        queryContainer.addOrReplace( feedbackPanel );
    }

    private void addMoreLessButton() {
        moreLessButton = new AjaxLink<String>( "moreLessButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingMore = !showingMore;
                addMoreLessButton();
                target.add( moreLessButton );
                addCommitment();
                target.add( commitmentDataPanel );
            }
        };
        moreLessButton.setOutputMarkupId( true );
        moreLessButton.add( new Label( "moreLess", showingMore ? "- Less" : "+ More" ) );
        moreLessButton.add( new AttributeModifier( "class", "more" ) );
        queryContainer.addOrReplace( moreLessButton );
    }

    private void addCommitment() {
        commitmentDataPanel = new CommitmentDataPanel(
                "commitment",
                requestData,
                false,
                getFinder()
        );
        commitmentDataPanel.setOutputMarkupId( true );
        makeVisible( commitmentDataPanel, showingMore );
        addOrReplace( commitmentDataPanel );
    }

}
