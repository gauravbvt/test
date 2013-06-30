package com.mindalliance.sb.surveygizmo;

import java.util.Map;

/**
 * @TODO comment this
 */
public class SurveyOption {
    
    private int id;
    private String _type;
    private Map<String,String> title;
    private String value;
    private Map<String,String> properties;

    public String get_type() {
        return _type;
    }

    public void set_type( String _type ) {
        this._type = _type;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties( Map<String, String> properties ) {
        this.properties = properties;
    }

    public Map<String,String> getTitle() {
        return title;
    }

    public void setTitle( Map<String,String> title ) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public boolean equals( Object o ) {
        return this == o 
            || o != null 
                && getClass() == o.getClass() 
                && id == ( (SurveyOption) o ).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "SurveyOption" );
        sb.append( "{id=" ).append( id );
        sb.append( ", value='" ).append( value ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
