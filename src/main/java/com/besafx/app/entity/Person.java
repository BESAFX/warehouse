package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
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
    @JsonView(Views.Summery.class)
    private Long id;

    @JsonView(Views.Summery.class)
    private String email;

    @JsonView(Views.Summery.class)
    private String password;

    @JsonView(Views.Summery.class)
    private Boolean enabled;

    @JsonView(Views.Summery.class)
    private Boolean tokenExpired;

    @JsonView(Views.Summery.class)
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "branch")
    @JsonIgnoreProperties(value = {"masters"} , allowSetters = true)
    @JsonView(Views.Summery.class)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "team")
    @JsonView(Views.Summery.class)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "contact")
    @JsonView(Views.Summery.class)
    private Contact contact;

    @JsonCreator
    public static Person Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(jsonString, Person.class);
        return person;
    }
}
