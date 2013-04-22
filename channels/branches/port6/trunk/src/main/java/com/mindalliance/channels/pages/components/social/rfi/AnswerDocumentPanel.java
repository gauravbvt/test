package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Document answer panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 10:20 AM
 */
public class AnswerDocumentPanel extends AbstractAnswerTextPanel {

    private WebMarkupContainer answersContainer;

    public AnswerDocumentPanel( String id, Model<Question> questionModel, Model<RFI> rfiModel ) {
        super( id, questionModel, rfiModel );
    }

    @Override
    protected void moreInit() {
        addDocumentLinks();
    }

    private void addDocumentLinks() {
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
                        new PropertyModel<String>( item.getModelObject(), "url" )
                );
                textField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        setChanged( true );
                        addDocumentLinks();
                        target.add( answersContainer );
                    }
                } );
                item.add( textField );
                String url = item.getModelObject().getText();
                ExternalLink link = new ExternalLink(
                        "testLink",
                        url == null ? "" : url,
                        "Test it");
                link.setVisible( url != null && !url.isEmpty() );
                item.add( link );
            }
        };
        answersContainer.add( textList );

    }

}
