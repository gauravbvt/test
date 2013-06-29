package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Contact;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * An auto-complete contact field.
 */
public class ContactField extends AutoCompleteTextField<Contact> {

    private static final Logger LOG = LoggerFactory.getLogger( ContactField.class );

    private static final long serialVersionUID = -3258952571015176845L;

    @SpringBean
    ContactDao contactDao;

    public ContactField( String id, IModel<Contact> object ) {
        super( id, object, new AutoCompleteSettings().setCssClassName( "contact-popup" ) );
        setOutputMarkupId( true );
        

        add( new IValidator<Contact>() {
            @Override
            public void validate( IValidatable<Contact> validatable ) {
                if ( validatable.getValue() == null ) {
                    LOG.debug( "Invalid: null contact" );
                    validatable.error( new ValidationError().setMessage( "You must enter a valid contact" ) );
                }
            }
        } );
    }
    
    

    @Override
    protected Iterator<Contact> getChoices( String input ) {
        List<Contact> contacts = contactDao.find( input );
        LOG.debug( "Displaying {} choices for {}", contacts.size(), input );
        return contacts.iterator();
    }

    @Override
    public <C> IConverter<C> getConverter( Class<C> type ) {
        return new IConverter<C>() {
            @SuppressWarnings( "unchecked" )
            @Override
            public C convertToObject( String value, Locale locale ) {
                if ( value != null && !value.trim().isEmpty() ) {
                    List<Contact> contacts = contactDao.find( value );
                    LOG.debug( "Found {} contacts", contacts.size() );
                    if ( contacts.size() >= 1 ) {
                        Contact contact = contacts.get( 0 );
                        if ( contact.getFullName().equals( value ) )
                            return (C) contact;
                    }
                }

                LOG.debug( "Converted '{}' to null", value );
                return null;
            }

            @Override
            public String convertToString( C value, Locale locale ) {
                return value == null ? "" : value.toString();
            }
        };
    }
}
