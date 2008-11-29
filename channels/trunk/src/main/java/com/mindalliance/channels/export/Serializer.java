package com.mindalliance.channels.export;

import com.mindalliance.channels.Scenario;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Import/Export scenarios using serialization.
 */
public class Serializer implements Importer, Exporter {

    private static final String IMPORT_FAILED = "Import failed";

    public Serializer() {
    }

    /** {@inheritDoc} */
    public Scenario importScenario( InputStream stream ) throws IOException {
        try {
            return (Scenario) new ObjectInputStream( stream ).readObject();

        } catch ( ClassNotFoundException e ) {
            LogFactory.getLog( getClass() ).error( IMPORT_FAILED, e );
            throw new IOException( IMPORT_FAILED, e );
        }
    }

    /** {@inheritDoc} */
    public void exportScenario( Scenario scenario, OutputStream stream ) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream( stream );
        oos.writeObject( scenario );
        oos.flush();
    }
}
