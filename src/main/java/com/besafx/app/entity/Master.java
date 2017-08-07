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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Master implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "masterSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "MASTER_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "masterSequenceGenerator")
    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class, Views.BranchMaster.class})
    private Long id;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class, Views.BranchMaster.class})
    private Integer code;

    @JsonView(value = {Views.Summery.class, Views.BranchMaster.class})
    private String name;

    @JsonView(Views.Summery.class)
    private String period;

    @ManyToOne
    @JoinColumn(name = "branch")
    @JsonIgnoreProperties(value = {"manager", "masters", "banks"}, allowSetters = true)
    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class})
    private Branch branch;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Summery.class)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Person lastPerson;

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"master"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"master", "calls"}, allowSetters = true)
    private List<Offer> offers = new ArrayList<>();

    @JsonCreator
    public static Master Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Master master = mapper.readValue(jsonString, Master.class);
        return master;
    }

}
