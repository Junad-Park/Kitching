package com.backend.dto.meeting.response.read;

import com.backend.dto.meeting.response.read.output.HostOutput;
import com.backend.dto.meeting.response.read.output.InfoOutput;
import com.backend.dto.meeting.response.read.output.LocationOutput;
import com.backend.dto.meeting.response.read.output.TimeOutput;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MeetingResponse {
    private final long id;
    private final String thumbnailUrl;
    private final List<String> hashtags;

    @JsonProperty("info")
    private final InfoOutput infoOutput;

    @JsonProperty("location")
    private final LocationOutput locationOutput;

    @JsonProperty("time")
    private final TimeOutput timeOutput;

    @JsonProperty("host")
    private final HostOutput hostOutput;
}
