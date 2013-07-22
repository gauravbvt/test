package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.SituationReport;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/lists/situationreports")
@Controller
@RooWebScaffold(path = "lists/situationreports", formBackingObject = SituationReport.class)
public class SituationReportController {
}
