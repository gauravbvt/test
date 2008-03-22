package com.mindalliance.channels.playbook.pages

import org.apache.wicket.PageParameters
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookSession
import org.apache.wicket.markup.html.basic.Label
import java.text.DateFormat
import com.mindalliance.channels.playbook.mem.SessionCategory
import com.mindalliance.channels.playbook.support.models.RefPropertyModel

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:04:05 AM
*/
class HomePage extends Template {

    private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    HomePage(final PageParameters parameters) {
        super(parameters)

        PlaybookSession session = (PlaybookSession) getSession()
        use(SessionCategory) {

            if (!session.project) session.authenticate('admin', 'admin') // TODO remove - needed in test

            Ref p = session.project
            assert p
            List todos = session.participation ? session.participation.todos : []

            add(new Label("title", "Playbook"))

            // Add scenarios to the list
            add(listView("sc-list", p.scenarios, {listItem ->
                listItem.add(
                        new Label("sc-item",
                                new RefPropertyModel(listItem.getModelObject(), "name")))}
            ))

            // Add resources
            add(listView("r-list", p.resources, {listItem ->
                listItem.add(
                        new Label("r-item",
                                new RefPropertyModel(listItem.getModelObject(), "name")))}
            ))

            // Add todos
            final DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.SHORT, session.getLocale());
            
            add(dataView('todo', todos, {item ->
                use(SessionCategory) {
                    Ref t = (Ref) item.getModelObject();
                    item.add(new Label("todo-name", (String) t.description))
                    item.add(new Label("todo-priority", (String) t.priority))
                    item.add(new Label("todo-due", dateFormat.format((Date) t.due)))
                }
            }))
        }
    }

}