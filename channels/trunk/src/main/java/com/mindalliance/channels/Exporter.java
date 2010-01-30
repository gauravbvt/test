package com.mindalliance.channels;

import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Segment export mechanism.
 * @see Importer
 */
public interface Exporter extends Service {

    /**
     * Export a segment on the given stream.
     * @param segment the segment
     * @param stream the stream
     * @throws IOException on errors
     */
    void export( Segment segment, OutputStream stream ) throws IOException;

    /**
     * Export all data on the given stream.
     * @param plan the plan to export
     * @param stream the stream
     * @throws IOException on errors
     */
    void export( Plan plan, OutputStream stream ) throws IOException;

    /**
     * Export a journal on the given stream.
     * @param journal a journal
     * @param stream the stream
     * @throws IOException on errors
     */
    void export( Journal journal, OutputStream stream ) throws IOException;

    /**
     * The mime type of files to which segments are exported.
     * @return -- a mime type
     */
    String getMimeType();

    /**
     * Get current version, for upgrading purposes.
     * @return a string
     */
    String getVersion();

}
