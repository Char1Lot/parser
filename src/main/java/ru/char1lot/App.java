package ru.char1lot;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Parser parser = new Parser("C:\\Program Files\\chromedriver-win64\\chromedriver.exe");

        Scanner sc = new Scanner(System.in);

        System.out.println("Введите никнейм пользоватея на Faceit :");
        String username = sc.next();
        System.out.println("Введите url куда отправятся POST запросы :");
        String url = sc.next();

        List<List<String>> parsedData = parser.parse(username);

        for (List<String> singleParsedData : parsedData) {
            String json = parser.getJson(singleParsedData);
            sendJson(url, json);
        }
    }

    private static void sendJson(String url, String jsonData) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpPost.setEntity(new StringEntity(jsonData, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response Body: " + EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}