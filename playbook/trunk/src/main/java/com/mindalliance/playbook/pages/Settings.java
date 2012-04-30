package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AccountDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.pages.panels.ContactPanel;
import com.mindalliance.playbook.services.ContactMerger;
import com.mindalliance.playbook.services.SocialHub;
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

/**
 * Settings popup.
 */
public class Settings extends NavigablePage {

    private static final Logger LOG = LoggerFactory.getLogger( Settings.class );

    private static final long serialVersionUID = -3349648435696781610L;

    @SpringBean
    private Account account;

    @SpringBean
    private AccountDao accountDao;

    @SpringBean
    private ContactMerger contactMerger;

    @SpringBean
    private SocialHub socialHub;

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
                
                if ( upload != null )
                    try {
                        InputStream inputStream = upload.getInputStream();
                        try {
                            contactMerger.importVCards( inputStream );
                        } finally {
                            inputStream.close();
                        }

                    } catch ( IOException e ) {
                        LOG.error( "Error uploading contact file", e );
                    }

                accountDao.save( account );
                setResponsePage( Settings.class );
            }
        };

        form.setMultiPart( true );

        add( new BookmarkablePageLink( "home", PlaysPage.class ),

             form.add( new Label( "owner.fullName" ),
                       new Label( "contacts.size" ),
                       new CheckBox( "viewByTags" ),
                       new CheckBox( "showInactive" ),
                       new ContactPanel( "playbook.me", new PropertyModel<Contact>( account,
                                                                                    "playbook.me" ) ),
                       uploadField ) );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Settings";
    }
}
