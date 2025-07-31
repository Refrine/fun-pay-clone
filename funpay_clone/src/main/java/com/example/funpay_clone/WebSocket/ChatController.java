package com.example.funpay_clone.WebSocket;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.funpay_clone.models.ChatMessage;
import com.example.funpay_clone.models.User;
import com.example.funpay_clone.repository.ChatMessageRepository;
import com.example.funpay_clone.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send/{productId}")
    @SendTo("/topic/chat/{productId}")
    public ChatMessageDTO sendMessage(
            @Payload ChatMessageDTO chatMessageDTO,
            @DestinationVariable Long productId,
            Principal principal) {
        
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChatMessage message = new ChatMessage();
        message.setProductId(productId);
        message.setSender(sender);
        message.setContent(chatMessageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        
        chatMessageRepository.save(message);
        
        return convertToDTO(message);
    }

    @GetMapping("/api/chat/{productId}/history")
    @ResponseBody
    public List<ChatMessageDTO> getChatHistory(@PathVariable Long productId) {
        return chatMessageRepository.findByProductIdOrderByTimestampAsc(productId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setProductId(message.getProductId());
        dto.setSender(message.getSender().getUsername());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDTO {
        private Long id;
        private Long productId;
        private String sender;
        private String content;
        private LocalDateTime timestamp;
    }
}