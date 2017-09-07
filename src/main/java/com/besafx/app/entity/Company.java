package com.besafx.app.entity;
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

@Data
@Entity
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "companySequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "COMPANY_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "companySequenceGenerator")
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private String name;

    @JsonView(Views.Summery.class)
    private String address;

    @JsonView(Views.Summery.class)
    private String phone;

    @JsonView(Views.Summery.class)
    private String mobile;

    @JsonView(Views.Summery.class)
    private String fax;

    @JsonView(Views.Summery.class)
    private String email;

    @JsonView(Views.Summery.class)
    private String website;

    @JsonView(Views.Summery.class)
    private String commericalRegisteration;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String logo;

    @ManyToOne
    @JoinColumn(name = "manager")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person manager;

    @JsonCreator
    public static Company Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Company company = mapper.readValue(jsonString, Company.class);
        return company;
    }
}
