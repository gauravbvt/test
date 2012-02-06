package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collaboration confirmation page.
 */
public class ConfirmPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( ConfirmPage.class );

    private static final long serialVersionUID = -1053045641211330634L;

    @SpringBean
    private StepDao stepDao;

    @SpringBean
    private ConfirmationReqDao confirmationReqDao;
    
    public ConfirmPage( final Collaboration collaboration ) {
        final ConfirmationReq req = collaboration.createRequest();

        IModel<ConfirmationReq> model = new CompoundPropertyModel<ConfirmationReq>( req );
        setDefaultModel( model );
        
        add(
            new Label( "hTitle", getPageTitle() ),
            new Label( "collaboration.with.fullName" ),
            new StatelessLink( "cancel" ) {
                @Override
                public void onClick() {
                    if ( req.getId() == 0L )
                        setResponsePage( EditStep.class, new PageParameters().add( "id", 
                            req.getCollaboration().getId() ) );
                    else
                        setResponsePage( MessagesPage.class );
                }
            },
            new StatelessForm<ConfirmationReq>( "form", model ) {
                @Override
                protected void onSubmit() {
                    LOG.debug( "Saved" );
                    boolean saved = req.getId() == 0L;

                    confirmationReqDao.save( req );

                    if ( saved )
                        setResponsePage( EditStep.class, new PageParameters().add( "id",
                            req.getCollaboration().getId() ) );
                    else
                        setResponsePage( MessagesPage.class );
                }
            }.add(
                new Label( "collaboration.with.givenName" ),
                new TextArea<String>( "description" ),
                new CheckBox( "forwardable" )
            )
        );
            
    }

    @Override
    public String getPageTitle() {
        return "Confirm collaboration";
    }
}
