package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "offerSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "OFFER_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "offerSequenceGenerator")
    private Long id;

    private Integer code;

    private String note;

    private String customerName;

    private String customerIdentityNumber;

    private String customerMobile;

    private String masterPaymentType;

    private Double masterPrice;

    private Double masterDiscountAmount;

    private Double masterProfitAmount;

    private Double masterCreditAmount;

    private Boolean registered;

    @ManyToOne
    @JoinColumn(name = "master")
    private Master master;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    private Person lastPerson;

    @JsonCreator
    public static Offer Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer offer = mapper.readValue(jsonString, Offer.class);
        return offer;
    }
}
