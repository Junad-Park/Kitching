package com.backend.dto.meeting.response.output;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostOutput {
    private String name;
    private String profileImage;
}
