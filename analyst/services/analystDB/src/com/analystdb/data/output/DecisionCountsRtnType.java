
package com.analystdb.data.output;

import com.analystdb.data.Decision;


/**
 * Generated for query "decisionCounts" on 04/04/2013 08:31:55
 * 
 */
public class DecisionCountsRtnType {

    private Decision decision;
    private Long flows;
    private Long issues;
    private Long approaches;

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Long getFlows() {
        return flows;
    }

    public void setFlows(Long flows) {
        this.flows = flows;
    }

    public Long getIssues() {
        return issues;
    }

    public void setIssues(Long issues) {
        this.issues = issues;
    }

    public Long getApproaches() {
        return approaches;
    }

    public void setApproaches(Long approaches) {
        this.approaches = approaches;
    }

}
