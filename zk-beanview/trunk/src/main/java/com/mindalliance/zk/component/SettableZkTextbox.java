/**
 * 
 */
package com.mindalliance.zk.component;

import java.text.Format;
import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Textbox;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.util.SupportedConverters;

/**
 * A ZK Textbox that doubles as a BeanView PropertyComponent
 *
 */
public class SettableZkTextbox extends Textbox implements PropertyComponent {

    private static final long serialVersionUID = -3579728266262424718L;

    public void setValue(Object in) throws IllegalFormatConversionException
    {
        if (in == null)
        {
            this.setText("");
            return;
        }

        Format format = SupportedConverters.getFormatter(in.getClass(),
                beanView);

        if (format != null)
            this.setText(format.format(in));
        else
            this.setText(in.toString());
        
    }
    
    @Override
    public void setValue(String in) {
    	setValue((Object) in);
    }
    
    private BeanView beanView;

    public BeanView getBeanView()
    {
        return this.beanView;
    }

    public void setBeanView(BeanView beanView)
    {
        this.beanView = beanView;
    }
}
