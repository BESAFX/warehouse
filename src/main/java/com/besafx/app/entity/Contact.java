package com.besafx.app.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonView;
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
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "contactSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "CONTACT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "contactSequenceGenerator")
    @JsonView(value = {Views.Summery.class, Views.PersonComboBox.class})
    private Long id;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class, Views.PersonComboBox.class})
    private String firstName;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class})
    private String secondName;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class})
    private String thirdName;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class, Views.PersonComboBox.class})
    private String forthName;

    @JsonView(Views.Summery.class)
    private String address;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class})
    private String mobile;

    @JsonView(Views.Summery.class)
    private String phone;

    @JsonView(Views.Summery.class)
    private String nationality;

    @JsonView(value = {Views.Summery.class, Views.AccountComboBox.class})
    private String identityNumber;

    @JsonView(Views.Summery.class)
    private String identityLocation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(Views.Summery.class)
    private String photo;

    @JsonView(Views.Summery.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date identityStartDate;

    @JsonView(Views.Summery.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @JsonView(Views.Summery.class)
    private String birthLocation;

    @JsonView(Views.Summery.class)
    private String qualification;

    @JsonCreator
    public static Contact Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Contact contact = mapper.readValue(jsonString, Contact.class);
        return contact;
    }
}
