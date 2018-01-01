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
    private Long id;

    private Integer code;

    private String name;

    private String period;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @ManyToOne
    @JoinColumn(name = "master_category")
    private MasterCategory masterCategory;

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<Offer> offers = new ArrayList<>();

    @JsonCreator
    public static Master Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Master master = mapper.readValue(jsonString, Master.class);
        return master;
    }

}
