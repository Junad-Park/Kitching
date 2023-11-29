package com.backend.controller.chat;

import com.backend.dto.chat.request.create.ChatCreateRequest;
import com.backend.service.chat.RoomService;
import com.backend.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations template;

    private final ChatService chatService;
    private final RoomService roomService;

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatCreateRequest chatRequest, SimpMessageHeaderAccessor headerAccessor) {

        roomService.addUser(chatRequest.getRoomId(), chatRequest.getSenderId());

        // TODO : 서버가 껐다 켜지면 채팅방, 채팅 기록 등은 디비에 저장 되어 있는데, 소켓 연결은? 저장 해 뒀다가 서버가 다시 켜질떄 연결 다시 시켜야 하나?
        // socket session 에 userId, roomId를 저장
        headerAccessor.getSessionAttributes().put("userId", chatRequest.getSenderId());
        headerAccessor.getSessionAttributes().put("roomId", chatRequest.getRoomId());


        chatRequest.updateMessage(chatRequest.getSender() + " 님이 입장 했습니다.");
        sendMessage(chatRequest);
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatCreateRequest chatRequest) {
        log.info("CHAT {}", chatRequest);
        template.convertAndSend("/sub/chat/room/" + chatRequest.getRoomId(), chatRequest);
        chatService.saveMessage(chatRequest, chatRequest.getSenderId());
    }

    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        ChatCreateRequest chat = chatService.createLeaveMessage(userId, roomId);
        roomService.delUser(roomId, userId);

        sendMessage(chat);
    }
}