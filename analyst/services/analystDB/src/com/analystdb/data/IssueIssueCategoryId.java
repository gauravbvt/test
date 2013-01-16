
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.IssueIssueCategoryId
 *  01/06/2013 20:47:04
 * 
 */
public class IssueIssueCategoryId
    implements Serializable
{

    private Long issueId;
    private Long issueCategoryId;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IssueIssueCategoryId)) {
            return false;
        }
        IssueIssueCategoryId other = ((IssueIssueCategoryId) o);
        if (this.issueId == null) {
            if (other.issueId!= null) {
                return false;
            }
        } else {
            if (!this.issueId.equals(other.issueId)) {
                return false;
            }
        }
        if (this.issueCategoryId == null) {
            if (other.issueCategoryId!= null) {
                return false;
            }
        } else {
            if (!this.issueCategoryId.equals(other.issueCategoryId)) {
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
        if (this.issueCategoryId!= null) {
            rtn = (rtn + this.issueCategoryId.hashCode());
        }
        return rtn;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getIssueCategoryId() {
        return issueCategoryId;
    }

    public void setIssueCategoryId(Long issueCategoryId) {
        this.issueCategoryId = issueCategoryId;
    }

}
