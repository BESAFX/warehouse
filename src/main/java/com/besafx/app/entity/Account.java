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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "accountSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ACCOUNT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "accountSequenceGenerator")
    private Long id;

    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    private String coursePaymentType;

    private Double coursePrice;

    private Double courseDiscountAmount;

    private Double courseProfitAmount;

    private Double courseCreditAmount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "course")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountAttach> accountAttaches = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountCondition> accountConditions = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountNote> accountNotes = new ArrayList<>();

    public Double getRequiredPrice() {
        try{
            if (this.coursePaymentType.equals("نقدي")) {
                return (this.coursePrice - (this.coursePrice * this.courseDiscountAmount / 100));
            } else {
                return (this.coursePrice + (this.coursePrice * this.courseProfitAmount / 100));
            }
        }catch (Exception ex){
            return 0.0;
        }
    }

    public Double getPaidPrice() {
        try {
            return this.payments
                    .stream()
                    .filter(payment -> payment.getType().equals("ايرادات اساسية"))
                    .mapToDouble(Payment::getAmountNumber)
                    .sum();
        }catch (Exception ex){
            return 0.0;
        }
    }

    public Double getRemainPrice() {
        try{
            return this.getRequiredPrice() - this.getPaidPrice();
        }catch (Exception ex){
            return 0.0;
        }
    }

    @JsonCreator
    public static Account Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(jsonString, Account.class);
        return account;
    }
}
