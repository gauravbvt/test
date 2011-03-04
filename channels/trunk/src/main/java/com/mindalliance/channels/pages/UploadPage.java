package com.mindalliance.channels.pages;

import com.mindalliance.channels.attachments.FileBasedManager;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Simple file uploader.
 */
public class UploadPage extends AbstractImageFilePage {

    @SpringBean
    private FileBasedManager attachmentManager;


    public UploadPage( PageParameters parameters ) {
        super( parameters );
    }

    protected String decodeFileName( String fileName ) {
        try {
            return URLDecoder.decode( fileName, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    protected File getFile( String fileName ) {
        return new File( attachmentManager.getUploadDirectory( getPlan() ), fileName );
    }

}
