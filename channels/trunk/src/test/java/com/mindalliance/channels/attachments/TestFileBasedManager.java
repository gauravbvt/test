package com.mindalliance.channels.attachments;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Scenario;
import junit.framework.TestCase;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.upload.FileItem;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * ...
 */
public class TestFileBasedManager extends TestCase {

    private static final String UPLOAD_TXT = "upload.txt";

    private FileBasedManager mgr;
    private File testFile;
    private ModelObject object;
    private FileUpload upload;
    private FileItem fileItem;

    public TestFileBasedManager() {
        testFile = new File( getClass().getResource( UPLOAD_TXT ).getFile() );
    }

    @Override
    protected void setUp() throws Exception {
        mgr = new FileBasedManager();
        final File directory = new File( System.getProperty( "user.dir" ), "target/upload-test" );
        if ( !directory.exists() )
            directory.mkdir();
        mgr.setDirectory( directory );

        object = new Scenario();
        object.setId( 123L );

        fileItem = EasyMock.createMock( FileItem.class );
        upload = new FileUpload( fileItem );
    }

    public void testAttach() throws IOException {
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        final String[] filenames = mgr.getDirectory().list();
        assertNotNull( "Test directory does not exist", filenames );
        assertEquals( 0, filenames.length );

        mgr.attach( object, Attachment.Type.MOU, upload );
        assertEquals( 1, mgr.getDirectory().list().length );
        verify( fileItem );

        final Iterator<Attachment> it = mgr.attachments( object );
        assertTrue( it.hasNext() );
        FileAttachment fa = (FileAttachment) it.next();
        assertFalse( it.hasNext() );
        assertEquals( testFile.length(), fa.getFile().length() );
        assertSame( Attachment.Type.MOU, fa.getType() );
        assertEquals( UPLOAD_TXT, fa.getLabel() );

        mgr.detach( object, fa );
        assertEquals( 0, mgr.getDirectory().list().length );

    }

    public void testDetachAll() throws IOException {
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        mgr.attach( object, Attachment.Type.MOU, upload );
        assertEquals( 1, mgr.getDirectory().list().length );
        verify( fileItem );

        mgr.detachAll( object );
        assertEquals( 0, mgr.getDirectory().list().length );
    }
}
