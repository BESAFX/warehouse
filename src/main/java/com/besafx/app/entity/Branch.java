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
import java.util.List;

@Data
@Entity
public class Branch implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "branchSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BRANCH_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "branchSequenceGenerator")
    private Long id;

    private Integer code;

    private String name;

    private String address;

    private String phone1;

    private String phone2;

    private String postalCode;

    private String mobile;

    private String fax;

    private String email;

    private String website;

    private String commericalRegisteration;

    private String licenceCode;

    private String taxCode;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String logo;

    @ManyToOne
    @JoinColumn(name = "company")
    private Company company;

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<Master> masters = new ArrayList<>();

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<Bank> banks = new ArrayList<>();

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    private List<PaymentBook> paymentBooks = new ArrayList<>();

    @JsonCreator
    public static Branch Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Branch branch = mapper.readValue(jsonString, Branch.class);
        return branch;
    }
}
