package com.mindalliance.channels.pages;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * An updatable page or component.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 25, 2009
 * Time: 3:53:54 PM
 */
public interface Updatable {

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
     * Get query service not from parent.
     * @return a query service
     */
    QueryService getOwnQueryService();

}
