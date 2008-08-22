package com.mindalliance.channels.playbook.pages.forms.tabs.job;

import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.panels.ReferencePanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ifm.project.resources.Job;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 10:20:41 AM
 */
public class JobIdentityTab extends ResourceIdentityTab {

    private Job job;
    private ReferencePanel individualPanel;
    private ReferencePanel positionPanel;

    public JobIdentityTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        job = (Job)getElement().deref();
        individualPanel = new ReferencePanel("individual", this, "individual",  new RefQueryModel(getProject(), new Query("findAllIndividuals")));
        add(individualPanel);
        positionPanel = new ReferencePanel("position", this, "position",  new RefQueryModel(getProject(), new Query("findAllPositionsAnywhere")));
        add(positionPanel);
    }

    @Override
    protected void init() {
        super.init();
        nameField.setEnabled( false );
    }

    @Override
    public void elementChanged( String propPath, AjaxRequestTarget target ) {
        super.elementChanged( propPath, target );
         target.addComponent( nameField );
    }

    
}
