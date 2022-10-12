package com.innovation.stockstock.chatRedis;

import com.innovation.stockstock.chatRedis.redis.RedisPub;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ChannelTopic channelTopic;
    private final RedisPub redisPub;

    // redis에 메시지 발행
    public void sendChat(ChatMessage chatMessage, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        chatMessage.setImageUrl(member.getProfileImg());
        chatMessage.setNickName(member.getNickname());
        chatMessage.setUserId(member.getId());
        redisPub.publish(channelTopic, chatMessage);
    }

}
