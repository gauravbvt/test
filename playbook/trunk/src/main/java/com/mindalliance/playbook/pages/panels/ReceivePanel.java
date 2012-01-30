package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Step;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
            new RadioGroup<Medium>( "using", new PropertyModel<Medium>( model, "using" ) ).add(
                new ListView<Medium>( "media", new PropertyModel<List<? extends Medium>>( account, 
                    "playbook.me.media"  ) ) {
                    @Override
                    protected void populateItem( ListItem<Medium> item ) {
                        IModel<Medium> itemModel = item.getModel();
                        String mediumId = "medium" + item.getId();

                        item.add(
                            new WebMarkupContainer( "label" ).add(
                                new Label( "type", new PropertyModel<String>( itemModel, "type" ) ), 
                                new Label( "address", new PropertyModel<String>( itemModel, "address" ) ) )
                                .add( new AttributeModifier( "for", new Model<String>( mediumId ) ) ),

                            new Radio<Medium>( "value", itemModel ).add(
                                    new AttributeModifier( "id", new Model<String>( mediumId ) ) ) 
                                )
                                .setRenderBodyOnly( true );

                    }
                }.setRenderBodyOnly( true ) ).setRenderBodyOnly( true )

        );
    }
}
