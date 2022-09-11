package com.onegateafrica.Payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Long receiverId;

    private String message;

}
