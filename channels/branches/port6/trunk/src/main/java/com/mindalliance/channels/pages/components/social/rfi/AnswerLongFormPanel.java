package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Long form answer panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 10:19 AM
 */
public class AnswerLongFormPanel extends AbstractAnswerTextPanel {
    private WebMarkupContainer answersContainer;

    public AnswerLongFormPanel( String id, Model<Question> questionModel, Model<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    protected void moreInit() {
        addLongTexts();
    }

    private void addLongTexts() {
        answersContainer = new WebMarkupContainer( "answers" );
        answersContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( answersContainer );
        ListView<AbstractAnswerTextPanel.AnswerWrapper> textList = new ListView<AbstractAnswerTextPanel.AnswerWrapper>(
                "texts",
                getAnswerWrappers()
        ) {
            @Override
            protected void populateItem( ListItem<AbstractAnswerTextPanel.AnswerWrapper> item ) {
                TextArea<String> textArea = new TextArea<String>(
                        "text",
                        new PropertyModel<String>( item.getModelObject(), "text" )
                );
                textArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        addLongTexts();
                        target.add( answersContainer );
                    }
                } );
                item.add( textArea );
            }
        };
        answersContainer.add( textList );

    }

}

