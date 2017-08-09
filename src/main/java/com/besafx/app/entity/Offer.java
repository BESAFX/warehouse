package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @JsonView(Views.Summery.class)
    private String note;

    @JsonView(Views.Summery.class)
    private String customerName;

    @JsonView(Views.Summery.class)
    private String customerIdentityNumber;

    @JsonView(Views.Summery.class)
    private String customerMobile;

    @JsonView(Views.Summery.class)
    private String masterPaymentType;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    @JsonView(Views.Summery.class)
    private Double masterPrice;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    @JsonView(Views.Summery.class)
    private Double masterDiscountAmount;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    @JsonView(Views.Summery.class)
    private Double masterProfitAmount;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    @JsonView(Views.Summery.class)
    private Double masterCreditAmount;

    @JsonView(Views.Summery.class)
    private Boolean registered;

    @ManyToOne
    @JoinColumn(name = "master")
    @JsonIgnoreProperties(value = {"courses"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Master master;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person lastPerson;

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"offer"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<Call> calls = new ArrayList<>();

    @JsonCreator
    public static Offer Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer offer = mapper.readValue(jsonString, Offer.class);
        return offer;
    }
}
