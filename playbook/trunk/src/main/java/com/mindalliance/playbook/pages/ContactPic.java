package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Contact;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Show the picture of a contact, if there is one.
 */
public class ContactPic extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger( ContactPic.class );

    @SpringBean
    Account account;
    
    @SpringBean
    ContactDao contactDao;

    public ContactPic( PageParameters parameters ) {
        super( parameters );
    }

    private Contact getContact() {
        StringValue id = getPageParameters().get( "id" );
        if ( id.isNull() )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_NOT_FOUND,
                "Not Found" );

        Contact contact = contactDao.load( id.toLong() );
        if ( contact == null )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_NOT_FOUND,
                "Not Found" );

        // TODO fix protection
//        if ( contact.getAccount().getId() != account.getId() )
//            if ( contact == null )
//                throw new AbortWithHttpErrorCodeException(
//                    HttpServletResponse.SC_FORBIDDEN,
//                    "Unauthorized" );

        if ( contact.getPhoto() == null )
            throw new AbortWithHttpErrorCodeException(
                HttpServletResponse.SC_NOT_FOUND,
                "Not Found" );
        
        return contact;
    }

    @Override
    public void renderPage() {
        Contact contact = getContact();
        byte[] photo = contact.getPhoto();

        WebResponse response = (WebResponse) getResponse();

        LOG.debug( "Rendering photo for contact {}", contact );
        response.setLastModifiedTime( Time.valueOf( account.getLastModified() ) );
        response.setContentLength( (long) photo.length );
        response.setContentType( "image/jpeg" );

        response.write( photo );
    }
    
}
