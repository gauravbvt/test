package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.definition.TaskDefinition;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 4:05:10 PM
 */
public class TaskDefinitionPanel extends AbstractDefinitionPanel {

    protected TaskDefinition taskDefinition;
    protected AjaxCheckBox anyTaskTypeCheckBox;
    protected WebMarkupContainer taskTypesDiv;
    protected DynamicFilterTree taskTypesTree;
    protected AjaxCheckBox anyPurposeCheckBox;
    protected WebMarkupContainer purposesDiv;
    protected MultipleStringChooser purposesChooser;


    public TaskDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        taskDefinition = (TaskDefinition)getComponent();
        anyTaskTypeCheckBox = new AjaxCheckBox("anyTaskType", new Model((Boolean)taskDefinition.getTaskTypes().isEmpty())){
             protected void onUpdate(AjaxRequestTarget target) {
                 boolean anyTaskType = (Boolean)anyTaskTypeCheckBox.getModelObject();
                 if (anyTaskType) {
                     setProperty("taskTypes", new ArrayList<Ref>());
                 }
                 setVisibility(taskTypesDiv, !anyTaskType, target);
             }
         };
         addReplaceable(anyTaskTypeCheckBox);
         taskTypesDiv = new WebMarkupContainer("taskTypesDiv");
         setVisibility(taskTypesDiv, !taskDefinition.getTaskTypes().isEmpty());
         addReplaceable(taskTypesDiv);
         taskTypesTree = new DynamicFilterTree("taskTypes",
                                           new RefPropertyModel(getElement(), propPath+".taskTypes"),
                                           new RefQueryModel(getProject(), new Query("findAllTypes", "TaskType"))){
             public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 List<Ref> selected = taskTypesTree.getNewSelections();
                 setProperty("taskTypes", selected);
             }
         };
         addReplaceableTo(taskTypesTree, taskTypesDiv);

        anyPurposeCheckBox = new AjaxCheckBox("anyPurpose", new Model((Boolean)taskDefinition.getSpecificPurposes().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyPurpose = (Boolean)anyPurposeCheckBox.getModelObject();
                if (anyPurpose) {
                    setProperty("specificPurposes", new ArrayList<String>());
                    purposesChooser = new MultipleStringChooser("purposes", TaskDefinitionPanel.this, propPath+".specificPurposes", EDITABLE, feedback,
                            new RefQueryModel(getProject(), new Query("findAllPurposes")));   // TODO getProject(), not Channels.instance()
                    addReplaceableTo(purposesChooser, purposesDiv);
                }
                setVisibility(purposesDiv, !anyPurpose, target);
            }
        };
        addReplaceable(anyPurposeCheckBox);
        purposesDiv = new WebMarkupContainer("purposesDiv");
        setVisibility(purposesDiv, taskDefinition.getSpecificPurposes().isEmpty());
        addReplaceable(purposesDiv);
        purposesChooser = new MultipleStringChooser("purposes", this, propPath+".specificPurposes", EDITABLE, feedback,
                new RefQueryModel(getProject(), new Query("findAllPurposes")));   // TODO getProject(), not Channels.instance()
        addReplaceableTo(purposesChooser, purposesDiv);
    }
}
