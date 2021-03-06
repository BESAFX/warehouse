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
public class BillPurchasePayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "billPurchasePaymentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BILL_PURCHASE_PAYMENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "billPurchasePaymentSequenceGenerator")
    private Long id;

    private Integer code;

    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "billPurchase")
    private BillPurchase billPurchase;

    @ManyToOne
    @JoinColumn(name = "bankTransaction")
    private BankTransaction bankTransaction;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @JsonCreator
    public static BillPurchasePayment Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BillPurchasePayment billPurchasePayment = mapper.readValue(jsonString, BillPurchasePayment.class);
        return billPurchasePayment;
    }
}
