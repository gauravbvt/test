package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Step;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Detail about a receive step.
 */
public class ReceivePanel extends Panel {

    private static final long serialVersionUID = -4443924178443030877L;
    
    @SpringBean
    private Account account;

    public ReceivePanel( String id, IModel<Step> model ) {
        super( id, model );
        setRenderBodyOnly( true );
        
        add(
            new ContactField( "with", new PropertyModel<Contact>( model, "with" ) ),     
            new CheckBox( "startingPlay" ),
            new MediaList( "using", new PropertyModel<Medium>( model, "using" ), 
                                    new PropertyModel<List<Medium>>( account, "playbook.me.media"  ) )
        );
    }
}
