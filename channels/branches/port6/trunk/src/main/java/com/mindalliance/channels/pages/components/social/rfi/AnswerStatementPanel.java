package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import org.apache.wicket.model.IModel;

/**
 * A statement in an RFI.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/30/12
 * Time: 1:31 PM
 */
public class AnswerStatementPanel extends AbstractAnswerPanel {

    public AnswerStatementPanel( String id, IModel<Question> questionModel, IModel<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    protected void moreInit() {
        // Nothing
    }
}
