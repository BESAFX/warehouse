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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillSell implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "billSellSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BILL_PURCHASE_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "billSellSequenceGenerator")
    private Long id;

    private Long code;

    private Double discount;

    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date writtenDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @OneToMany(mappedBy = "billSell")
    private List<BillSellAttach> billSellAttaches = new ArrayList<>();

    @OneToMany(mappedBy = "billSell")
    private List<BillSellProduct> billSellProducts = new ArrayList<>();

    @OneToMany(mappedBy = "billSell")
    private List<BillSellPayment> billSellPayments = new ArrayList<>();

    @JsonCreator
    public static BillSell Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BillSell billSell = mapper.readValue(jsonString, BillSell.class);
        return billSell;
    }

    public Double getTotalPrice() {
        try {
            return this.billSellProducts
                    .stream()
                    .mapToDouble(billSellProduct -> billSellProduct.getQuantity() * billSellProduct.getUnitSellPrice())
                    .sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getTotalVat() {
        try {
            return this.billSellProducts
                    .stream()
                    .mapToDouble(billSellProduct -> billSellProduct.getQuantity() * billSellProduct.getUnitVat())
                    .sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getTotalPriceAfterDiscountAndVat() {
        try {
            return (this.getTotalPrice() + this.getTotalVat()) - this.discount;
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getPaid() {
        try {
            return this.billSellPayments
                    .stream()
                    .mapToDouble(BillSellPayment::getAmount)
                    .sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getRemain() {
        try {
            return getTotalPriceAfterDiscountAndVat() - getPaid();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Date getLastPaymentDate() {
        try {
            return this.billSellPayments.stream().map(BillSellPayment::getDate).max(Date::compareTo).get();
        } catch (Exception ex) {
            return null;
        }
    }

}
