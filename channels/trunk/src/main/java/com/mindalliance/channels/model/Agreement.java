package com.mindalliance.channels.model;

/**
 * An agreed-to commitment to share information.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 28, 2009
 * Time: 10:59:27 AM
 */
public class Agreement {

    /**
     * Employment that is the beneficiary of the agreement.
     */
    private Employment beneficiary;
    /**
     * Name of information.
     */
    private String information;
    /**
     * Elements of information.
     */
    private String eois;
    /**
     * Task for which info can be used.
     */
    private String task;

    public Agreement() {}

    public Employment getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary( Employment beneficiary ) {
        this.beneficiary = beneficiary;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation( String information ) {
        this.information = information;
    }

    public String getEois() {
        return eois;
    }

    public void setEois( String eois ) {
        this.eois = eois;
    }

    public String getTask() {
        return task;
    }

    public void setTask( String task ) {
        this.task = task;
    }
}
