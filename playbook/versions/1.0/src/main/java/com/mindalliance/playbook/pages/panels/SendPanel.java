package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Step;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Details a send step.
 */
public class SendPanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger( SendPanel.class );

    private static final long serialVersionUID = 841767260474526472L;

    @SpringBean
    private ContactDao contactDao;
    
    @SpringBean
    private StepDao stepDao;

    public SendPanel( String id, final IModel<Step> model ) {
        super( id, model );
        setRenderBodyOnly( true );

        final MediaList mediaDiv = new MediaList( "mediaDiv", new PropertyModel<Medium>( model, "using" ),
                                                              new PropertyModel<List<Medium>>( model, "with.media" ),
                                                              "Contacting them using:" ) {
            @Override
            public void updateTo( Medium medium, AjaxRequestTarget target ) {
                SendPanel.this.updateTo( medium, target );
            }
        }
            .showList( ( (Collaboration) model.getObject() ).getWith() != null ); 
        
        add(
            new ContactField( "contact", new PropertyModel<Contact>( model, "with" ) ).add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        Contact contact = ( (Collaboration) model.getObject() ).getWith();
                        boolean visible = contact != null;
                        LOG.debug( "onChange: {}", visible );
                        mediaDiv.showList( visible );
                        target.add( mediaDiv );
                        updateTo( contact, target );
                        
                        if ( visible )
                            target.appendJavaScript( "$('#stepForm').trigger('create');" );
                    }
                } ),

            mediaDiv

        );
    }

    public void updateTo( Medium medium, AjaxRequestTarget target ) {
    }

    public void updateTo( Contact contact, AjaxRequestTarget target ) {
    }
}
