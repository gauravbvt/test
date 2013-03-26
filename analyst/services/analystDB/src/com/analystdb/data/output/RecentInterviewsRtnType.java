
package com.analystdb.data.output;

import com.analystdb.data.Interview;


/**
 * Generated for query "recentInterviews" on 03/26/2013 11:09:07
 * 
 */
public class RecentInterviewsRtnType {

    private Long id;
    private String resource;
    private Interview interview;
    private Long approaches;
    private Long flows;
    private Long issues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Long getApproaches() {
        return approaches;
    }

    public void setApproaches(Long approaches) {
        this.approaches = approaches;
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

}
