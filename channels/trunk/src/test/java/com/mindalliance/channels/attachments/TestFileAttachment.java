package com.mindalliance.channels.attachments;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.dao.Memory;
import junit.framework.TestCase;

import java.io.File;

/**
 * ...
 */
public class TestFileAttachment extends TestCase {

    private Dao dao;
    private ModelObject object;

    public TestFileAttachment() {
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        dao = new Memory();
        object = dao.createScenario();
    }

    public void testCreateFileName() throws NotFoundException {
        final String label = "some/bizarely\\named%file.txt";

        for ( Attachment.Type type : Attachment.Type.values() ) {
            final File f = new File( FileAttachment.createFileName( object, type, label ) );
            assertSame( type, FileAttachment.typeFrom( f ) );
            assertEquals( label, FileAttachment.labelFrom( f ) );
            assertSame( object, FileAttachment.objectFrom( dao, f ) );
        }
    }

    public void testConstructor() throws NotFoundException {
        final Attachment.Type type = Attachment.Type.Issue;
        final String label = "a problem description.doc";
        final File f = new File( FileAttachment.createFileName( object, type, label ) );

        final FileAttachment fa = new FileAttachment( dao, f );

        assertSame( f, fa.getFile() );
        assertSame( type, fa.getType() );
        assertSame( object, fa.getObject() );
        assertEquals( label, fa.getLabel() );
    }

    public void testLink() {
        final FileAttachment fa = new FileAttachment();
        assertNull( fa.getLink() );
        final String s = "bla";
        fa.setLink( s );
        assertSame( s, fa.getLink() );
    }
}
