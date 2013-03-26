
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.IssueApproachId
 *  03/26/2013 11:09:00
 * 
 */
public class IssueApproachId
    implements Serializable
{

    private Long issueId;
    private Long approachId;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IssueApproachId)) {
            return false;
        }
        IssueApproachId other = ((IssueApproachId) o);
        if (this.issueId == null) {
            if (other.issueId!= null) {
                return false;
            }
        } else {
            if (!this.issueId.equals(other.issueId)) {
                return false;
            }
        }
        if (this.approachId == null) {
            if (other.approachId!= null) {
                return false;
            }
        } else {
            if (!this.approachId.equals(other.approachId)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.issueId!= null) {
            rtn = (rtn + this.issueId.hashCode());
        }
        rtn = (rtn* 37);
        if (this.approachId!= null) {
            rtn = (rtn + this.approachId.hashCode());
        }
        return rtn;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getApproachId() {
        return approachId;
    }

    public void setApproachId(Long approachId) {
        this.approachId = approachId;
    }

}
