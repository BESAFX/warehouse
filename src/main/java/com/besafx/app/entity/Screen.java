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
public class Screen implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "screenSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "SCREEN_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "screenSequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScreenCode code;

    private Integer key;

    private String name;


    @JsonCreator
    public static Screen Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Screen screen = mapper.readValue(jsonString, Screen.class);
        return screen;
    }

    public enum ScreenCode {
        COMPANY,
        BRANCH,
        MASTER,
        OFFER,
        COURSE,
        STUDENT,
        ACCOUNT,
        PAYMENT,
        PERSON,
        TEAM,
        BANK,
        DEPOSIT,
        WITHDRAW,
        BILL_BUY,
        BILL_BUY_TYPE,
    }

}
