// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap;

/**
 * Local functionality of an agency.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class LocalAgency implements Agency {

    private Model localModel;
    private Model integratedModel;
    private String name;
    private String shortName;
    private String description;
    private Agencies collaboratingAgencies;

    public final String getDescription() {
        return this.description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public final Model getLocalModel() {
        return this.localModel;
    }

    public void setLocalModel( Model model ) {
        this.localModel = model;
    }

    public final String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public final Agencies getCollaboratingAgencies() {
        return this.collaboratingAgencies;
    }

    public void setCollaboratingAgencies( Agencies collaboratingAgencies ) {
        this.collaboratingAgencies = collaboratingAgencies;
    }

    public final String getShortName() {
        return this.shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    public final Model getIntegratedModel() {
        return this.integratedModel;
    }

    public void setIntegratedModel( Model integratedModel ) {
        this.integratedModel = integratedModel;
    }
}
