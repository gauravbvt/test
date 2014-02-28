/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * An updatable page or component.
 */
public interface Updatable {

    /**
     * Replace the default update target with a given one.
     *
     * @param updatable an updatable
     */
    void setUpdateTarget( Updatable updatable );

    /**
     * An identifiable object  was changed; change state accordingly.
     * This is in anticipation of receiving an "updateWith" message.
     *
     * @param change the nature of the change
     */
    void changed( Change change );

    /**
     * An identifiable object  was changed; update UI components.
     * Always follows a corresponding "changed" message.
     * Meant to percolate up the parent chain.
     *
     * @param target  the ajax target
     * @param change  the nature of the change
     * @param updated list of already updated updatables
     */
    void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated );

    /**
     * Take an action on an object.
     * Does not percolate up the parent chain.
     *
     * @param target the ajax target
     * @param object an object
     * @param action action taken on object
     */
    void update( AjaxRequestTarget target, Object object, String action );

    /**
     * Refresh due to change if not already in list of updated.
     * Meant to sink down the children tree.
     *
     * @param target  the ajax request target
     * @param change  the nature of tha change
     * @param updated the list of already updated updatables
     * @param aspect  aspect shown
     */
    void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect );

    /**
     * Refresh due to change if not already in list of updated.
     * Meant to sink down the children tree.
     *
     * @param target  the ajax request target
     * @param change  the nature of tha change
     * @param updated the list of already updated updatables
     */
    void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated );

    /**
     * Refresh due to change..
     * Meant to sink down the children tree.
     *
     * @param target the ajax request target
     * @param change the nature of tha change
     */
    void refresh( AjaxRequestTarget target, Change change );

    /**
     * Get query service.
     *
     * @return a query service
     */
    QueryService getQueryService();

    /**
     * Get the plan of the page or panel.
     *
     * @return a plan
     */
    CollaborationModel getCollaborationModel();

    /**
     * Get current user.
     *
     * @return a user
     */
    ChannelsUser getUser();
}
