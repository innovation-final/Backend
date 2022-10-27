package com.innovation.stockstock.chatRedis;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    ChatRoom findByName(String name);
}
