package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "courseSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "COURSE_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "courseSequenceGenerator")
    private Long id;

    private Integer code;

    private String instructor;

    private String companyName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "master")
    private Master master;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    @JsonIgnoreProperties(value = {"branch"}, allowSetters = true)
    private Person lastPerson;

    @JsonCreator
    public static Course Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Course course = mapper.readValue(jsonString, Course.class);
        return course;
    }

}
