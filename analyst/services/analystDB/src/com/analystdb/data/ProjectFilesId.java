
package com.analystdb.data;

import java.io.Serializable;


/**
 *  analystDB.ProjectFilesId
 *  12/30/2012 05:53:59
 * 
 */
public class ProjectFilesId
    implements Serializable
{

    private Integer name;
    private Long project;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProjectFilesId)) {
            return false;
        }
        ProjectFilesId other = ((ProjectFilesId) o);
        if (this.name == null) {
            if (other.name!= null) {
                return false;
            }
        } else {
            if (!this.name.equals(other.name)) {
                return false;
            }
        }
        if (this.project == null) {
            if (other.project!= null) {
                return false;
            }
        } else {
            if (!this.project.equals(other.project)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.name!= null) {
            rtn = (rtn + this.name.hashCode());
        }
        rtn = (rtn* 37);
        if (this.project!= null) {
            rtn = (rtn + this.project.hashCode());
        }
        return rtn;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

}
