package com.mindalliance.channels.core.model;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/14
 * Time: 4:49 PM
 */
public class AbstractIdentifiable implements Identifiable {

    public AbstractIdentifiable() {
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return "?";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    @Override
    public String getClassLabel() {
        return "?";
    }

    @Override
    public String getKindLabel() {
        return "?";
    }

    @Override
    public String getUid() {
        return "?";
    }

    @Override
    public String getName() {
        return "?";
    }
}
