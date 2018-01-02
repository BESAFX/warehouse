package com.besafx.app.entity.wrapper;

import com.besafx.app.entity.Payment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Payment> payments = new ArrayList<>();

    @JsonCreator
    public static PaymentWrapper Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PaymentWrapper payment = mapper.readValue(jsonString, PaymentWrapper.class);
        return payment;
    }
}
