package com.onegateafrica.Controllers;

import com.google.common.collect.Lists;
import com.onegateafrica.Controllers.utils.ImageIO;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Message;
import com.onegateafrica.Payloads.response.ConversationResponse;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.ConversationService;
import com.onegateafrica.Service.MessageService;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ConversationController {
    private final JwtUtils jwtUtils;
    private final ConsommateurService consommateurService;
    private final RemorqueurService remorqueurService;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private static Map<Long, Map<Long, List<Message>>> messages = new HashMap<Long, Map<Long, List<Message>>>();

    @Autowired
    public ConversationController(ConsommateurService consommateurService, JwtUtils jwtUtils,
                                  RemorqueurService remorqueurService, ConversationService conversationService, MessageService messageService) {
        this.consommateurService = consommateurService;
        this.jwtUtils = jwtUtils;
        this.remorqueurService = remorqueurService;
        this.conversationService = conversationService;
        this.messageService = messageService;
    }


    @PostMapping("/sendMessage")
    public ResponseEntity<?> SendMessage(@RequestHeader("Authorization") String auth,
                                         @RequestBody com.onegateafrica.Payloads.request.Message message) {
        if (message.getMessage() == null || message.getMessage().length() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid message");
        } else if (message.getReceiverId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("recieverId null");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> consommateur1;
        Optional<Consommateur> consommateur2;

        consommateur1 = consommateurService.getConsommateurByEmail(email);
        consommateur2 = consommateurService.getConsommateur(message.getReceiverId());
        if (consommateur1.isEmpty() || consommateur2.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid destination");
        }
        if (consommateur1.get().getId() == consommateur2.get().getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot send message to yourself");
        }

        Optional<Conversation> conversation = conversationService.findConversationByIds(consommateur1.get().getId(), consommateur2.get().getId());
        if (conversation.isEmpty()) {
            Conversation conversation1 = new Conversation();
            conversation1.setConsommateur1(consommateur1.get());
            conversation1.setConsommateur2(consommateur2.get());
            conversationService.save(conversation1);
            conversation = conversationService.findConversationByIds(consommateur1.get().getId(), consommateur2.get().getId());

        }
        Message newMessage = new Message();
        newMessage.setMessage(message.getMessage());
        newMessage.setTimestamp(new Date());
        newMessage.setSenderId(consommateur1.get().getId());
        newMessage.setConversation(conversation.get());
        newMessage.setSeen(false);
        newMessage.setReceived(false);
        newMessage.setRecieverId(consommateur2.get().getId());
        newMessage = messageService.save(newMessage);
        Conversation conversationUpdate = conversation.get();
        Date date = new Date();
        conversationUpdate.setLastActivity(date);
        conversationUpdate.setLastMessage(newMessage);
        if (messages.containsKey(conversation.get().getId())) {
            Map<Long, List<Message>> conversationM = messages.get(conversation.get().getId());
            if (conversationM.containsKey(consommateur2.get().getId())) {
                List<Message> receiverMessages = conversationM.get(consommateur2.get().getId());
                receiverMessages.add(newMessage);
                conversationM.put(consommateur2.get().getId(), receiverMessages);
                messages.put(conversation.get().getId(), conversationM);
            } else {
                List<Message> receiverMessages = new ArrayList<Message>();
                receiverMessages.add(newMessage);
                Map<Long, List<Message>> receiverMap = new HashMap<Long, List<Message>>();
                receiverMap.put(consommateur2.get().getId(), receiverMessages);
                messages.put(conversation.get().getId(), receiverMap);
            }
        } else {
            List<Message> receiverMessages = new ArrayList<Message>();
            receiverMessages.add(newMessage);
            Map<Long, List<Message>> receiverMap = new HashMap<Long, List<Message>>();
            receiverMap.put(consommateur2.get().getId(), receiverMessages);
            messages.put(conversation.get().getId(), receiverMap);
        }
        conversationService.save(conversationUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(newMessage);
    }

    @GetMapping("/getAllConversations")
    public ResponseEntity<?> AuthenticatedUserRealm(@RequestHeader("Authorization") String auth) {

        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            Optional<List<Conversation>> conversations = conversationService.getAllConversation(user.get().getId());
            if (conversations.isPresent()) {
                List<ConversationResponse> conversationResponses = new ArrayList<ConversationResponse>();
                for(Conversation conversation: conversations.get()){
                    conversationResponses.add(new ConversationResponse(conversation));
                }
                return ResponseEntity.status(HttpStatus.OK).body(conversationResponses);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request");
    }

    @GetMapping("/getMessagesById/{id}/{begins}/{ends}")
    public ResponseEntity<?> getMessagesById(@PathVariable(name = "id") Long id,
                                             @PathVariable(name = "begins") Integer begins,
                                             @PathVariable(name = "ends") Integer ends,
                                             @RequestHeader("Authorization") String auth) {
        System.out.println(begins + " " + ends + " " + id);
        if (!(begins != null && ends != null && id != null && begins >= 0 && ends >= begins)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameters");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            System.out.println("user is present");
            Optional<Conversation> conversation = conversationService.findById(id);
            if (conversation.isPresent()) {
                System.out.println(user.get().getId() + " " + conversation.get().getConsommateur1().getId()
                        + " " + conversation.get().getConsommateur2().getId());
                if (user.get().getId() == conversation.get().getConsommateur1().getId() ||
                        user.get().getId() == conversation.get().getConsommateur2().getId()) {
                    Optional<List<Message>> messages = messageService.filterMessage(id);
                    if (messages.isPresent()) {
                        List<Message> messages1 = messages.get();
                        if (messages1.size() > ends + 1)
                            messages1 = messages1.subList(begins, ends + 1);
                        else
                            messages1 = messages1.subList(begins, messages1.size());
                        messages1 = Lists.reverse(messages1);
                        messageService.setRecieved(user.get().getId(), id);
                        Optional<List<Conversation>> conversations = conversationService.getAllConversation(user.get().getId());
                        if (conversations.isPresent() && conversations.get().size() > 0) {
                            for (Conversation conv : conversations.get()) {
                                if(this.messages.containsKey(conv.getId()) && this.messages.get(conv.getId()).containsKey(user.get().getId())) {
                                    this.messages.get(conv.getId()).remove(user.get().getId());
                                }
                            }
                        }
                        return ResponseEntity.status(HttpStatus.OK).body(messages1);
                    }
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no messages");
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no such conversation");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID");
    }

    /*
    @GetMapping("/getNewMessagesById/{id}")
    public ResponseEntity<?> getNewMessages(@PathVariable(name = "id") Long id,
                                            @RequestHeader("Authorization") String auth) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("conversation id is null");

        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            System.out.println("user is present");
            Optional<Conversation> conversation = conversationService.findById(id);
            if (conversation.isPresent()) {
                System.out.println(user.get().getId() + " " + conversation.get().getConsommateur1().getId()
                        + " " + conversation.get().getConsommateur2().getId());
                if (user.get().getId() == conversation.get().getConsommateur1().getId() ||
                        user.get().getId() == conversation.get().getConsommateur2().getId()) {
                    Optional<List<Message>> messages = messageService.getNewMessages(user.get().getId(), id);
                    if (messages.isPresent()) {
                        System.out.println(messageService.setRecieved(user.get().getId(), id));
                        List<Message> messages1 = messages.get();
                        List<Message> updatedReceivedMassages;
                        if (receivedMessages.containsKey(id)) {
                            updatedReceivedMassages = receivedMessages.get(id);
                        } else {
                            updatedReceivedMassages = new ArrayList<Message>();
                        }
                        updatedReceivedMassages.addAll(messages1);
                        receivedMessages.put(id, updatedReceivedMassages);
                        return ResponseEntity.status(HttpStatus.OK).body(messages1);
                    }
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no messages");
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no such conversation");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID");
    }
    */
    @GetMapping("/getUpdates/{id}")
    public ResponseEntity<?> getUpdates(@PathVariable(name = "id") Long id,
                                        @RequestHeader("Authorization") String auth) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("conversation id is null");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            if (this.messages.containsKey(id)) {
                if (this.messages.get(id).containsKey(user.get().getId())) {
                    List<Message> messages = this.messages.get(id).get(user.get().getId());
                    for (Message message : messages) {
                        if (message.getReceived() == false) {
                            if (this.messages.get(id).containsKey(message.getSenderId())) {
                                message.setReceived(true);
                                Map<Long, List<Message>> senderMap = this.messages.get(id);
                                List<Message> senderMessages = senderMap.get(message.getSenderId());
                                senderMessages.add(message);
                                senderMap.put(message.getSenderId(), senderMessages);
                                this.messages.put(id, senderMap);
                            } else {
                                List<Message> senderMessages = new ArrayList<Message>();
                                message.setReceived(true);
                                senderMessages.add(message);
                                Map<Long, List<Message>> senderMap = new HashMap<Long, List<Message>>();
                                senderMap.put(message.getSenderId(), senderMessages);
                                this.messages.put(id, senderMap);
                            }
                        }
                    }
                    this.messages.get(id).remove(user.get().getId());
                    messageService.setRecieved(user.get().getId(), id);
                    return ResponseEntity.status(HttpStatus.OK).body(messages);
                }
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No updates");
    }

    @PostMapping("/setSeen/{convId}")
    public ResponseEntity<?> setSeen(@RequestHeader("Authorization") String auth,
                                     @PathVariable(name = "convId") Long convId) {
        if (convId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("null convId");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            Optional<List<Message>> messages = messageService.getUnseen(convId, user.get().getId());
            if (messages.isPresent() && messages.get().size() > 0) {
                List<Long> unseenMessagesIds = messages.get().stream().map(message -> message.getId()).collect(Collectors.toList());
                if (this.messages.containsKey(convId)) {
                    Map<Long, List<Message>> conversationM = this.messages.get(convId);
                    if (conversationM.containsKey(messages.get().get(0).getSenderId())) {
                        List<Message> receiverMessages = conversationM.get(messages.get().get(0).getSenderId());
                        int i = 0;
                        for (Message receiverMessage : receiverMessages) {
                            if (unseenMessagesIds.contains(receiverMessage.getId())) {
                                receiverMessage.setSeen(true);
                                receiverMessages.set(i, receiverMessage);
                                unseenMessagesIds.remove((receiverMessage.getId()));
                            }
                            i++;
                        }
                        for (Message message : messages.get()) {
                            if (unseenMessagesIds.contains(message.getId())) {
                                message.setSeen(true);
                                receiverMessages.add(message);
                            }
                        }
                        conversationM.put(messages.get().get(0).getSenderId(), receiverMessages);
                        this.messages.put(convId, conversationM);
                    } else {
                        List<Message> unseenMessages = messages.get().stream().map(message -> getSeenMessages(message)).collect(Collectors.toList());
                        conversationM.put(messages.get().get(0).getSenderId(), unseenMessages);
                        this.messages.put(convId, conversationM);
                    }
                } else {
                    List<Message> unseenMessages = messages.get().stream().map(message -> getSeenMessages(message)).collect(Collectors.toList());
                    Map<Long, List<Message>> conversationM = new HashMap<Long, List<Message>>();
                    conversationM.put(messages.get().get(0).getSenderId(), unseenMessages);
                    this.messages.put(convId, conversationM);
                }
            }
            messageService.setSeen(convId, user.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("done!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");
    }

    public Message getSeenMessages(Message message) {
        message.setSeen(true);
        return message;
    }

    /*@PostMapping("/setRecieved/{convId}")
    public ResponseEntity<?> SendMessage(@RequestHeader("Authorization") String auth,
                                         @PathVariable(name = "convId") Long convId) {
        if (convId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("null convId");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
            messageService.setRecieved(user.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("done!");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");
    }*/

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("id") Long id
    ) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id null");
        }
        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("image null");
        }
        if (ImageIO.uploadImage(image, image.hashCode() + id + "image" + "-" + image.getOriginalFilename())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(image.hashCode() + id + "image" + "-" + image.getOriginalFilename());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");

    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    ResponseEntity<?> getUserPictureById(
            @RequestParam("imageName") String imageName, @RequestParam("id") Long id, @RequestHeader("Authorization") String auth
    ) {

        /**
         * http://localhost:8080/api/cinPicture?cinNumber=[cinNumber]
         */
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id null");
        }
        if (imageName == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("imageName null");
        }
        System.out.println(imageName);
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Conversation> conversation = conversationService.findById(id);
        if (conversation.isPresent()) {
            if (conversation.get().getConsommateur1().getEmail().equals(email) || conversation.get().getConsommateur2().getEmail().equals(email)) {
                if (ImageIO.isImage(imageName)) {
                    try {
                        byte[] image = ImageIO.getImage(imageName);
                        return ResponseEntity.status(HttpStatus.OK).body(image);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("conversation not found");
    }

}
