package com.mindalliance.channels.playbook.tests.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.Item;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.List;
import java.util.Date;
import java.text.DateFormat;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 8:31:00 PM
 */
public class SomePage extends Template {

    private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    public SomePage(final PageParameters parameters) {
        super(parameters);

        PlaybookSession session = (PlaybookSession) getSession();

            if (session.getProject() == null) session.authenticate("admin", "admin"); // needed in test

            Ref p = session.getProject();

            add(new Label("title", "PlaybookPage"));

            // Add scenarios to the list
            add(new ListView("sc-list", (List)p.deref("scenarios")) {
                public void populateItem(ListItem listItem) {
                  listItem.add(
                        new Label("sc-item",
                                new RefPropertyModel(listItem.getModelObject(), "name")));
                }
            }
            );

            // Add resources
        add(new ListView("r-list", (List)p.deref("resources")) {
            public void populateItem(ListItem listItem) {
              listItem.add(
                    new Label("r-item",
                            new RefPropertyModel(listItem.getModelObject(), "name")));
            }
        }
        );
             // Add todos
            final DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.SHORT, session.getLocale());

        add(new DataView("todo", new RefDataProvider(session, "participation.todos") ) {
            public void populateItem(Item item) {
               Ref t = (Ref) item.getModelObject();
                    item.add(new Label("todo-name", (String) t.deref("description")));
                    item.add(new Label("todo-priority", (String) t.deref("priority")));
                    item.add(new Label("todo-due", dateFormat.format((Date) t.deref("due"))));
            }
        }
        );
    }

}
