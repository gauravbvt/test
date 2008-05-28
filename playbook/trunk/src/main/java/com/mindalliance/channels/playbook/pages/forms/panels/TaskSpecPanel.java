package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.ifm.spec.TaskSpec;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 7:57:33 AM
 */
public class TaskSpecPanel extends AbstractSpecComponentPanel {

    protected DynamicFilterTree taskTypesTree;
    protected TimingPanel timingPanel;

    public TaskSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        taskTypesTree = new DynamicFilterTree("taskTypes", new RefPropertyModel(getComponent(), "taskTypes"),
                                           new RefQueryModel(getScope(), new Query("findAllTypes", "TaskType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = taskTypesTree.getNewSelections();
                RefUtils.set(getComponent(), "taskTypes", selected);
                elementChanged(propPath+".taskTypes", target);
            }
        };
        addReplaceable(taskTypesTree);
        timingPanel = new TimingPanel("timing", this, propPath+".timing", isReadOnly(), feedback);
        addReplaceable(timingPanel);
    }

    protected Spec makeNewSpec() {
        return new TaskSpec();
    }

    @Override
    protected String getAnyLabelString() {
        return "any task";
    }

}
