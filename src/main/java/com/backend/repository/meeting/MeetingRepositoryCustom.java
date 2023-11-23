package com.backend.repository.meeting;

import com.backend.entity.meeting.Meeting;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingRepositoryCustom {
    Page<Meeting> findAllWithDetails(Pageable pageable);

    Optional<Meeting> findMeetingWithDetailsById(Long id);
}

