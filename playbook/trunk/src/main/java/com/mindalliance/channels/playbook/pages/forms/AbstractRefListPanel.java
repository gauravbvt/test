package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 6:59:00 AM
 */
abstract public class AbstractRefListPanel extends Panel {

    Ref element;
    String listPropName;
    List<String> ddPropNames;
    final Map<String, Component> ddFields = new HashMap<String, Component>();

    public AbstractRefListPanel(String id, Ref element, String listPropName, String[] drillDownPropNames) {
        super(id);
        this.element = element;
        this.listPropName = listPropName;
        this.ddPropNames = Arrays.asList(drillDownPropNames);
        init();
        load();
    }

    protected void init() {
    }

    protected void load() {
        final WebMarkupContainer wmc = new WebMarkupContainer("refListEditor");
        wmc.setOutputMarkupId(true);
        RefreshingView refListView = new RefreshingView("refList", new RefPropertyModel(element, listPropName)) {
            protected Iterator getItemModels() {
                final IModel model = getModel();
                final List list = (List) model.getObject();
                return new ModelIteratorAdapter(list.iterator()) {
                    protected IModel model(Object ref) {
                        return new RefModel(ref);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref itemRef = (Ref)item.getModel().getObject();
                for (String ddName : ddPropNames) {
                    item.add(new Label(ddName, new RefPropertyModel(itemRef, ddName)));
                }
                AjaxLink removeLink = new AjaxLink("remove") {
                    public void onClick(AjaxRequestTarget target) {
                        List<Ref> refList = (List<Ref>) RefUtils.get(element, listPropName);
                        refList.remove(itemRef);
                        element.changed(listPropName);
                        target.addComponent(wmc);
                    }
                };
                removeLink.setOutputMarkupId(true);
                item.add(removeLink);
            }
        };
        refListView.setOutputMarkupId(true);
        wmc.add(refListView);

        // Add
        final AjaxLink addRefLink = new AjaxLink("add") {
            public void onClick(AjaxRequestTarget target) {
                Ref ref = getDrilledDownRef(); // check again
                if (ref != null) {
                    List<Ref>refList = (List<Ref>)RefUtils.get(element, listPropName);
                    refList.add(ref);
                    element.changed(listPropName);
                    for (Component drillDown : ddFields.values()) {
                        resetModelOf(drillDown);
                    }
                    target.addComponent(wmc);
                }
            }
        };
        // addRefLink.setVisible(false);
        addRefLink.setOutputMarkupId(true);

        //// SPECIFIC
        loadDrillDownFields();

        // Set up the update dynamics
        for (final String ddName : ddPropNames) {
            Component ddField = ddFields.get(ddName);
            ddField.setOutputMarkupId(true);
            wmc.add(ddField);
            ddField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    // Clear fields to "the right"
                    int index = ddPropNames.indexOf(ddName);
                    for (int i = index+1; i < ddFields.size(); i++) {
                        Component downStream = ddFields.get(ddPropNames.get(i));
                        downStream.setEnabled(i == index+1);
                        resetModelOf(downStream);
                        target.addComponent(downStream);
                    }
                    // If last field is empty make add link invisible else make visible
                    /*Component lastField = ddFields.get(ddPropNames.get(ddPropNames.size() - 1));
                    String s = ((String) lastField.getModel().getObject()).trim();
                    if (s.isEmpty()) {
                        addRefLink.setVisible(false);
                    } else {
                        Ref ref = getDrilledDownRef();
                        addRefLink.setVisible(ref != null);
                    }
                    target.addComponent(addRefLink);*/
                }
            });
        }
        wmc.add(addRefLink);
        add(wmc);

    }

    protected Project currentProject() {
        PlaybookSession session = (PlaybookSession) Session.get();
        return (Project)session.getProject().deref();
    }

    protected void addDrillDownField(int index, Component field) {
        ddFields.put(ddPropNames.get(index), field);
        if (index > 0) field.setEnabled(false);
    }

    protected String valueOf(Component field) {
        return (String)field.getModel().getObject();
    }

    protected String valueOf(String propName) {
        return (String)ddFields.get(propName).getModel().getObject();
    }

    protected Referenceable getElement() {
        return element.deref();
    }

    public void refresh(AjaxRequestTarget target) {
        for (Component ddField : ddFields.values()) {
            target.addComponent(ddField);
        }
    }

    abstract protected void loadDrillDownFields();

    abstract protected Ref getDrilledDownRef();

    abstract protected void resetModelOf(Component field);

}
