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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractPremium implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "contractPremiumSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "CONTRACT_PREMIUM_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "contractPremiumSequenceGenerator")
    private Long id;

    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "contract")
    private Contract contract;

    @JsonCreator
    public static ContractPremium Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ContractPremium contractPremium = mapper.readValue(jsonString, ContractPremium.class);
        return contractPremium;
    }
}
