package com.besafx.app.entity;

import com.besafx.app.util.DateConverter;
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

    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    private String coursePaymentType;

    @Column(columnDefinition = "Decimal(10,2) default '0.00'")
    private Double coursePrice = 0.0;

    @Column(columnDefinition = "Decimal(10,2) default '0.00'")
    private Double courseDiscountAmount = 0.0;

    @Column(columnDefinition = "Decimal(10,2) default '0.00'")
    private Double courseProfitAmount = 0.0;

    @Column(columnDefinition = "Decimal(10,2) default '0.00'")
    private Double courseCreditAmount = 0.0;

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
    private Person lastPerson;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountAttach> accountAttaches = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountCondition> accountConditions = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountNote> accountNotes = new ArrayList<>();

    @JsonCreator
    public static Account Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(jsonString, Account.class);
        return account;
    }

    public Double getRequiredPrice() {
        try {
            if (this.coursePaymentType.equals("نقدي")) {
                return (this.coursePrice - (this.coursePrice * this.courseDiscountAmount / 100));
            } else {
                return (this.coursePrice + (this.coursePrice * this.courseProfitAmount / 100));
            }
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getPaidPrice() {
        try {
            return this.payments
                    .stream()
                    .filter(payment -> payment.getType().equals("ايرادات اساسية"))
                    .mapToDouble(Payment::getAmountNumber)
                    .sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getRemainPrice() {
        try {
            return this.getRequiredPrice() - this.getPaidPrice();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public String getKey() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(DateConverter.getYearShortcut(this.getRegisterDate()));
            builder.append("-");
            builder.append(this.getCourse().getMaster().getBranch().getCode());
            builder.append("-");
            builder.append(this.getCourse().getMaster().getCode());
            builder.append("-");
            builder.append(this.getCourse().getCode());
            builder.append("-");
            builder.append(this.getCode());
            return builder.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getKeyRTL() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(this.getCode());
            builder.append("-");
            builder.append(this.getCourse().getCode());
            builder.append("-");
            builder.append(this.getCourse().getMaster().getCode());
            builder.append("-");
            builder.append(this.getCourse().getMaster().getBranch().getCode());
            builder.append("-");
            builder.append(DateConverter.getYearShortcut(this.getRegisterDate()));
            return builder.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getName() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(this.student.getContact().getFirstName());
            builder.append(" ");
            builder.append(this.student.getContact().getSecondName());
            builder.append(" ");
            builder.append(this.student.getContact().getThirdName());
            builder.append(" ");
            builder.append(this.student.getContact().getForthName());
            return builder.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
