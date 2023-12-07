package com.backend.dto.registration.response;

import com.backend.dto.common.ResponseMessage;
import com.backend.dto.common.ResponseStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcceptResponse {
    private final List<Long> registrationIds;

    private final String roomId;

    @Builder.Default
    private final ResponseStatus status = ResponseStatus.SUCCESS;

    @Builder.Default
    private final LocalDateTime timeStamp = LocalDateTime.now();

    @Builder.Default
    private final String message = ResponseMessage.APPLY_ACCEPT.getMessage();
}
