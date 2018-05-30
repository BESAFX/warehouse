package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class BillPurchaseAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "billPurchaseAttachSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BILL_PURCHASE_ATTACH_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "billPurchaseAttachSequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attach")
    private Attach attach;

    @ManyToOne
    @JoinColumn(name = "billPurchase")
    private BillPurchase billPurchase;

    @JsonCreator
    public static BillPurchaseAttach Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BillPurchaseAttach attachment = mapper.readValue(jsonString, BillPurchaseAttach.class);
        return attachment;
    }
}
