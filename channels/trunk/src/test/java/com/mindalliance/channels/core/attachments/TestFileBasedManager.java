package com.mindalliance.channels.core.attachments;

import com.mindalliance.channels.AbstractChannelsTest;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.Upload;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.model.AttachmentImpl;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.upload.FileItem;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ...
 */
@SuppressWarnings( {"HardCodedStringLiteral"} )
public class TestFileBasedManager extends AbstractChannelsTest {

    private FileBasedManager mgr;
    private FileUpload upload;

    public TestFileBasedManager() {
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
        mgr = new FileBasedManager();
        mgr.setPlanManager( planManager );
        File directory = new File( System.getProperty( "user.dir" ),
                "target" + System.getProperty( "file.separator" ) + "upload-test" );
        if ( !directory.exists() )
            directory.mkdir();
        FileItem fileItem = mock( FileItem.class );
        when( fileItem.getName() ).thenReturn( "test.txt" );
        when( fileItem.getInputStream() ).thenReturn( new ByteArrayInputStream( "Hello".getBytes( "UTF-8" ) ) );
        upload = new FileUpload( fileItem );
    }

    @Test
    public void testUpload() {
        Attachment attachment = mgr.upload( User.current().getPlan(),
                                            new Upload() {
                                                @Override
                                                public Type getSelectedType() {
                                                    return Type.Reference;
                                                }

                                                @Override
                                                public String getName() {
                                                    return "";
                                                }

                                                @Override
                                                public String getFileName() {
                                                    return upload.getClientFileName();
                                                }

                                                @Override
                                                public InputStream getInputStream() throws IOException {
                                                    return upload.getInputStream();
                                                }
                                            } );
        assertNotNull( attachment );
        assertEquals( attachment.getUrl(), "test.txt" );
        assertEquals( attachment.getType(), AttachmentImpl.Type.Reference );
    }

}
