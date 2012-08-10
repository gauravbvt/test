// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.pages.AckPage;
import com.mindalliance.playbook.pages.ConfirmPage;
import com.mindalliance.playbook.pages.ContactPic;
import com.mindalliance.playbook.pages.MessagesPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;

/**
 * A collaboration request, as shown in a list.
 */
public class RequestItem extends Panel {
    
    @SpringBean
    private ConfirmationReqDao reqDao;

    private static final long serialVersionUID = 6609089765862463562L;

    public RequestItem( String id, IModel<? extends ConfirmationReq> model, final boolean incoming ) {
        super( id, model );
        setRenderBodyOnly( true );
        final ConfirmationReq req = model.getObject();
        final Collaboration collaboration = req.getCollaboration();
        Contact contact = incoming ? req.getSender()
                                   : req.getRecipient();

        Link<?> link = new StatelessLink<Object>( "link" ) {
            @Override
            public void onClick() {
                if ( incoming )
                    setResponsePage( AckPage.class, new PageParameters().add( "id", req.getId() ) );
                else
                    setResponsePage( new ConfirmPage( collaboration ) );
            }
        };

        add(
            link.add(
                new WebMarkupContainer( "photo" )
                    .add( new AttributeModifier( "src", getPhotoUrl( contact ) ) )
                    .setVisible( contact != null && contact.hasPhoto() ),

                new Label( "origin", req.getOrigin( incoming ) ),
                new Label( "summary", req.getSummary() ),
                new Label( "description", req.getDescription() ) ),
            
            new StatelessLink( "delete" ){
                @Override
                public void onClick() {
                    reqDao.delete( req );
                    setResponsePage( MessagesPage.class );                    
                }
            }.setVisible( !incoming )
            );
    }

    private Serializable getPhotoUrl( Contact contact ) {
        return contact == null ?
               "#" :
               (Serializable) urlFor( ContactPic.class, 
                                      new PageParameters().add( "id", contact.getId() ) );
    }


}
