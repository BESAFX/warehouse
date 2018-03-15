package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "paymentOutSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PAYMENT_OUT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "paymentOutSequenceGenerator")
    private Long id;

    private Long code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String amountString;

    private Double amountNumber;

    private String toName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @JsonCreator
    public static PaymentOut Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PaymentOut paymentOut = mapper.readValue(jsonString, PaymentOut.class);
        return paymentOut;
    }
}
