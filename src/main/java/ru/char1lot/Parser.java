package ru.char1lot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    private final String pathToChromeDriver;

    private final WebDriver webDriver;

    public Parser(String pathToChromeDriver) {
        this.webDriver = new ChromeDriver();
        this.pathToChromeDriver = pathToChromeDriver;
    }

    public List<List<String>> parse(String username) {
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);
        List<List<String>> resultParse = new ArrayList<>();

        try {
            webDriver.get("https://www.faceit.com/ru/players/" + username + "/stats/cs2");

            Thread.sleep(10000);

            String pageSource = webDriver.getPageSource();

            Document document = Jsoup.parse(pageSource);

            Elements matchRows = document.select("table.styles__MatchHistoryTable-sc-1b6c3a7a-3 tbody tr");
            for (int i = 0; i < matchRows.size(); i++) {
                Elements cells = matchRows.get(i).select("td");
                if (cells.size() >= 6) {
                    List<String> parsedData = new ArrayList<>();
                    parsedData.add(cells.get(0).text().substring(1,8));
                    parsedData.add(cells.get(0).text().substring(11,16));
                    parsedData.add(cells.get(1).text());
                    parsedData.add(cells.get(2).text().equals("Поражение") ? "false" : "true");
                    parsedData.add(cells.get(3).text().substring(1));
                    parsedData.add(cells.get(4).select("img").attr("alt"));
                    parsedData.add(username);
                    resultParse.add(parsedData);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            webDriver.close();
        }
        return resultParse;
    }

    public String getJson(List<String> strs){
        Map<String, String> data = new HashMap<>();

        data.put("date", strs.get(0));
        data.put("time", strs.get(1));
        data.put("type", strs.get(2));
        data.put("result", strs.get(3));
        data.put("score",strs.get(4));
        data.put("map", strs.get(5));
        data.put("userName", strs.get(6));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;

        try {
            jsonData = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonData;
    }
}
