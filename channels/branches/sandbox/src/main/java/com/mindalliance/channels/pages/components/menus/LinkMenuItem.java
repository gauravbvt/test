package com.mindalliance.channels.pages.components.menus;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * A link menu item.
 */
public class LinkMenuItem extends Panel {

    /**
     * Space with width of one en (half of one em).
     */
    private static char ENSP = (char)8194;

    IModel<String> model;
    private String paddedName;
    private Label label;
    private AbstractLink link;

    public LinkMenuItem( String s, IModel<String> model, AbstractLink link ) {
        super( s, model );
        this.model = model;
        this.link = link;
        link.setMarkupId( "link" );
        add( link );
    }

    protected void onBeforeRender() {
        super.onBeforeRender();
        if ( label == null ) {
            // Just in time setting of label (after name padding).
            label = new Label( "string", new PropertyModel<String>( this, "name" ) );
            link.add( label );
        }
    }

    public String getName() {
        return paddedName == null ? model.getObject() : paddedName;
    }

    public void padName( int length ) {
        paddedName = StringUtils.rightPad( model.getObject(), length, ENSP  );
    }
}
