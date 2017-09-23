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
public class MasterCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "masterCategorySequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "MASTER_CATEGORY_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "masterCategorySequenceGenerator")
    private Long id;

    private Integer code;

    private String name;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @OneToMany(mappedBy = "masterCategory", fetch = FetchType.LAZY)
    private List<Master> masters = new ArrayList<>();

    @JsonCreator
    public static MasterCategory Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MasterCategory masterCategory = mapper.readValue(jsonString, MasterCategory.class);
        return masterCategory;
    }

}
