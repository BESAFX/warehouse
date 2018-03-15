package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.stream.Collectors;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "personSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PERSON_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "personSequenceGenerator")
    private Long id;

    private String email;

    private String password;

    private Boolean enabled;

    private Boolean tokenExpired;

    private Boolean active;

    @JsonIgnore
    private String hiddenPassword;

    private Date lastLoginDate;

    private String ipAddress;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String options;

    @ManyToOne
    @JoinColumn(name = "team")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "contact")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    private List<BranchAccess> branchAccesses = new ArrayList<>();

    @JsonCreator
    public static Person Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(jsonString, Person.class);
        return person;
    }

    public List<Branch> getBranches() {
        try {
            List<Branch> list = new ArrayList<>();
            list.add(this.branch);
            list.addAll(this.branchAccesses.stream().map(BranchAccess::getBranch).collect(Collectors.toList()));
            return list.stream().distinct().collect(Collectors.toList());
        } catch (Exception ex) {
            return null;
        }
    }

    public String getName() {
        try {
            return this.contact.getFirstName().concat(" ").concat(this.contact.getForthName());
        } catch (Exception ex) {
            return "";
        }
    }
}
