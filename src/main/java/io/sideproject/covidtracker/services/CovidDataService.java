package io.sideproject.covidtracker.services;

import io.sideproject.covidtracker.models.LocationStats;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CovidDataService {
  private static String Global_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
  private static String US_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";

  private List<LocationStats> allStats = new ArrayList<>();
  private List<LocationStats> usStats = new ArrayList<>();
  public List<LocationStats> getAllStats() {
    return allStats;
  }

  public List<LocationStats> getUsStats() {
    return usStats;
  }

  @PostConstruct
  @Scheduled(cron = "* * 1 * * *")
  public void fetchData() throws IOException, InterruptedException {
    this.allStats = fetchVirusData(Global_DATA_URL);
    List<LocationStats> us = fetchVirusData(US_DATA_URL);
    this.usStats = process(us);
  }
  public List<LocationStats> fetchVirusData(String url) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
    HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
    //System.out.println(httpResponse.body());

    List<LocationStats> newStats = new ArrayList<>();
    StringReader csvReader = new StringReader(httpResponse.body());
    Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
    for (CSVRecord record : records) {
      LocationStats locationStats = new LocationStats();
      if (url.contains("global.csv")) {
        locationStats.setState(record.get("Province/State"));
        locationStats.setCountry(record.get("Country/Region"));
      } else {
        locationStats.setState(record.get("Province_State"));
        locationStats.setCountry("US");
      }
      int latestCases = Integer.parseInt(record.get(record.size() - 1));
      int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
      locationStats.setLatestTotalCases(latestCases);
      locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
      newStats.add(locationStats);
    }
    return newStats;
  }

  private List<LocationStats> process(List<LocationStats> origin) {
    List<LocationStats> res = new ArrayList<>();
    Collections.sort(origin, Comparator.comparing(LocationStats::getState));
    for (int i = 1; i < origin.size(); i++) {
      LocationStats cur = origin.get(i);
      LocationStats prev = origin.get(i-1);
      if (cur.getState().equals(prev.getState())) {
        cur.combine(prev);
      } else {
        res.add(prev);
      }
    }
    res.add(origin.get(origin.size() - 1));
    return res;
  }

}
