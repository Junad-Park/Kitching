package com.backend.dto.chat.response.delete;

import com.backend.dto.bases.BaseResponse;
import com.backend.dto.bases.ResponseMessage;
import com.backend.dto.bases.ResponseStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChatRoomUserExitResponse extends BaseResponse {
    private final Long id;
    public static ChatRoomUserExitResponse success(Long id) {
        return ChatRoomUserExitResponse.builder()
                .id(id)
                .status(ResponseStatus.SUCCESS)
                .message(ResponseMessage.CHAT_ROOM_USER_EXIT_SUCCESS.getMessage())
                .build();
    }
}
