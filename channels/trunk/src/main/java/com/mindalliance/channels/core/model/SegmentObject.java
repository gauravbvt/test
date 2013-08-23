package com.mindalliance.channels.core.model;

/**
 * A model object in the scope of a segment.
 */
public interface SegmentObject extends Identifiable {

    /**
     * Get the model object's containing segment.
     *
     * @return a segment
     */
    Segment getSegment();

    String getTitle();
}
