package com.besafx.app.entity;

import com.besafx.app.entity.enums.StudentCondition;
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
public class AccountCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "accountConditionSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ACCOUNT_CONDITION_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "accountConditionSequenceGenerator")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @Enumerated(EnumType.STRING)
    private StudentCondition studentCondition;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @JsonCreator
    public static AccountCondition Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountCondition accountCondition = mapper.readValue(jsonString, AccountCondition.class);
        return accountCondition;
    }
}
