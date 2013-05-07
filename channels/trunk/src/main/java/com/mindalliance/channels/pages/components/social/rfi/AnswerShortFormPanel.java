package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.db.data.surveys.Question;
import com.mindalliance.channels.db.data.surveys.RFI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Short form answer panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 10:17 AM
 */
public class AnswerShortFormPanel extends AbstractAnswerTextPanel {
    private WebMarkupContainer answersContainer;

    public AnswerShortFormPanel( String id, Model<Question> questionModel, Model<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    protected void moreInit() {
        addShortTexts();
    }

    private void addShortTexts() {
        answersContainer = new WebMarkupContainer( "answers" );
        answersContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( answersContainer );
        ListView<AnswerWrapper> textList = new ListView<AnswerWrapper>(
                "texts",
                getAnswerWrappers()
        ) {
            @Override
            protected void populateItem( ListItem<AnswerWrapper> item ) {
                TextField<String> textField = new TextField<String>(
                        "text",
                        new PropertyModel<String>( item.getModelObject(), "text" )
                );
                textField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        setChanged( true );
                        addShortTexts();
                        target.add( answersContainer );
                    }
                } );
                item.add( textField );
            }
        };
        answersContainer.add( textList );

    }

}
