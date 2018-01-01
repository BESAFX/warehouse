package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class PaymentBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "paymentBookSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PAYMENT_BOOK_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "paymentBookSequenceGenerator")
    private Long id;

    private Long code;

    private Long fromCode;

    private Long toCode;

    private Long maxCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Person lastPerson;

    @OneToMany(mappedBy = "paymentBook", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @JsonCreator
    public static PaymentBook Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PaymentBook paymentBook = mapper.readValue(jsonString, PaymentBook.class);
        return paymentBook;
    }
}
