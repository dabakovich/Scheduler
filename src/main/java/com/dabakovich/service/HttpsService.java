package com.dabakovich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by dabak on 07.07.2017, 23:38.
 */
@Service("httpsService")
public class HttpsService {

    public String sendGet(String urlString) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(urlString);
        try {
            HttpResponse response = client.execute(get);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sendPost(String urlString, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlString);

        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            StringEntity stringEntity = new StringEntity(json, "UTF-8");
            stringEntity.setContentType("application/json");
            post.setEntity(stringEntity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("HTTP error code: " + response.getStatusLine().getStatusCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sendPost(String urlString, Header[] headers, List<BasicNameValuePair> parameters) {

        HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(urlString);

        post.setHeaders(headers);
        try {
            post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
            HttpResponse response = client.execute(post);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
