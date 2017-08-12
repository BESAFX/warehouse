package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
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
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "paymentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PAYMENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "paymentSequenceGenerator")
    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private Long id;

    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private Date date;

    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private String amountString;

    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private Double amountNumber;

    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private String type;

    @JsonView(Views.Summery.class)
    private String toName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private String note;

    @ManyToOne
    @JoinColumn(name = "account")
    @JsonIgnoreProperties(value = {"payments", "accountAttaches", "accountConditions"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    @JsonView(value = {Views.Summery.class, Views.PaymentByAccount.class})
    private Person lastPerson;

    @JsonCreator
    public static Payment Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Payment payment = mapper.readValue(jsonString, Payment.class);
        return payment;
    }
}
