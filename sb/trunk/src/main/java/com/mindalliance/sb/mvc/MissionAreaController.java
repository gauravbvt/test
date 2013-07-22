package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.MissionArea;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/missionareas")
@Controller
@RooWebScaffold(path = "lists/missionareas", formBackingObject = MissionArea.class, update = false, delete = false, create = false)
public class MissionAreaController extends AbstractController<MissionArea> {

    @Override
    protected List<MissionArea> getList() {
        return MissionArea.findAllMissionAreas();
    }
}
