package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Data
@Entity
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "studentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "STUDENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "studentSequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact")
    private Contact contact;

    @JsonCreator
    public static Student Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(jsonString, Student.class);
        return student;
    }
}
