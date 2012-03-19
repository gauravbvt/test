package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import org.apache.wicket.model.Model;

/**
 * Panel for viewing the results of a survey.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/12
 * Time: 2:40 PM
 */
public class SurveyResultsPanel extends AbstractUpdatablePanel {
    public SurveyResultsPanel( String id, Model<RFISurvey> rfiSurveyModel ) {
        super( id, rfiSurveyModel );
        init();
    }

    private void init() {
        // todo
    }

    private RFISurvey getRFISurvey() {
        return (RFISurvey)getModel().getObject();
    }

}
