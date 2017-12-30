package com.besafx.app.entity;
import com.besafx.app.entity.enums.PaymentMethod;
import com.besafx.app.util.ArabicLiteralNumberParser;
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
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "paymentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PAYMENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "paymentSequenceGenerator")
    private Long id;

    private Long code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    private String amountString;

    private Double amountNumber;

    private String type;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Long paymentMethodCode;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @OneToOne
    @JoinColumn(name = "attach")
    private Attach attach;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @JsonCreator
    public static Payment Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Payment payment = mapper.readValue(jsonString, Payment.class);
        return payment;
    }

    public Double getTax() {
        try{
            return (this.getAmountNumber() * 5 ) / 100;
        }catch (Exception ex){
            return 0.0;
        }
    }

    public Double getAmountTax() {
        try{
            return this.amountNumber + this.getTax();
        }catch (Exception ex){
            return 0.0;
        }
    }

    public String getAmountTaxString() {
        try{
            return ArabicLiteralNumberParser.literalValueOf(this.getAmountTax());
        }catch (Exception ex){
            return "";
        }
    }

    public String getPaymentMethodInArabic() {
        try {
            return this.paymentMethod.getName();
        } catch (Exception ex) {
            return "";
        }
    }
}
