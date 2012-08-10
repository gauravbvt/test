package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Contact details viewer.
 */
public class ContactPanel extends Panel {

    private static final long serialVersionUID = -7094022058897651369L;
    
    private Medium preferred;

    public ContactPanel( String id, IModel<Contact> model ) {
        super( id );
        setDefaultModel( new CompoundPropertyModel<Contact>( model ) );

        Contact contact = model.getObject();

        add(
            // TODO figure out what is the right way of doing this...
            new WebMarkupContainer( "photo" ).add( new AttributeModifier( "src",
                                                                          new Model<String>(
                                                                              "contacts/" + contact.getId() ) ) )
                                             .setVisible( contact.getPhoto() != null ),
            new Label( "fullName" ),
            new Label( "job" ),
            new ListView<Medium>( "media" ) {
                @Override
                protected void populateItem( ListItem<Medium> item ) {
                    Medium medium = item.getModelObject();
                    Component component = new Label( "address", medium.toString() ).add(
                        new AttributeModifier( "class", medium.getCssClass() ) );
                    
                    if ( medium.getActionUrl() != null )
                        component = component.add( new AttributeModifier( "href", medium.getActionUrl() ) );
                    
                    item.add( component );
                }
            } );
    }
}
