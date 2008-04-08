package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.support.RefUtils;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 28, 2008
 * Time: 3:15:33 PM
 */
// Not much to abstract...
abstract public class AbstractComponentPanel extends Panel {

    Ref element;     // element containing the component to be edited
    String propName; // name of the element's property which value is the component to be edited

    public AbstractComponentPanel(String id, Ref element, String propName) {
        super(id);
        this.element = element;
        this.propName = propName;
        init();
        load();
    }

    protected void init() {
        this.setOutputMarkupId(true);
        this.add(new SimpleAttributeModifier("class", "component"));
    }

    protected void load() {
       // Do nothing
    }

    public void refresh(AjaxRequestTarget target) {
       // Do nothing
    }

    public void onDetach() {
        Bean bean = (Bean) RefUtils.get(element, propName);
        bean.detach();
        super.onDetach();
    }

}
