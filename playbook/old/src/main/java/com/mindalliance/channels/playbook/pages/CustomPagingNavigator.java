package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;

/**
 *  Overloaded to modify default template for XHTML...
 */
public class CustomPagingNavigator extends PagingNavigator {

    public CustomPagingNavigator( String id, IPageable pageable ) {
        super( id, pageable );
    }
}
