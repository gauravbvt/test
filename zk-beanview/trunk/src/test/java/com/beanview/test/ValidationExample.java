package com.beanview.test;

import com.beanview.validation.EmailAddress;
import com.beanview.validation.MatchRegex;
import com.beanview.validation.MaximumLength;
import com.beanview.validation.MaximumValue;
import com.beanview.validation.MinimumLength;
import com.beanview.validation.MinimumValue;
import com.beanview.validation.WebsiteUrl;

public class ValidationExample
{

    String noValidation;

    String emailAddress = "null@null.com";

    int minimumValue = 0;

    int maximumValue = 0;

    String websiteURL = "http://www.cascadetg.com/";

    String minimumLength = "yes";

    String maximumLength = "no";

    String matchRegex = "JavaBeans";

    String minMaxLength = "hello";

    String minMaxValue = "50";

    @EmailAddress
    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    @MatchRegex(constraint = "Java")
    public String getMatchRegex()
    {
        return matchRegex;
    }

    public void setMatchRegex(String matchRegex)
    {
        this.matchRegex = matchRegex;
    }

    @MaximumLength(constraint = "5")
    public String getMaximumLength()
    {
        return maximumLength;
    }

    public void setMaximumLength(String maximumLength)
    {
        this.maximumLength = maximumLength;
    }

    @MaximumValue(constraint = "100")
    public int getMaximumValue()
    {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue)
    {
        this.maximumValue = maximumValue;
    }

    @MinimumLength(constraint = "3")
    public String getMinimumLength()
    {
        return minimumLength;
    }

    public void setMinimumLength(String minimumLength)
    {
        this.minimumLength = minimumLength;
    }

    @MinimumValue(constraint = "-100")
    public int getMinimumValue()
    {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue)
    {
        this.minimumValue = minimumValue;
    }

    public String getNoValidation()
    {
        return noValidation;
    }

    public void setNoValidation(String noValidation)
    {
        this.noValidation = noValidation;
    }

    @WebsiteUrl
    public String getWebsiteURL()
    {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL)
    {
        this.websiteURL = websiteURL;
    }

    public String getMinMaxLength()
    {
        return minMaxLength;
    }

    public void setMinMaxLength(String minMaxLength)
    {
        this.minMaxLength = minMaxLength;
    }

    public String getMinMaxValue()
    {
        return minMaxValue;
    }

    @MaximumValue(constraint = "100")
    @MinimumValue(constraint = "-100")
    public void setMinMaxValue(String minMaxValue)
    {
        this.minMaxValue = minMaxValue;
    }

}
