package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.Timing;
import com.mindalliance.channels.playbook.ifm.definition.TaskDefinition;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
    protected AjaxCheckBox anyResponseTimingCheckBox;
    protected WebMarkupContainer responseTimingDiv;

    protected TimingPanel responseTimingPanel;


    public TaskDefinitionPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        taskDefinition = (TaskDefinition)getComponent();
        anyTaskTypeCheckBox = new AjaxCheckBox("anyTaskType", new Model<Boolean>(taskDefinition.getTaskTypes().isEmpty())){
             protected void onUpdate(AjaxRequestTarget target) {
                 boolean anyTaskType = anyTaskTypeCheckBox.getModelObject();
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
                                           new RefQueryModel(getScope(), new Query("findAllTypes", "TaskType"))){
             public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 List<Ref> selected = taskTypesTree.getNewSelections();
                 setProperty("taskTypes", selected);
             }
         };
         addReplaceableTo(taskTypesTree, taskTypesDiv);

        anyPurposeCheckBox = new AjaxCheckBox("anyPurpose", new Model<Boolean>( taskDefinition.getSpecificPurposes().isEmpty() )){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyPurpose = anyPurposeCheckBox.getModelObject();
                if (anyPurpose) {
                    setProperty("specificPurposes", new ArrayList<String>());
                    purposesChooser = new MultipleStringChooser("purposes", TaskDefinitionPanel.this, propPath+".specificPurposes",
                            new RefQueryModel(getScope(), new Query("findAllPurposes")));
                    addReplaceableTo(purposesChooser, purposesDiv);
                }
                setVisibility(purposesDiv, !anyPurpose, target);
            }
        };
        addReplaceable(anyPurposeCheckBox);
        purposesDiv = new WebMarkupContainer("purposesDiv");
        setVisibility(purposesDiv, !taskDefinition.getSpecificPurposes().isEmpty());
        addReplaceable(purposesDiv);
        purposesChooser = new MultipleStringChooser("purposes", this, propPath+".specificPurposes",
                new RefQueryModel(getScope(), new Query("findAllPurposes")));
        addReplaceableTo(purposesChooser, purposesDiv);
        // anyReponseTiming checkbox
        anyResponseTimingCheckBox = new AjaxCheckBox("anyResponseTiming", new Model<Boolean>(!taskDefinition.getResponseTiming().isDefined() )){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyResponseTiming = anyResponseTimingCheckBox.getModelObject();
                if (anyResponseTiming) {
                    setProperty("reponseTiming", new Timing());
                    responseTimingPanel = new TimingPanel("responseTiming", TaskDefinitionPanel.this, propPath+".responseTiming");
                    addReplaceableTo(responseTimingPanel, responseTimingDiv);
                }
                setVisibility(responseTimingDiv, !anyResponseTiming, target);
            }
        };
        addReplaceable(anyResponseTimingCheckBox);
        responseTimingDiv = new WebMarkupContainer("responseTimingDiv");
        setVisibility(responseTimingDiv, taskDefinition.getResponseTiming().isDefined());
        addReplaceable(responseTimingDiv);
        responseTimingPanel = new TimingPanel("responseTiming", this, propPath+".responseTiming");
        addReplaceableTo(responseTimingPanel, responseTimingDiv);
    }
}
