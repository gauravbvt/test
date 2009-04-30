package com.mindalliance.channels.attachments;

import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.dao.Memory;
import com.mindalliance.channels.query.DataQueryObjectImpl;
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
    private ModelObject object;
    private FileUpload upload;
    private FileItem fileItem;
    private File map;

    public TestFileBasedManager() {
        testFile = new File( getClass().getResource( UPLOAD_TXT ).getFile() );
    }

    @Override
    protected void setUp() throws Exception {
        mgr = new FileBasedManager();
        File directory = new File( System.getProperty( "user.dir" ), "target/upload-test" );
        if ( !directory.exists() )
            directory.mkdir();
        mgr.setDirectory( directory );
        map = new File( mgr.getDirectory(), "index.properties" );


        DataQueryObject dqo = new DataQueryObjectImpl( new Memory() );
        mgr.setDqo( dqo );

        object = dqo.findOrCreate( Scenario.class, "test" );

        fileItem = EasyMock.createMock( FileItem.class );
        upload = new FileUpload( fileItem );
    }

    public void testAttach() throws IOException {
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        String[] filenames = mgr.getDirectory().list();
        assertNotNull( "Test directory does not exist", filenames );
        assertEquals( "Leftovers from previous test errors", 0, filenames.length );

        mgr.attach( object, Attachment.Type.MOU, upload );
        assertEquals( 1, mgr.getDirectory().list().length );
        verify( fileItem );

        Iterator<Attachment> it = mgr.attachments( object );
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
        expect( fileItem.getName() ).andReturn( UPLOAD_TXT );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        expect( fileItem.getInputStream() ).andReturn( new FileInputStream( testFile ) );
        replay( fileItem );

        mgr.attach( object, Attachment.Type.MOU, upload );
        mgr.attach( object, Attachment.Type.Document, upload );
        assertEquals( 2, mgr.getDirectory().list().length );
        verify( fileItem );

        mgr.detachAll( object );
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
        assertTrue( map.exists() );
        map.delete();
    }

    public void testUrl() throws MalformedURLException {
        assertFalse( map.exists() );
        mgr.start();
        assertFalse( mgr.attachments( object ).hasNext() );
        String spec = "http://localhost:8081";
        mgr.attach( object, Attachment.Type.PolicyMust, new URL( spec ) );

        Iterator<Attachment> as = mgr.attachments( object );
        assertTrue( as.hasNext() );
        Attachment a = as.next();
        assertFalse( as.hasNext() );
        assertEquals( spec, a.getUrl() );

        mgr.stop();
        mgr.start();

        Iterator<Attachment> as2 = mgr.attachments( object );
        assertTrue( as2.hasNext() );
        Attachment a2 = as2.next();
        assertFalse( as2.hasNext() );
        assertEquals( spec, a2.getUrl() );

        mgr.stop();
        map.delete();
    }

    public void testRemap() throws MalformedURLException {
        mgr.start();
        assertFalse( mgr.attachments( object ).hasNext() );

        mgr.attach( object, Attachment.Type.Document, new URL( "http://localhost/" ) );

        Map<Long,Long> remap = new HashMap<Long,Long>();
        remap.put( object.getId(), 456L );

        assertTrue( mgr.attachments( object ).hasNext() );

        mgr.remap( remap );
        assertFalse( mgr.attachments( object ).hasNext() );
    }
}
