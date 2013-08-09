package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.PlanFile;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/planfiles")
@Controller
@RooWebScaffold(path = "lists/planfiles", formBackingObject = PlanFile.class)
public class PlanFileController extends AbstractController<PlanFile> {

    @Override
    protected List<PlanFile> getList() {
        return PlanFile.findAllPlanFiles();
    }
}
