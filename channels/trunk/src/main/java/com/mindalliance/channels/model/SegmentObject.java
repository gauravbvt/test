package com.mindalliance.channels.model;

/**
 * A model object in the scope of a plan segment.
 */
public interface SegmentObject extends Identifiable {

    /**
     * Get the model object's containing segment.
     *
     * @return a plan segment
     */
    Segment getSegment();

    String getTitle();
}
