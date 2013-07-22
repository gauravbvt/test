package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Discipline;
import com.mindalliance.sb.model.Expertise;
import com.mindalliance.sb.model.OrgType;
import com.mindalliance.sb.model.Organization;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

import java.text.ParseException;
import java.util.Locale;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        
        // Register application converters and formatters
        registry.addFormatter( new OrgTypeFormatter() );
        registry.addFormatter( new DisciplineFormatter() );
        registry.addFormatter( new OrganizationFormatter() );
        registry.addFormatter( new ExpertiseFormatter() );
    }

    public static class ExpertiseFormatter implements Formatter<Expertise> {
        @Override
        public Expertise parse( String text, Locale locale ) throws ParseException {
            return Expertise.findExpertisesByNameEquals( text ).getSingleResult();
        }

        @Override
        public String print( Expertise object, Locale locale ) {
            return object.getName();
        }
    }

    public static class OrgTypeFormatter implements Formatter<OrgType> {
        @Override
        public OrgType parse( String text, Locale locale ) throws ParseException {
            return OrgType.findOrgTypesByNameEquals( text ).getSingleResult();
        }

        @Override
        public String print( OrgType object, Locale locale ) {
            return object.getName();
        }
    }

    public static class DisciplineFormatter implements Formatter<Discipline>  {
        @Override
        public Discipline parse( String text, Locale locale ) throws ParseException {
            return Discipline.findDisciplinesByNameEquals( text ).getSingleResult();
        }

        @Override
        public String print( Discipline object, Locale locale ) {
            return object.getName();
        }
    }

    public static class OrganizationFormatter implements Formatter<Organization>  {
        @Override
        public Organization parse( String text, Locale locale ) throws ParseException {
            return Organization.findOrganizationsByNameEquals( text ).getSingleResult();
        }

        @Override
        public String print( Organization object, Locale locale ) {
            return object.getName();
        }
    }

}
