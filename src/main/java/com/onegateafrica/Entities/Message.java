package com.onegateafrica.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long recieverId;

    private String message;

    private Date timestamp;

    private Boolean received;

    private Boolean seen;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="id_conversation", nullable=false )
    private Conversation conversation;

}
