// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;

/**
 * Convenience selector to get icons for given class of objects.
 *
 * <p>The returned values will always be as specific as possible.
 * For example, if there are icons defined for both Object and X,
 * asking for getSmallIcon( x ) will return the icon for X, not object.</p>
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class IconManager {

    /**
     * The annotation mode for the desired icon.
     */
    public enum Mode {

        /**
         * A normal unannotated icon.
         */
        Normal,

        /**
         * An icon annotated with a little yellow warning sign.
         */
        Warning,

        /**
         * An icon annotated with a red stop sign.
         */
        Error
    }

    private String smallPrefix = "";
    private String bigPrefix = "";
    private String nullIcon;
    private String defaultIcon;
    private Map<Class,String> icons = new HashMap<Class,String>();
    /**
     * Default constructor.
     */
    public IconManager() {
    }

    /**
     * Return the normal icon for an object.
     * @param object the object
     * @return getSmallIcon( object, Normal )
     */
    public String getSmallIcon( Object object ) {
        return getSmallIcon( object, Mode.Normal );
    }

    /**
     * Return the normal icon for an object.
     * @param object the object
     * @return getBigIcon( object, Normal )
     */
    public String getBigIcon( Object object ) {
        return getBigIcon( object, Mode.Normal );
    }

    /**
     * Return the proper icon for an object.
     * @param object the object
     * @param mode the mode of the icon
     * @return a 24x24 icon url
     */
    public String getBigIcon( Object object, Mode mode ) {
        return object == null ? getBigPrefix() + getNullIcon()
                              : getBigIcon( object.getClass(), mode );
    }

    /**
     * Return the proper icon for an object.
     * @param object the object
     * @param mode the mode of the icon
     * @return a 16x16 icon url
     */
    public String getSmallIcon( Object object, Mode mode ) {
        return object == null ? getSmallPrefix() + getNullIcon()
                              : getSmallIcon( object.getClass(), mode );
    }

    /**
     * Return the appropriate icon for objects of a given class.
     * @param clazz the class
     * @param mode the desired mode of the icon
     * @return a 16x16 icon url
     */
    public String getSmallIcon( Class clazz, Mode mode ) {
        return getIcon( clazz, getIcons(), getSmallPrefix() );
    }

    /**
     * Return the appropriate icon for objects of a given class.
     * @param clazz the class
     * @param mode the desired mode of the icon
     * @return a 24x24 icon url
     */
    public String getBigIcon( Class clazz, Mode mode ) {
        return getIcon( clazz, getIcons(), getBigPrefix() );
    }

    private String getIcon(
            Class clazz, Map<Class, String> icons, String prefix ) {

        //TODO implement icon modes
        String icon = icons.get( clazz );

        if ( icon == null ) {
            Collection<Class> classes = getSuperclasses( clazz );
            for ( Class c : classes ) {
                icon = icons.get( c );
                if ( icon != null )
                    break;
            }
        }

        if ( icon == null )
            icon = getDefaultIcon();

        return prefix + icon;
    }

    /**
     * Silly method for potential class/interface matches
     * for a given class.
     * @param clazz the class
     * @return an ordered list of classes/interfaces implemented
     * by the given class
     */
    @SuppressWarnings( "unchecked" )
    private Collection<Class> getSuperclasses( Class clazz ) {
        Set<Class> result = new ListOrderedSet();
        result.add( clazz );
        addSuperclasses( result, clazz );
        return result;
    }

    private void addSuperclasses( Set<Class> set, Class clazz ) {
        for ( Class i : clazz.getInterfaces() )
            set.add( i );
        Class s = clazz.getSuperclass();
        if ( s != null )
            set.add( s );

        for ( Class i : clazz.getInterfaces() )
            addSuperclasses( set, i );
        if ( s != null )
            addSuperclasses( set, s );
    }

    /**
     * Return the value of smallIcons.
     */
    public final Map<Class, String> getIcons() {
        return this.icons;
    }

    /**
     * Set the value of icons.
     * @param smallIcons The new value of icons
     */
    public void setIcons( Map<Class, String> smallIcons ) {
        this.icons = smallIcons;
    }

    /**
     * Return the value of prefix.
     * @return the prefix to prepend to icons initialized by a
     * call to setXIcons()
     */
    public String getSmallPrefix() {
        return this.smallPrefix;
    }

    /**
     * Set the value of prefix.
     * @param prefix The new value of prefix
     */
    public void setSmallPrefix( String prefix ) {
        this.smallPrefix = prefix;
    }

    /**
     * Return the value of nullIcon.
     */
    public String getNullIcon() {
        return this.nullIcon == null ?
                getDefaultIcon() : this.nullIcon ;
    }

    /**
     * Set the value of nullIcon.
     * @param nullIcon The new value of nullIcon
     */
    public void setNullIcon( String nullIcon ) {
        this.nullIcon = nullIcon;
    }

    /**
     * Return the value of defaultIcon.
     */
    public String getDefaultIcon() {
        return this.defaultIcon;
    }

    /**
     * Set the value of defaultIcon.
     * @param defaultIcon The new value of defaultIcon
     */
    public void setDefaultIcon( String defaultIcon ) {
        this.defaultIcon = defaultIcon;
    }

    /**
     * Return the value of bigPrefix.
     */
    public String getBigPrefix() {
        return this.bigPrefix;
    }

    /**
     * Set the value of bigPrefix.
     * @param bigPrefix The new value of bigPrefix
     */
    public void setBigPrefix( String bigPrefix ) {
        this.bigPrefix = bigPrefix;
    }
}
