
package com.analystdb.data;



/**
 *  analystDB.IssueApproach
 *  03/26/2013 11:09:00
 * 
 */
public class IssueApproach {

    private IssueApproachId id;
    private Issue issue;
    private Approach approach;

    public IssueApproachId getId() {
        return id;
    }

    public void setId(IssueApproachId id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Approach getApproach() {
        return approach;
    }

    public void setApproach(Approach approach) {
        this.approach = approach;
    }

}
