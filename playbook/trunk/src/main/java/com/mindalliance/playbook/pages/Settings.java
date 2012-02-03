package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.AddressMedium;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.OtherMedium;
import com.mindalliance.playbook.pages.login.Reset;
import com.mindalliance.playbook.pages.panels.ContactPanel;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Settings popup.
 */
public class Settings extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( Settings.class );

    private static final long serialVersionUID = -3349648435696781610L;

    @SpringBean
    private Account account;

    @SpringBean
    private AccountDao accountDao;

    @SpringBean
    private ContactMerger contactMerger;

    @SuppressWarnings( "unchecked" )
    public Settings( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );

        ArrayList<FileUpload> upload = new ArrayList<FileUpload>();
        final FileUploadField uploadField = new FileUploadField( "contactUpload", new Model( upload ) );

        Form<Account> form = new StatelessForm<Account>( "form" ) {
            @Override
            protected void onSubmit() {
                FileUpload upload = uploadField.getFileUpload();
                Account realAccount = accountDao.deproxy( account );
                if ( upload != null )
                    try {
                        InputStream inputStream = upload.getInputStream();
                        try {
                            List<Contact> contacts = new ArrayList<Contact>();

                            CompatibilityHints.setHintEnabled( CompatibilityHints.KEY_RELAXED_PARSING, true );
                            for ( VCard card : new VCardBuilder( inputStream ).buildAll() )
                                contacts.add( convert( realAccount, card ) );

                            contactMerger.merge( contacts );
                            
                        } catch ( ParserException e ) {
                            LOG.error( "Unable to import contacts", e );
                            throw new RuntimeException( "Unable to import contacts", e );

                        } finally {
                            inputStream.close();
                        }
                    } catch ( IOException e ) {
                        LOG.error( "Error uploading contact file", e );
                    }

                accountDao.save( realAccount );
                setResponsePage( Settings.class );
            }
        };

        form.setMultiPart( true );

        add(
            new BookmarkablePageLink( "home", PlaysPage.class ),

            form.add(
                new Label( "email" ),
                new Label( "contacts.size" ),
                new CheckBox( "viewByTags" ),
                new CheckBox( "showInactive" ),
                new ContactPanel( "playbook.me", new PropertyModel<Contact>( account, "playbook.me" ) ),
                new BookmarkablePageLink<Reset>( "password", Reset.class ),
                uploadField ) );
    }

    private static Contact convert( Account account, VCard card ) {
        Contact contact = new Contact( account );

        N name = (N) card.getProperty( Id.N );
        if ( name != null ) {
            contact.setFamilyName( name.getFamilyName() );
            contact.setGivenName( name.getGivenName() );
            contact.setAdditionalNames( concat( name.getAdditionalNames() ) );
            contact.setPrefixes( concat( name.getPrefixes() ) );
            contact.setSuffixes( concat( name.getSuffixes() ) );
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
            contact.addMedium(
                new OtherMedium(
                    contact, phone.getParameter( Parameter.Id.TYPE ).getValue(), phone.getValue() ) );

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

    private static String concat( String[] strings ) {
        StringBuilder buffer = new StringBuilder();

        for ( String item : strings ) {
            if ( buffer.length() != 0 )
                buffer.append( ", " );
            buffer.append( item );
        }

        return buffer.length() == 0 ? null : buffer.toString();
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Settings";
    }
}
