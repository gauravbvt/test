package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/6/12
 * Time: 12:43 PM
 */
public class QuestionnaireManagerPanel extends AbstractUpdatablePanel {
    public QuestionnaireManagerPanel( String id, IModel<Questionnaire> questionnaireModel ) {
        super( id, questionnaireModel );
        init();
    }

    private void init() {
        // todo
    }

}
