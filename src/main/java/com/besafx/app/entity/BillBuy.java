package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class BillBuy implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "billBuySequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BILL_BUY_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "billBuySequenceGenerator")
    private Long id;

    private Long code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Double amount;

    private String fromName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "bill_buy_type")
    private BillBuyType billBuyType;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @JsonCreator
    public static BillBuy Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BillBuy billBuy = mapper.readValue(jsonString, BillBuy.class);
        return billBuy;
    }
}
