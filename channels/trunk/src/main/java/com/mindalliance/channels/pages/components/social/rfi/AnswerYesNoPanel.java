package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.RFI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 10:16 AM
 */
public class AnswerYesNoPanel extends AbstractAnswerPanel {

    private AjaxCheckBox yesCheckBox;
    private AjaxCheckBox noCheckBox;

    public AnswerYesNoPanel( String id, Model<Question> questionModel, Model<RFI> rfiModel ) {
        super(id, questionModel, rfiModel );
    }

    @Override
    protected void moreInit() {
        addYesNo();
    }

    private void addYesNo() {
         yesCheckBox = new AjaxCheckBox(
                "yes",
                new PropertyModel<Boolean>( this, "yes" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setChanged( true );
                target.add( noCheckBox );
            }
        };
        getContainer().add( yesCheckBox );
        noCheckBox = new AjaxCheckBox(
                "no",
                new PropertyModel<Boolean>( this, "no" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                setChanged( true );
                target.add( yesCheckBox );
            }
        };
        getContainer().add( noCheckBox );
    }

    public boolean isYes() {
        return getAnswer().isGiven() && getAnswer().isYes();
    }

    public void setYes( boolean val ) {
        if ( val ) {
            getAnswer().setYes();
        }
        else {
            getAnswer().remove();
        }
    }

    public boolean isNo() {
        return getAnswer().isGiven() && !getAnswer().isYes();
    }

    public void setNo( boolean val ) {
        if ( val ) {
            getAnswer().setNo();
        }
        else {
            getAnswer().remove();
        }
    }

}
