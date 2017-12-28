package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BranchAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "branchAccessSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BRANCH_ACCESS_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "branchAccessSequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @JsonCreator
    public static BranchAccess Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BranchAccess branchAccess = mapper.readValue(jsonString, BranchAccess.class);
        return branchAccess;
    }
}
