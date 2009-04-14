package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.pages.Project;

/**
 * Abstract XStream converter base class for Channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 13, 2009
 * Time: 3:52:08 PM
 */
public abstract class AbstractChannelsConverter implements Converter {
    /**
     * Get Data Query Object
     * @return
     */
    protected DataQueryObject getDqo() {
        return Project.getProject().getDqo();
    }
}
