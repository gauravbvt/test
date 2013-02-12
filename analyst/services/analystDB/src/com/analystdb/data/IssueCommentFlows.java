
package com.analystdb.data;



/**
 *  analystDB.IssueCommentFlows
 *  02/10/2013 19:29:36
 * 
 */
public class IssueCommentFlows {

    private IssueCommentFlowsId id;
    private IssueComment issueComment;
    private Flow flow;

    public IssueCommentFlowsId getId() {
        return id;
    }

    public void setId(IssueCommentFlowsId id) {
        this.id = id;
    }

    public IssueComment getIssueComment() {
        return issueComment;
    }

    public void setIssueComment(IssueComment issueComment) {
        this.issueComment = issueComment;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

}
