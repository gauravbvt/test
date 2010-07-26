package com.mindalliance.channels.pages;

import com.mindalliance.channels.attachments.FileBasedManager;
import com.mindalliance.channels.dao.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;

/**
 * Simple file uploader.
 */
public class UploadPage extends AbstractImageFilePage {

    @SpringBean
    private FileBasedManager attachmentManager;


    public UploadPage( PageParameters parameters ) {
        super( parameters );
    }

    protected File getFile( String fileName ) {
        return new File( attachmentManager.getUploadDirectory( User.current().getPlan() ), fileName );
    }

}
