package com.mindalliance.channels.playbook.pages

import org.apache.wicket.PageParameters
import com.mindalliance.channels.playbook.ref.Reference
import com.mindalliance.channels.playbook.support.PlaybookSession
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.PropertyModel
import java.text.DateFormat
import org.apache.wicket.markup.repeater.data.ListDataProvider
import com.mindalliance.channels.playbook.mem.SessionCategory
import org.apache.wicket.markup.repeater.data.DataView
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
        super( parameters )

        PlaybookSession session = (PlaybookSession) getSession()

        use(SessionCategory) {
            session.authenticate('admin', 'admin') // TODO remove - needed in test

            Reference p = session.project
            assert p

            List todos = session.participation ? session.participation.todos : []

            add(new Label("title", "Playbook"))

            // Add scenarios to the list
            def scenariosListView = proxyClass(
                    ListView.class,
                    ["sc-list", p.scenarios],
                    ['populateItem': {listItem ->
                         listItem.add(
                            new Label("sc-item",
                                    new RefPropertyModel(listItem.getModelObject(), "name")))
                    }]
            )
            add(scenariosListView)

            /*add(new ListView("sc-list", (List) p.scenarios)
            {


                protected void populateItem(ListItem listItem) {
                    listItem.add(
                            new Label("sc-item",
                                    new PropertyModel(listItem.getModelObject(), "name")))
                }
            });*/

            // Add resources
            def resourcesListView = proxyClass(
                    ListView.class,
                    ["r-list", (List) p.resources],
                    ['populateItem': {listItem ->
                         listItem.add(
                            new Label("r-item",
                                    new RefPropertyModel(listItem.getModelObject(), "name")))
                    }]
            )
            add(resourcesListView)

           /* add(new ListView("r-list", (List) p.resources) {
                protected void populateItem(ListItem listItem) {
                    listItem.add(
                            new Label("r-item",
                                    new PropertyModel(listItem.getModelObject(), "name")));
                }
            });
*/
            // Add todos
            final DateFormat dateFormat =
                DateFormat.getDateInstance(DateFormat.SHORT, session.getLocale());

            def todoDataView = proxyClass(
                    DataView.class,
                    ["todo", new ListDataProvider(todos)],
                    ['populateItem': {item ->
                        Reference t = (Reference)item.getModelObject();
                        item.add(new Label("todo-name", (String)t.description))
                        item.add(new Label("todo-priority", (String)t.priority))
                        item.add(new Label("todo-due", dateFormat.format((Date)t.due)))
                    }]
            )
            add(todoDataView)

            /*add(new DataView("todo", new ListDataProvider(todos)) {
                protected void populateItem(Item item) {
                    Todo t = (Todo) item.getModelObject();
                    item.add(new Label("todo-name", t.getDescription()));
                    item.add(new Label("todo-priority", t.getPriority()));
                    item.add(new Label("todo-due", dateFormat.format(t.getDue())));
                }
            });*/
        }
    }

}