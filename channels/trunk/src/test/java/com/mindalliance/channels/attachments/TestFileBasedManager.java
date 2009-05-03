package com.mindalliance.channels.attachments;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.upload.FileItem;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ...
 */
@SuppressWarnings( { "HardCodedStringLiteral" } )
public class TestFileBasedManager extends TestCase {

    private static final String UPLOAD_TXT = "upload.txt";

    private FileBasedManager mgr;
    private File testFile;
    private FileUpload upload;
    private FileItem fileItem;
    private File map;
    private long id;

    public TestFileBasedManager() {
        testFile = new File( getClass().getResource( UPLOAD_TXT ).getFile() );
    }

    @Override
    protected void setUp() throws Exception {
        mgr = new FileBasedManager();
        File directory = new File( System.getProperty( "user.dir" ),
                                "target" + System.getProperty( "file.separator" )+ "upload-test" );
        if ( !directory.exists() )
            directory.mkdir();
        mgr.setDirectory( directory );
        map = new File( mgr.getDirectory(), mgr.getMapFileName() );

        fileItem = EasyMock.createMock( FileItem.class );
        upload = new FileUpload( fileItem );
        id = 123L;
    }

    public void testAttach() throws IOException {
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        String[] filenames = mgr.getDirectory().list();
        assertNotNull( "Test directory does not exist", filenames );
        assertEquals( "Leftovers from previous test errors", 0, filenames.length );
        mgr.attach( id, Attachment.Type.MOU, upload );
        assertEquals( 2, mgr.getDirectory().list().length );
        verify( fileItem );

        Iterator<Attachment> it = mgr.attachments( id );
        assertTrue( it.hasNext() );
        FileAttachment fa = (FileAttachment) it.next();
        assertFalse( it.hasNext() );
        assertEquals( testFile.length(), fa.getFile().length() );
        assertSame( Attachment.Type.MOU, fa.getType() );
        assertEquals( UPLOAD_TXT, fa.getLabel() );

        mgr.detach( id, fa );
        map.delete();
        assertEquals( 0, mgr.getDirectory().list().length );
    }

    public void testDetachAll() throws IOException {
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        mgr.attach( id, Attachment.Type.MOU, upload );
        mgr.attach( id, Attachment.Type.Document, upload );
        assertEquals( 3, mgr.getDirectory().list().length );
        verify( fileItem );

        mgr.detachAll( id );
        map.delete();
        assertEquals( 0, mgr.getDirectory().list().length );
    }

    public void testStartStop() {
        assertFalse( map.exists() );
        assertFalse( mgr.isRunning() );
        mgr.start();
        assertTrue( mgr.isRunning() );
        assertFalse( map.exists() );
        mgr.stop();
        assertFalse( mgr.isRunning() );
    }

    public void testUrl() throws MalformedURLException {
        assertFalse( map.exists() );
        mgr.start();

        assertFalse( mgr.attachments( id ).hasNext() );
        String spec = "http://localhost:8081";
        mgr.attach( id, Attachment.Type.PolicyMust, new URL( spec ) );

        Iterator<Attachment> as = mgr.attachments( id );
        assertTrue( as.hasNext() );
        Attachment a = as.next();
        assertFalse( as.hasNext() );
        assertEquals( spec, a.getUrl() );

        mgr.stop();
        mgr.start();

        Iterator<Attachment> as2 = mgr.attachments( id );
        assertTrue( as2.hasNext() );
        Attachment a2 = as2.next();
        assertFalse( as2.hasNext() );
        assertEquals( spec, a2.getUrl() );

        mgr.stop();
        map.delete();
    }

    public void testRemap() throws MalformedURLException {
        mgr.start();

        assertFalse( mgr.attachments( id ).hasNext() );

        mgr.attach( id, Attachment.Type.Document, new URL( "http://localhost/" ) );

        Map<Long,Long> remap = new HashMap<Long,Long>();
        remap.put( id, 456L );

        assertTrue( mgr.attachments( id ).hasNext() );

        mgr.remap( remap );
        assertFalse( mgr.attachments( id ).hasNext() );
        map.delete();
    }
}
