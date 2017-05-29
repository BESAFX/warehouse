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
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "permissionSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "PERMISSION_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "permissionSequenceGenerator")
    private Long id;

    private Boolean createEntity;

    private Boolean updateEntity;

    private Boolean deleteEntity;

    private Boolean reportEntity;

    @ManyToOne
    @JoinColumn(name = "screen")
    private Screen screen;

    @JsonCreator
    public static Permission Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Permission permission = mapper.readValue(jsonString, Permission.class);
        return permission;
    }
}
