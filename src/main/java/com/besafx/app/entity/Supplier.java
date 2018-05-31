package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "supplierSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "SUPPLIER_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "supplierSequenceGenerator")
    private Long id;

    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Column(columnDefinition = "boolean default true")
    private Boolean enabled;

    @ManyToOne
    @JoinColumn(name = "contact")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "bank")
    private Bank bank;

    @Transient
    private Double balance;

    @Transient
    private Double totalDeposits;

    @Transient
    private Double totalWithdraws;

    @OneToMany(mappedBy = "supplier")
    private List<BillPurchase> billPurchases = new ArrayList<>();

    @JsonCreator
    public static Supplier Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Supplier supplier = mapper.readValue(jsonString, Supplier.class);
        return supplier;
    }

    public Double getBillsTotalPrice() {
        try {
            return this.billPurchases.stream().mapToDouble(BillPurchase::getTotalPrice).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getBillsPaid() {
        try {
            return this.billPurchases.stream().mapToDouble(BillPurchase::getPaid).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getBillsRemain() {
        try {
            return this.billPurchases.stream().mapToDouble(BillPurchase::getRemain).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
