/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.user.Opinion;
import com.mindalliance.channels.User;

// The version of an Element including its then state (frozen, thawed,
// deleted)
public class Version implements Serializable {

    private Element clone; // version of the element per se, unless
                            // deleted or frozen
    private Date when;
    private User who;
    private boolean deleted = false;
    private boolean frozen = false;
    private List<Opinion> opinions;

    /**
     * @return the clone
     */
    public Element getClone() {
        return clone;
    }

    /**
     * @param clone the clone to set
     */
    public void setClone( Element clone ) {
        this.clone = clone;
    }

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted( boolean deleted ) {
        this.deleted = deleted;
    }

    /**
     * @return the frozen
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * @param frozen the frozen to set
     */
    public void setFrozen( boolean frozen ) {
        this.frozen = frozen;
    }

    /**
     * @return the opinions
     */
    public List<Opinion> getOpinions() {
        return opinions;
    }

    /**
     * @param opinions the opinions to set
     */
    public void setOpinions( List<Opinion> opinions ) {
        this.opinions = opinions;
    }

    /**
     * @param opinion
     */
    public void addOpinion( Opinion opinion ) {
        opinions.add( opinion );
    }

    /**
     * @param opinion
     */
    public void removeOpinion( Opinion opinion ) {
        opinions.remove( opinion );
    }

    /**
     * @return the when
     */
    public Date getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen( Date when ) {
        this.when = when;
    }

    /**
     * @return the who
     */
    public User getWho() {
        return who;
    }

    /**
     * @param who the who to set
     */
    public void setWho( User who ) {
        this.who = who;
    }

}
