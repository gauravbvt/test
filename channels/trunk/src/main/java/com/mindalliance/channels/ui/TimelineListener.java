// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import com.mindalliance.channels.model.Occurence;

/**
 * Listener to selections in a scenario timeline.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public interface TimelineListener {

    /**
     * The selection in the timeline changed.
     *
     * @param timeline the source of the selection event
     * @param oldSelection the previous selection (can be null)
     * @param newSelection the new selection (can also be null)
     */
    void selectionChanged(
            ScenarioTimeline timeline,
            Occurence oldSelection, Occurence newSelection );

}
