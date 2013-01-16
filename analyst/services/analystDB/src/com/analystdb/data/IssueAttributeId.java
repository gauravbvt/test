
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.IssueAttributeId
 *  12/30/2012 05:53:59
 * 
 */
public class IssueAttributeId
    implements Serializable
{

    private Long id;
    private Long issue;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IssueAttributeId)) {
            return false;
        }
        IssueAttributeId other = ((IssueAttributeId) o);
        if (this.id == null) {
            if (other.id!= null) {
                return false;
            }
        } else {
            if (!this.id.equals(other.id)) {
                return false;
            }
        }
        if (this.issue == null) {
            if (other.issue!= null) {
                return false;
            }
        } else {
            if (!this.issue.equals(other.issue)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.id!= null) {
            rtn = (rtn + this.id.hashCode());
        }
        rtn = (rtn* 37);
        if (this.issue!= null) {
            rtn = (rtn + this.issue.hashCode());
        }
        return rtn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssue() {
        return issue;
    }

    public void setIssue(Long issue) {
        this.issue = issue;
    }

}
