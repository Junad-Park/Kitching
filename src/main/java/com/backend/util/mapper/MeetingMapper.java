package com.backend.util.mapper;

import com.backend.dto.meeting.common.ImageDTO;
import com.backend.dto.meeting.common.LocationDTO;
import com.backend.dto.meeting.common.TimeDTO;
import com.backend.entity.meeting.Meeting;
import com.backend.entity.meeting.MeetingImage;
import com.backend.entity.meeting.embeddable.MeetingAddress;
import com.backend.entity.meeting.embeddable.MeetingTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeetingMapper {

    public static MeetingTime toMeetingTime(TimeDTO timeDTO) {
        return MeetingTime.builder()
                .startTime(timeDTO.getStartTime())
                .endTime(timeDTO.getEndTime())
                .build();
    }

    public static MeetingAddress toMeetingAddress(LocationDTO locationDTO) {
        return MeetingAddress.builder()
                .location(locationDTO.getLocation())
                .detailLocation(locationDTO.getDetailLocation())
                .build();
    }

    public static List<MeetingImage> toMeetingImages(Meeting meeting, ImageDTO imageDTO) {
        List<MeetingImage> meetingImages = new ArrayList<>();

        Optional.ofNullable(imageDTO.getThumbnailUrl())
                .ifPresent(url -> meetingImages.add(createMeetingImage(meeting, url, true)));

        Optional.ofNullable(imageDTO.getImageUrls())
                .ifPresent(urls -> urls.forEach(url ->
                        meetingImages.add(createMeetingImage(meeting, url, false))));

        return meetingImages;
    }

    private static MeetingImage createMeetingImage(Meeting meeting, String imageUrl, boolean isThumbnail) {
        return MeetingImage.builder()
                .imageUrl(imageUrl)
                .isThumbnail(isThumbnail)
                .meeting(meeting)
                .build();
    }
}

