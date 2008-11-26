package com.mindalliance.channels.analysis;

import com.mindalliance.channels.model.ModelObject;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator on issues about a ModelObject, possibly specific to a given property
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 26, 2008
 * Time: 4:06:56 PM
 */
public class IssueIterator implements Iterator<Issue> {

    /**
     * The model object with potential issues
     */
    private ModelObject modelObject;
    /**
     * The property with issues (optional)
     */
    private String property;
    /**
     * An iterator on the issue detectors
     */
    private Iterator<IssueDetector> detectors;
    /**
     * The next issue
     */
    private Issue currentIssue;

    public IssueIterator( List<IssueDetector> detectors, ModelObject modelObject, String property) {
        this.detectors = detectors.iterator();
        this.modelObject = modelObject;
        this.property = property;
    }

    public IssueIterator(List<IssueDetector> detectors, ModelObject modelObject) {
        this(detectors, modelObject, null);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        findCurrentIssue();
        return currentIssue != null;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    public Issue next() {
        findCurrentIssue();
        Issue issue = currentIssue;
        currentIssue = null;
        return issue;
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt>
     *                                       operation is not supported by this Iterator.
     * @throws IllegalStateException         if the <tt>next</tt> method has not
     *                                       yet been called, or the <tt>remove</tt> method has already
     *                                       been called after the last call to the <tt>next</tt>
     *                                       method.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Finds the current issue if any
     */
    private void findCurrentIssue() {
        while (currentIssue == null && detectors.hasNext()) {
            IssueDetector detector = detectors.next();
            if (property != null) {
               if (detector.appliesTo( modelObject, property )) {
                   currentIssue = detector.detectIssue( modelObject );
               }
            }
            else {
                if (detector.appliesTo( modelObject )) {
                    currentIssue = detector.detectIssue( modelObject );
                }
            }
        }
    }
}
