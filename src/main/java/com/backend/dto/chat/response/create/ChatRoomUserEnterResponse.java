package com.backend.dto.chat.response.create;

import com.backend.dto.common.BaseResponse;
import com.backend.dto.common.ResponseMessage;
import com.backend.dto.common.ResponseStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChatRoomUserEnterResponse extends BaseResponse {
    private final Long id;

    public static ChatRoomUserEnterResponse success(Long id) {
        return ChatRoomUserEnterResponse.builder()
                .id(id)
                .status(ResponseStatus.SUCCESS)
                .message(ResponseMessage.CHAT_ROOM_USER_ENTER_SUCCESS.getMessage())
                .build();
    }
}
