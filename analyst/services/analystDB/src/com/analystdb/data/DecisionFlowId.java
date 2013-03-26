
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.DecisionFlowId
 *  03/26/2013 11:09:00
 * 
 */
public class DecisionFlowId
    implements Serializable
{

    private Integer decision;
    private Long flow;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DecisionFlowId)) {
            return false;
        }
        DecisionFlowId other = ((DecisionFlowId) o);
        if (this.decision == null) {
            if (other.decision!= null) {
                return false;
            }
        } else {
            if (!this.decision.equals(other.decision)) {
                return false;
            }
        }
        if (this.flow == null) {
            if (other.flow!= null) {
                return false;
            }
        } else {
            if (!this.flow.equals(other.flow)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.decision!= null) {
            rtn = (rtn + this.decision.hashCode());
        }
        rtn = (rtn* 37);
        if (this.flow!= null) {
            rtn = (rtn + this.flow.hashCode());
        }
        return rtn;
    }

    public Integer getDecision() {
        return decision;
    }

    public void setDecision(Integer decision) {
        this.decision = decision;
    }

    public Long getFlow() {
        return flow;
    }

    public void setFlow(Long flow) {
        this.flow = flow;
    }

}
