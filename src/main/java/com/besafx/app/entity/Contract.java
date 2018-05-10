package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "contractSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "CONTRACT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "contractSequenceGenerator")
    private Long id;

    private Long code;

    private Double paperFees;

    private Double commissionFees;

    private Double lawFees;

    @ManyToOne
    @JoinColumn(name = "seller")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "sponsor1")
    private Customer sponsor1;

    @ManyToOne
    @JoinColumn(name = "sponsor2")
    private Customer sponsor2;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @OneToMany(mappedBy = "contract")
    private List<ContractProduct> contractProducts = new ArrayList<>();

    @JsonCreator
    public static Contract Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Contract contract = mapper.readValue(jsonString, Contract.class);
        return contract;
    }

    public Double getTotalPrice(){
        try{
            return this.contractProducts
                    .stream()
                    .mapToDouble(contractProduct -> contractProduct.getQuantity() * contractProduct.getUnitSellPrice())
                    .sum();
        }catch (Exception ex){
            return 0.0;
        }
    }

    public Double getPaid(){
        try{
            return 0.0;
        }catch (Exception ex){
            return 0.0;
        }
    }

    public Double getRemain(){
        try{
            return getTotalPrice() - getPaid();
        }catch (Exception ex){
            return 0.0;
        }
    }
}
