// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Toolbarbutton;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.ui.editor.picker.AbstractPicker;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public abstract class AbstractChooser<T extends AbstractJavaBean, PickerType extends AbstractPicker> extends Box {
    protected ZkBeanViewPanel<PickerType> browser;

    protected Toolbarbutton createButton;

    protected Toolbarbutton deleteButton;

    protected Button editButton;

    protected System system;

    protected Scenario scenario;

    protected User user;
    
    protected Class c;

    public AbstractChooser(Class<T> c, System system, Scenario scenario, User user) {
        this.user = user;
        this.scenario = scenario;
        this.system = system;
        this.c = c;
        init();
    }

    private void init() {
        browser = new ZkBeanViewPanel<PickerType>();
        browser.setContext("user", user);
        browser.setContext("system", system);
        browser.setContext("scenario", scenario);

        createButton = new Toolbarbutton();
        createButton.setImage("images/16x16/add2.png");
        createButton.setTooltiptext("Create a new person");

        deleteButton = new Toolbarbutton();
        deleteButton.setImage("images/16x16/delete2.png");
        deleteButton.setTooltiptext("Delete the selected person");

        org.zkoss.zul.Toolbar toolbar = new org.zkoss.zul.Toolbar();
        toolbar.appendChild(createButton);
        toolbar.appendChild(deleteButton);

        Hbox layout = new Hbox();
        layout.appendChild(browser);
        layout.appendChild(toolbar);

        appendChild(layout);

    }

    protected final void setDataObject(AbstractPicker<T> picker) {
        browser.setDataObject(picker);
    }
}
