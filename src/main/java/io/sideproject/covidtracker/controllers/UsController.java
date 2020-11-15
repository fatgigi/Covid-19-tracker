package io.sideproject.covidtracker.controllers;

import io.sideproject.covidtracker.models.LocationStats;
import io.sideproject.covidtracker.services.CovidDataService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsController {
  @Autowired
  CovidDataService covidDataService;

  @GetMapping("/us")
  public String us(Model model) {
    List<LocationStats> allStats = covidDataService.getUsStats();
    model.addAttribute("locationStats", allStats);
    return "us";
  }
}
