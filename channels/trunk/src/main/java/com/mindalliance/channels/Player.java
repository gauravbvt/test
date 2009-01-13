package com.mindalliance.channels;

import com.mindalliance.channels.analysis.profiling.Play;

import java.util.List;

/**
 * A player is an entity who has plays (plays are derived from flows)
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:37:04 PM
 */
public interface Player {
    /**
     * Find all plays of the player within the current project
     * @return list of plays
     */
    List<Play> findAllPlays();
}
