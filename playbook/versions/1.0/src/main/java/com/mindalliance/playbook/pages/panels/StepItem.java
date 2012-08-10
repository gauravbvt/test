// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.model.Subplay;
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

                    new Label( "title", step.getTitle() ),
                    new Label( "summary", getSummary( step ) ),
                    new Label( "description", step.getDescription() ) ),
                    new Label( "action", step.getActionText() )
                        .add( new AttributeModifier( "href", step.getActionLink() ) )
                        .setVisible( step.isCollaboration() && ( (Collaboration) step ).isSend() && step.getActionLink() != null ) );
    }

    private static String getSummary( Step step ) {
        switch ( step.getType() ) {

        case SUBPLAY:
            Play subplay = ( (Subplay) step ).getSubplay();
            return subplay == null ? "Execute a subplay" : "Execute play: " + subplay.getTitle();

        default:
        case TASK:
            return "";

        case SEND:
        case RECEIVE:

            Collaboration collaboration = (Collaboration) step;
            Contact with = collaboration.getWith();
            if ( with == null )
                return step.getType() == Type.SEND ? "With someone" : "When someone contacts me";
            else
                return collaboration.getMediumString();
        }        
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
