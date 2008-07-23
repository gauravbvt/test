package com.mindalliance.channels.playbook.pages.forms.tabs.task;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.playbook.Task;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:25:34 PM
 */
public class TaskBasicTab extends InformationActBasicTab {

    protected DynamicFilterTree taskTypeTree;
    protected WebMarkupContainer purposesDiv;
    protected RefreshingView purposesView;
    protected AjaxButton addPurposeButton;
    protected TimingPanel durationPanel;

    public TaskBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        final Task task = (Task)getElement().deref();
        taskTypeTree = new DynamicFilterTree("taskType", new RefPropertyModel(getElement(), "taskType"),
                new RefQueryModel(getProject(),
                        new Query("findAllTypes", "TaskType")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedType = taskTypeTree.getNewSelection();
                setProperty("taskType", selectedType);
            }
        };
        addReplaceable(taskTypeTree);
        purposesDiv = new WebMarkupContainer("purposesDiv");
        addReplaceable(purposesDiv);
        purposesView = new RefreshingView("purposes", new RefPropertyModel(getElement(), "specificPurposes")) {
            protected Iterator getItemModels() {
                List<Integer> indices = listIndicesIn((List)purposesView.getModelObject());                
                return new ModelIteratorAdapter(indices.iterator()) {
                    protected IModel model(Object index) {
                        return new Model((Integer) index);
                    }
                };
            }
            protected void populateItem(Item item) {
                final int index = (Integer)item.getModelObject();
                String purpose = (String)task.getSpecificPurposes().get(index);
                final TextArea purposeField = new TextArea("purpose", new Model(purpose));
                purposeField.add(new AjaxFormComponentUpdatingBehavior("onchange"){
                    protected void onUpdate(AjaxRequestTarget target) {
                        task.getSpecificPurposes().remove(index);
                        task.getSpecificPurposes().add(index, purposeField.getModelObjectAsString());
                        task.changed("specificPurposes");
                    }
                });
                item.add(purposeField);
                AjaxLink deleteLink = new AjaxLink("deletePurpose") {
                    public void onClick(AjaxRequestTarget target) {
                        task.getSpecificPurposes().remove(index);
                        task.changed("specificPurposes");
                        target.addComponent(purposesDiv);
                    }
                };
                item.add(deleteLink);
            }
        };
        addReplaceableTo(purposesView, purposesDiv);
        addPurposeButton = new AjaxButton("addPurpose") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                task.getSpecificPurposes().add("");
                task.changed("specificPurposes");
                target.addComponent(purposesDiv);
            }
        };
        addReplaceableTo(addPurposeButton,purposesDiv);
        durationPanel = new TimingPanel("duration", this, "duration");
        addReplaceable(durationPanel);
    }

    private List<Integer> listIndicesIn(List list) {
        List<Integer> indices = new ArrayList<Integer>();
        for (int i=0; i<list.size(); i++) {
            indices.add(i);
        }
        return indices;
    }

}
