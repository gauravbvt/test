package com.mindalliance.channels.model;

import java.util.List;

/**
 * That which has tags.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/27/11
 * Time: 1:20 PM
 */
public interface Taggable extends Identifiable {

    List<Tag> getTags();

    void setTags( String s );

}
