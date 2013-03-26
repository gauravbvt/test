
package com.analystdb.data;



/**
 *  analystDB.DecisionFlow
 *  03/26/2013 11:09:00
 * 
 */
public class DecisionFlow {

    private DecisionFlowId id;
    private Flow flow;
    private Decision decision;

    public DecisionFlowId getId() {
        return id;
    }

    public void setId(DecisionFlowId id) {
        this.id = id;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

}
