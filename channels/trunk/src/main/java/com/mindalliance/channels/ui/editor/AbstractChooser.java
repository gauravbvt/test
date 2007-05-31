// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Toolbarbutton;

import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
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

    protected SystemService system;

    protected User user;
    
    protected Class c;

    public AbstractChooser(Class<T> c, SystemService system, User user) {
        this(c, system, user, new ZkBeanViewPanel<PickerType>());
    }
    
    public AbstractChooser(Class<T> c, SystemService system, User user, ZkBeanViewPanel panel) {
        this.user = user;
        this.system = system;
        this.c = c;
        browser = panel;
        browser.setContext("user", user);
        browser.setContext("system", system);
        browser.setContext( "class", c );
        init();
    }  

    private void init() {
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
