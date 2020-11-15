package io.sideproject.covidtracker.controllers;

import io.sideproject.covidtracker.models.LocationStats;
import io.sideproject.covidtracker.services.CovidDataService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class GlobalController {
  @Autowired
  CovidDataService covidDataService;

  @GetMapping("/global")
  public String global(Model model) {
    List<LocationStats> allStats = covidDataService.getAllStats();
    model.addAttribute("locationStats", allStats);
    return "global";
  }
}
