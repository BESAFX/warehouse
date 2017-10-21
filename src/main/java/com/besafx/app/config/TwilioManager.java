package com.besafx.app.config;

import com.google.common.collect.Lists;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class TwilioManager {

    private final Logger log = LoggerFactory.getLogger(TwilioManager.class);

    public static final String ACCOUNT_SID = "ACefec35545cc868089dadc7fade4eafb6";

    public static final String AUTH_TOKEN = "df45ca6b7e27f746d764f8fb79fb2131";

    @PostConstruct
    public void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public List<Message> getMessages(){
        ResourceSet<Message> messages = Message.reader().read();
        return Lists.newArrayList(messages);
    }

    @Async("threadPoolTwilioSMS")
    public Future<Message.Status> send(String to, String body){
        try{
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber("+14157234094"),
                    body)
                    .create();
            return new AsyncResult<>(message.getStatus());
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            return new AsyncResult<>(Message.Status.FAILED);
        }
    }
}
