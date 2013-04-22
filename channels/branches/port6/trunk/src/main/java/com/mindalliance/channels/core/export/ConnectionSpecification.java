package com.mindalliance.channels.core.export;

import java.io.Serializable;

/**
 * Specification of a connection.
 * Either from a local connector to an external part,
 * or to an external connector itself in inner flow with a part.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2009
 * Time: 7:41:35 PM
 */
public class ConnectionSpecification implements Serializable {

    /**
     * Segment of an external (portal) connector, or connected external part (connected to proxy connector).
     */
    private SegmentSpecification segmentSpecification;
    /**
     * Specification of part connecting to external connector, or of connected external part.
     */
    private PartSpecification partSpecification;
    /**
     * Name of flow between external connector and part, or of external flow to/from part.
     */
    private String flowName;
    /**
     * Proxy connector is source, or external connector is source.
     */
    private boolean isSource;
    /**
     * External flow id.
     */
    private Long externalFlowId;
    /**
     * The name of the restriction.
     */
    private String restriction;
    /**
     * Whether confirmation of receipt is requested.
     */
    private boolean receiptConfirmationRequested;
    /**
     * Whether an intermediate can be bypassed.
     */
    private boolean canBypassIntermediate;

    public ConnectionSpecification() {
    }

    public SegmentSpecification getSegmentSpecification() {
        return segmentSpecification;
    }

    public void setSegmentSpecification( SegmentSpecification segmentSpecification ) {
        this.segmentSpecification = segmentSpecification;
    }

    public PartSpecification getPartSpecification() {
        return partSpecification;
    }

    public void setPartSpecification( PartSpecification partSpecification ) {
        this.partSpecification = partSpecification;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName( String flowName ) {
        this.flowName = flowName;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource( boolean source ) {
        isSource = source;
    }

    public Long getExternalFlowId() {
        return externalFlowId;
    }

    public void setExternalFlowId( Long externalFlowId ) {
        this.externalFlowId = externalFlowId;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction( String restriction ) {
        this.restriction = restriction;
    }

    public boolean isReceiptConfirmationRequested() {
        return receiptConfirmationRequested;
    }

    public void setReceiptConfirmationRequested( boolean receiptConfirmationRequested ) {
        this.receiptConfirmationRequested = receiptConfirmationRequested;
    }

    public boolean isCanBypassIntermediate() {
        return canBypassIntermediate;
    }

    public void setCanBypassIntermediate( boolean canBypassIntermediate ) {
        this.canBypassIntermediate = canBypassIntermediate;
    }
}
