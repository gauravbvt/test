package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.ifm.context.environment.Person;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 29, 2008
 * Time: 6:43:20 PM
 */
public class PositionsPanel extends Panel {

    Ref element;
    String listPropName;
    List<String> ddPropNames;
    final Map<String, Component> ddFields = new HashMap<String, Component>();

    public PositionsPanel(String id, Ref element, String listPropName, String[] drillDownPropNames) {
        super(id);
        this.element = element;
        this.listPropName = listPropName;
        this.ddPropNames = Arrays.asList(drillDownPropNames);
        init();
        load();
    }

    private void init() {

    }

    private void load() {
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
                        drillDown.getModel().setObject("");
                    }
                    target.addComponent(wmc);
                }
            }
        };
        // addRefLink.setVisible(false);
        addRefLink.setOutputMarkupId(true);

        ///////// START SPECIFIC

        // drillDownPropNames[0] -> drill down to Position on Organization
        final AutoCompleteTextField acfOrg = new AutoCompleteTextField("with.organization.name", new Model("")) {
            protected Iterator getChoices(String input) {
                List<Ref> orgs = ((Person) element.deref()).findOrganizationsWithOtherPositions();
                List<String> choices = new ArrayList<String>();
                for (Ref org : orgs) {
                    Organization organization = (Organization)org.deref();
                    String orgName = organization.getName();
                    if (orgName.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(orgName);
                    }
                }
                return choices.iterator();
            }
        };
        ddFields.put(ddPropNames.get(0), acfOrg);

        // drillDownPropNames[1] -> drill down to Position on name, given Organization
        AutoCompleteTextField acfName = new AutoCompleteTextField("with.name", new Model("")) {
            protected Iterator getChoices(String input) {
                String orgName = (String) acfOrg.getModel().getObject();
                List<String> names = ((Person) element.deref()).findOtherPositionNamesInOrganizationNamed(orgName);
                List<String> choices = new ArrayList<String>();
                for (String name : names) {
                    if (name.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(name);
                    }
                }
                return choices.iterator();
            }
        };
        acfName.setEnabled(false);
        ddFields.put(ddPropNames.get(1), acfName);

        ///////// END SPECIFIC

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
                        downStream.getModel().setObject("");
                        downStream.setEnabled(i == index+1);
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

    // SPECIFIC

    // Fetch the Ref identified by the autocomplete text field values
    protected Ref getDrilledDownRef() {
        Ref ref = null;
        String orgName = (String)ddFields.get("organization.name").getModel().getObject();
        String name = (String)ddFields.get("name").getModel().getObject();
        Ref org = currentProject().findResourceNamed("Organization", orgName);
        if (org != null) {
            ref = ((Organization)org.deref()).findPositionNamed(name);
        }
        return ref;
    }

    // END SPECIFIC

    protected Project currentProject() {
        PlaybookSession session = (PlaybookSession) Session.get();
        return (Project)session.getProject().deref();
    }
}
