package com.mindalliance.playbook.services.impl;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.AddressMedium;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.OtherMedium;
import com.mindalliance.playbook.services.ContactMerger;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.Property.Id;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.VCardBuilder;
import net.fortuna.ical4j.vcard.property.Address;
import net.fortuna.ical4j.vcard.property.N;
import net.fortuna.ical4j.vcard.property.Note;
import net.fortuna.ical4j.vcard.property.Org;
import net.fortuna.ical4j.vcard.property.Photo;
import net.fortuna.ical4j.vcard.property.Role;
import net.fortuna.ical4j.vcard.property.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic contact merger implementation.
 * <p/>
 * Merging is done by finding the first contact with some matching key values (name, email, etc) that doesn't have any
 * different non-null values for properties.
 * <p/>
 * If merging cannot be done, the contact is simply added to the current account.
 */
@Service
public class ContactMergerImpl implements ContactMerger {

    private static final Logger LOG = LoggerFactory.getLogger( ContactMergerImpl.class );

    @Autowired
    private ContactDao contactDao;
    
    @Autowired
    private AccountDao accountDao;

    private static Contact convert( Account account, VCard card ) {
        Contact contact = new Contact( account );

        N name = (N) card.getProperty( Id.N );
        if ( name != null ) {
            contact.setFamilyName( name.getFamilyName() );
            contact.setGivenName( name.getGivenName() );
            contact.setAdditionalNames( concatenate( name.getAdditionalNames() ) );
            contact.setPrefixes( concatenate( name.getPrefixes() ) );
            contact.setSuffixes( concatenate( name.getSuffixes() ) );
        }

        Property nick = card.getProperty( Id.NICKNAME );
        if ( nick != null )
            contact.setNickname( nick.getValue() );

        Role r = (Role) card.getProperty( Id.ROLE );
        if ( r != null )
            contact.setRole( r.getValue() );

        Org o = (Org) card.getProperty( Id.ORG );
        if ( o != null )
            contact.setOrganization( o.getValue() );

        Title t = (Title) card.getProperty( Id.TITLE );
        if ( t != null )
            contact.setTitle( t.getValue() );

        Note note = (Note) card.getProperty( Id.NOTE );
        if ( note != null )
            contact.setNote( note.getValue() );

        Photo photo = (Photo) card.getProperty( Id.PHOTO );
        if ( photo != null )
            contact.setPhoto( photo.getBinary() );

        for ( Property e : card.getProperties( Id.EMAIL ) )
            contact.addMedium( new OtherMedium( contact, "EMAIL", e.getValue() ) );

        for ( Property phone : card.getProperties( Id.TEL ) )
            contact.addMedium( new OtherMedium( contact,
                                                phone.getParameter( Parameter.Id.TYPE ).getValue(),
                                                phone.getValue() ) );

        for ( Property address : card.getProperties( Id.ADR ) )
            contact.addMedium( convert( contact, (Address) address ) );

        return contact;
    }

    private static Medium convert( Contact contact, Address address ) {
        List<Parameter> parameters = address.getParameters( Parameter.Id.TYPE );
        String type = "ADDRESS";
        boolean preferred = false;
        for ( Parameter parameter : parameters ) {
            if ( "pref".equalsIgnoreCase( parameter.getValue() ) )
                preferred = true;
            else
                type = parameter.getValue();
        }

        Medium medium = new AddressMedium( contact, type, convert( address ) );
        medium.setPreferred( preferred );
        return medium;
    }

    private static com.mindalliance.playbook.model.Address convert( Address address ) {
        com.mindalliance.playbook.model.Address result = new com.mindalliance.playbook.model.Address();

        result.setStreet( address.getStreet() );
        result.setRegion( address.getRegion() );
        result.setLocality( address.getLocality() );
        result.setPostalCode( address.getPostcode() );
        result.setPoBox( address.getPoBox() );
        result.setCountry( address.getCountry() );

        return result;
    }

    private static String concatenate( String[] strings ) {
        StringBuilder buffer = new StringBuilder();

        for ( String item : strings ) {
            if ( buffer.length() != 0 )
                buffer.append( ", " );
            buffer.append( item );
        }

        return buffer.length() == 0 ? null : buffer.toString();
    }

    @Override
    public void merge( Contact newContact ) {

        // Try matching emails
        for ( Object email : newContact.getEmails() )
            for ( Contact oldContact : contactDao.findByEmail( email ) )
                if ( oldContact.isMergeableWith( newContact ) ) {
                    LOG.debug( "Merging {} in {}", newContact, oldContact );
                    oldContact.merge( newContact );
                    return;
                }

        // Try by name, family name etc.
        for ( Contact oldContact : contactDao.findByName( newContact ) )
            if ( oldContact.isMergeableWith( newContact ) ) {
                LOG.debug( "Merging {} in {}", newContact, oldContact );
                oldContact.merge( newContact );
                return;
                }

        accountDao.getCurrentAccount().addContact( newContact );
        LOG.debug( "Added {}", newContact );
    }

    @Override
    public void merge( List<Contact> contacts ) {
        for ( Contact contact : contacts )
            merge( contact );
        LOG.debug( "Merged {} contacts", contacts.size() );
    }

    @Override
    public void importVCards( InputStream inputStream ) throws IOException {
        List<Contact> contacts = new ArrayList<Contact>();
        Account realAccount = accountDao.getCurrentAccount();

        CompatibilityHints.setHintEnabled( CompatibilityHints.KEY_RELAXED_PARSING, true );
        try {
            for ( VCard card : new VCardBuilder( inputStream ).buildAll() )
                contacts.add( convert( realAccount, card ) );
            
        } catch ( ParserException e ) {
            LOG.error( "Error while importing vcards", e );
            throw new RuntimeException( e );
        }

        merge( contacts );
    }
}
