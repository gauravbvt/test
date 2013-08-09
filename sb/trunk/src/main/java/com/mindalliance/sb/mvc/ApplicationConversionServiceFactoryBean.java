package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.NamedObject;
import com.mindalliance.sb.model.PrintableObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
@Service( "applicationConversionService" )
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        
        // Register application converters and formatters

        registry.addConverter( new CollectionToStringConverter( getObject() ) );
        registry.addConverter( new Converter<NamedObject, String>() {
            @Override
            public String convert( NamedObject source ) {
                return source.getName();
            }
        } );
        registry.addConverter( new Converter<PrintableObject, String>() {
            @Override
            public String convert( PrintableObject source ) {
                return source.toString();
            }
        } );
        registry.addConverter( new Converter<Calendar, String>() {
            @Override
            public String convert( Calendar source ) {
                return new SimpleDateFormat( "M/dd/yyyy HH:mm:ss",
                                             Locale.getDefault() )
                    .format( source.getTime() );
            }
        } );
        registry.addConverter( new Converter<Date, String>() {
            @Override
            public String convert( Date source ) {
                return new SimpleDateFormat( "M/dd/yyyy", Locale.getDefault() )
                    .format( source );
            }
        } );
    }
}
