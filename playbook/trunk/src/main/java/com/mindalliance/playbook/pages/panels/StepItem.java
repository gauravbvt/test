// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.pages.ContactPic;
import com.mindalliance.playbook.pages.EditStep;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

/**
 * A step, as displayed in a list.
 */
public class StepItem extends Panel {

    private static final long serialVersionUID = 8719656619488288953L;

    public StepItem( String id, IModel<? extends Step> stepModel, boolean showPlay ) {
        super( id, stepModel );
        setRenderBodyOnly( true );
        Step step = stepModel.getObject();
        Contact contact = getContact( step );

        add(
            new BookmarkablePageLink<EditStep>( "link", EditStep.class, new PageParameters().add( "id", step.getId() ) )
                .add(
                    new WebMarkupContainer( "photo" ).add(
                        new AttributeModifier(
                            "src",
                            getPhotoUrl( contact ) ) ).setVisible( contact != null && contact.hasPhoto() ),

                    new WebMarkupContainer( "play" ).add(
                        new Label(
                            "playTitle",
                            step.getPlay().getTitle() ) ).setVisible( showPlay ),

                    new Label( "sequence", String.valueOf( step.getSequence() ) ),
                    new Label( "title", step.getTitle() ),
                    new Label( "summary", getSummary( step ) ),
                    new Label( "description", step.getDescription() ) ) );
    }

    private String getSummary( Step step ) {
        if ( !step.isCollaboration() )
            return step.getDescription();
        
        Collaboration collaboration = (Collaboration) step;
        return "With " + collaboration.getWith() + " using " + collaboration.getUsing();
    }

    private Serializable getPhotoUrl( Contact contact ) {
        return contact == null ?
               "#" :
               (Serializable) urlFor( ContactPic.class, new PageParameters().add( "id", contact.getId() ) );
    }

    private static Contact getContact( Step step ) {
        return step.isCollaboration() ? ( (Collaboration) step ).getWith() : null;
    }
}
