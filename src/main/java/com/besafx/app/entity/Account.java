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
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    private Person lastPerson;

    @JsonCreator
    public static Account Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(jsonString, Account.class);
        return account;
    }
}
