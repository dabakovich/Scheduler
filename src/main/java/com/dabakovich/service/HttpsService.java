package com.dabakovich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by dabak on 07.07.2017, 23:38.
 */
//@Service("httpsService")
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

    public String sendPost(String urlString, Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String json;
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        stringEntity.setContentType("application/json");
        return sendPost(urlString, null, stringEntity);
    }

    public String sendPost(String urlString, Header[] headers, List<BasicNameValuePair> parameters) throws IOException {
        return sendPost(urlString, headers, new UrlEncodedFormEntity(parameters, "UTF-8"));
    }

    public String sendPost(String urlString, Header[] headers, HttpEntity httpEntity) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlString);

        post.setHeaders(headers);
        post.setEntity(httpEntity);
        HttpResponse response;
        response = client.execute(post);
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
    }
}
