package com.besafx.app.config;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SendSMS {

    private final static Logger log = LoggerFactory.getLogger(SendSMS.class);

    private void getCredit() throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        String uri = new String("http://api.yamamah.com/GetCredit/{userName}/{password}");

        Map<String, String> vars = new HashMap<>();
        vars.put("userName", "966599233472");
        vars.put("password", "0504975344");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        HttpEntity<String> httpEntity = new HttpEntity<>(json.toString(), httpHeaders);

        String response = restTemplate.postForObject(uri, httpEntity, String.class, vars);

        JSONObject jsonObj = new JSONObject(response);
        log.info(jsonObj.toString());
    }

    public String sendMessage(List<String> mobileList, String message) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        String uri = new String("http://api.yamamah.com/SendSMS");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        JSONObject json = new JSONObject();
        json.put("Username", "966599233472");
        json.put("Password", "0504975344");
        json.put("Tagname", "ANNI");
        json.put("RecepientNumber", String.join(",", mobileList));
        json.put("VariableList", "");
        json.put("ReplacementList", "");
        json.put("Message", message);
        json.put("SendDateTime", 0);
        json.put("EnableDR", false);

        HttpEntity<String> httpEntity = new HttpEntity<>(json.toString(), httpHeaders);

        String response = restTemplate.postForObject(uri, httpEntity, String.class);

        JSONObject jsonObj = new JSONObject(response);
        return jsonObj.toString();
    }
}
