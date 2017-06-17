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
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date registerDate;

    @JsonView(Views.Summery.class)
    private String coursePaymentType;

    @JsonView(Views.Summery.class)
    private Double coursePrice;

    @JsonView(Views.Summery.class)
    private Double courseDiscountAmount;

    @JsonView(Views.Summery.class)
    private Double courseProfitAmount;

    @JsonView(Views.Summery.class)
    private Double courseCreditAmount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String note;

    @ManyToOne
    @JoinColumn(name = "course")
    @JsonIgnoreProperties(value = {"accounts"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student")
    @JsonView(Views.Summery.class)
    private Student student;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person lastPerson;

    @JsonCreator
    public static Account Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(jsonString, Account.class);
        return account;
    }

    public Double getRequiredPrice() {
        if (this.coursePaymentType.equals("نقدي")) {
            return (this.coursePrice - (this.coursePrice * this.courseDiscountAmount / 100));
        } else {
            return (this.coursePrice + (this.coursePrice * this.courseProfitAmount / 100));
        }
    }
}
