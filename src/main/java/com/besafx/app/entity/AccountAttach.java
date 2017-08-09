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
public class AccountAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "accountAttachSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ACCOUNT_ATTACH_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "accountAttachSequenceGenerator")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account")
    @JsonIgnoreProperties(value = {"payments", "accountAttaches"}, allowSetters = true)
    @JsonView(Views.Summery.class)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "attach")
    @JsonView(Views.Summery.class)
    private Attach attach;

    @ManyToOne
    @JoinColumn(name = "attachType")
    @JsonView(Views.Summery.class)
    private AttachType attachType;

    @JsonCreator
    public static AccountAttach Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountAttach billBuyType = mapper.readValue(jsonString, AccountAttach.class);
        return billBuyType;
    }
}
