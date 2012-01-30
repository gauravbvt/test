package com.mindalliance.playbook.pages;

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
    
    public ConfirmPage( final Collaboration collaboration ) {
        final ConfirmationReq req = collaboration.createRequest();

        IModel<ConfirmationReq> model = new CompoundPropertyModel<ConfirmationReq>( req );
        setDefaultModel( model );
        
        add(
            new Label( "hTitle", getPageTitle() ),
            new Label( "collaboration.with.fullName" ), 
            new StatelessForm<ConfirmationReq>( "form", model ) {
                @Override
                protected void onSubmit() {
                    LOG.debug( "Saved" );
                    collaboration.addRequest( req );
                    stepDao.save( collaboration );
                    ConfirmPage.this.continueToOriginalDestination();
                }
            }.add(
                new Label( "collaboration.with.givenName" ),
                new TextArea<String>( "description" ),
                new CheckBox( "forwardable" ),
                new StatelessLink( "cancel" ) {
                    @Override
                    public void onClick() {
                        // TODO find the proper way of doing this
                        LOG.debug( "Cancelled" );
                        ConfirmPage.this.continueToOriginalDestination();
                    }
                }
            )
        );
            
    }

    @Override
    public String getPageTitle() {
        return "Confirm collaboration";
    }
}
