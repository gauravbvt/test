// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.dao.StepDao.Status;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.ConfirmationAck;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Step;

import java.io.Serializable;

/**
 * Extended status information about a step.
 */
public interface StepInformation extends Serializable {

    /**
     * Get the ID of the play of the step.
     * @return the id
     */
    long getPlayId();

    /**
     * The step of concern.
     * @return a step
     */
    Step getStep();

    /**
     * The confirmation request, if one is pending.
     * @return null if unconfirmed
     */
    ConfirmationReq getReq();

    /**
     * The acknowledgement to the request, if there is one.
     * @return null if unconfirmed or pending
     */
    ConfirmationAck getAck();

    /**
     * The status of the step.
     * @return the status
     */
    Status getStatus();

    /**
     * @return true if step needs to be confirmed
     */
    boolean isConfirmable();

    /**
     * @return acknowledgement message if there was one, null otherwise
     */
    String getAckMessage();

    /**
     * @return the account defining the step
     */
    Account getAccount();

    /**
     * @return true when the step is a collaboration
     */
    boolean isCollaboration();
}
