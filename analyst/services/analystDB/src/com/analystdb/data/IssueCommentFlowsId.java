
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.IssueCommentFlowsId
 *  03/26/2013 11:09:00
 * 
 */
public class IssueCommentFlowsId
    implements Serializable
{

    private Long issues;
    private Long flows;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IssueCommentFlowsId)) {
            return false;
        }
        IssueCommentFlowsId other = ((IssueCommentFlowsId) o);
        if (this.issues == null) {
            if (other.issues!= null) {
                return false;
            }
        } else {
            if (!this.issues.equals(other.issues)) {
                return false;
            }
        }
        if (this.flows == null) {
            if (other.flows!= null) {
                return false;
            }
        } else {
            if (!this.flows.equals(other.flows)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.issues!= null) {
            rtn = (rtn + this.issues.hashCode());
        }
        rtn = (rtn* 37);
        if (this.flows!= null) {
            rtn = (rtn + this.flows.hashCode());
        }
        return rtn;
    }

    public Long getIssues() {
        return issues;
    }

    public void setIssues(Long issues) {
        this.issues = issues;
    }

    public Long getFlows() {
        return flows;
    }

    public void setFlows(Long flows) {
        this.flows = flows;
    }

}
