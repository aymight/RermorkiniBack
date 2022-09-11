package com.onegateafrica.Payloads.response;

import com.onegateafrica.Entities.Conversation;
import lombok.Data;

import java.util.Date;

@Data
public class ConversationResponse extends Conversation {
    private Date now;
    public ConversationResponse(Conversation conversation){
        this.setConsommateur1(conversation.getConsommateur1());
        this.setConsommateur2(conversation.getConsommateur2());
        this.setId(conversation.getId());
        this.setLastActivity(conversation.getLastActivity());
        this.now = new Date();
        this.setLastMessage(conversation.getLastMessage());
    }
}
