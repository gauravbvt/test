
package com.analystdb.data;

import java.util.HashSet;
import java.util.Set;


/**
 *  analystDB.Documents
 *  02/10/2013 19:29:36
 * 
 */
public class Documents {

    private Integer id;
    private DocumentCategory documentCategory;
    private String document;
    private Set<com.analystdb.data.Flow> flows = new HashSet<com.analystdb.data.Flow>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DocumentCategory getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(DocumentCategory documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Set<com.analystdb.data.Flow> getFlows() {
        return flows;
    }

    public void setFlows(Set<com.analystdb.data.Flow> flows) {
        this.flows = flows;
    }

}
