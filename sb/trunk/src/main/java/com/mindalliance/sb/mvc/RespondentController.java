package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Respondent;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/respondents")
@Controller
@RooWebScaffold(path = "lists/respondents", formBackingObject = Respondent.class)
public class RespondentController  extends AbstractController<Respondent> {

    @Override
    protected List<Respondent> getList() {
        return Respondent.findAllRespondents();
    }
}
