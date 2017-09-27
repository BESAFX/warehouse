package com.besafx.app.entity;
import com.besafx.app.component.BeanUtil;
import com.besafx.app.search.OfferSearch;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.OfferService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Component
public class Offer implements Serializable {

    private final static Logger log = LoggerFactory.getLogger(Offer.class);

    private static final long serialVersionUID = 1L;

    @Transient
    private static AccountService accountService;

    @Transient
    private static OfferService offerService;

    @PostConstruct
    public void init() {
        try{
            accountService = BeanUtil.getBean(AccountService.class);
            offerService = BeanUtil.getBean(OfferService.class);
        }catch (Exception ex){

        }
    }

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

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    private Double masterPrice;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    private Double masterDiscountAmount;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    private Double masterProfitAmount;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'", nullable = false)
    private Double masterCreditAmount;

    private Boolean registered;

    @ManyToOne
    @JoinColumn(name = "master")
    private Master master;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    private List<Call> calls = new ArrayList<>();

    public Boolean getRegistered(){
        try {
            Long accountsCount = accountService.countByStudentContactMobile(this.customerMobile);
            log.info("عدد التسجيلات = " + accountsCount);
            if (accountsCount > 0) {
                if(!this.registered){
                    this.registered = true;
                    offerService.save(this);
                }
            } else {
                if(this.registered){
                    this.registered = false;
                    offerService.save(this);
                }
            }
            return this.registered;
        }catch (Exception ex){
            return null;
        }
    }

    public Double getNet(){
        try{
            if(masterPaymentType.equals("نقدي")){
                return this.masterPrice - (this.masterPrice * this.masterDiscountAmount / 100);
            }else{
                return this.masterPrice + (this.masterPrice * this.masterProfitAmount / 100);
            }
        }catch (Exception ex){
            return null;
        }
    }

    @JsonCreator
    public static Offer Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer offer = mapper.readValue(jsonString, Offer.class);
        return offer;
    }
}
