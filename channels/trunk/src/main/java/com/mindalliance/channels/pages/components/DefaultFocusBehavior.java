package com.mindalliance.channels.pages.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/14
 * Time: 10:05 PM
 */
public class DefaultFocusBehavior extends Behavior
{
    @Override
    public void bind(Component component)
    {
        if (!(component instanceof FormComponent ))  {
            throw new IllegalArgumentException("DefaultFocusBehavior: component must a FormComponent");
        }
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse iHeaderResponse) {
        super.renderHead(component, iHeaderResponse);

        AjaxRequestTarget target = AjaxRequestTarget.get();
        if (target != null)  {
            final String javascript = "document.getElementById('"
                    + component.getMarkupId() + "').focus();";

            target.appendJavaScript(javascript);
        }
    }
}
