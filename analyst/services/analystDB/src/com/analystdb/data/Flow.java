
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Flow
 *  01/06/2013 20:47:04
 * 
 */
public class Flow {

    private Long id;
    private Interview interview;
    private Documents documents;
    private String fromActor;
    private String toActor;
    private String name;
    private String description;
    private Integer version;
    private Set<com.analystdb.data.IssueCommentFlows> issueCommentFlowses = new HashSet<com.analystdb.data.IssueCommentFlows>();
    private Set<com.analystdb.data.FlowAttribute> flowAttributes = new HashSet<com.analystdb.data.FlowAttribute>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }

    public String getFromActor() {
        return fromActor;
    }

    public void setFromActor(String fromActor) {
        this.fromActor = fromActor;
    }

    public String getToActor() {
        return toActor;
    }

    public void setToActor(String toActor) {
        this.toActor = toActor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<com.analystdb.data.IssueCommentFlows> getIssueCommentFlowses() {
        return issueCommentFlowses;
    }

    public void setIssueCommentFlowses(Set<com.analystdb.data.IssueCommentFlows> issueCommentFlowses) {
        this.issueCommentFlowses = issueCommentFlowses;
    }

    public Set<com.analystdb.data.FlowAttribute> getFlowAttributes() {
        return flowAttributes;
    }

    public void setFlowAttributes(Set<com.analystdb.data.FlowAttribute> flowAttributes) {
        this.flowAttributes = flowAttributes;
    }

}