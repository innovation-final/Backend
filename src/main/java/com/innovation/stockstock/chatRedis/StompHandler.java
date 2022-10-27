package com.innovation.stockstock.chatRedis;

import com.innovation.stockstock.chatRedis.redis.RedisPub;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final RedisPub redisPub;
    private final ChannelTopic channelTopic;
    private final ChatRoomRepository chatRoomRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행.
    @Override
    @Transactional
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        LocalTime now = LocalTime.now();
        ChatRoom chatRoom = chatRoomRepository.findByName("chatroom");

        if (StompCommand.CONNECT == accessor.getCommand()) { // 채팅룸 구독 요청
            // 기존의 채팅방을 불러와서 업데이트
            chatRoom.updateNum(true);
            ChatMessage chatMessage = ChatMessage.builder()
                    .type(MessageType.ENTER)
                    .sendTime(now.toString())
                    .nickName("서버알림")
                    .userId(0L)
                    .imageUrl("null")
                    .message("입장")
                    .userCnt(chatRoom.userCnt)
                    .build();
            redisPub.publish(channelTopic, chatMessage);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 종료
            chatRoom.updateNum(false);
            ChatMessage chatMessage = ChatMessage.builder()
                    .type(MessageType.QUIT)
                    .sendTime(now.toString())
                    .nickName("서버알림")
                    .userId(0L)
                    .imageUrl("null")
                    .message("퇴장")
                    .userCnt(chatRoom.userCnt)
                    .build();
            redisPub.publish(channelTopic, chatMessage);
        }
        return message;
    }
}
