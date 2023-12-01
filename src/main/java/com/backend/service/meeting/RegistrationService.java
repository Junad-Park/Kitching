package com.backend.service.meeting;

import com.backend.annotation.CheckIsOwner;
import com.backend.dto.registration.response.RegistrationResponse;
import com.backend.dto.registration.response.RegistrationResponseList;
import com.backend.entity.meeting.Meeting;
import com.backend.entity.meeting.MeetingRegistration;
import com.backend.entity.meeting.RegistrationRole;
import com.backend.entity.meeting.RegistrationStatus;
import com.backend.entity.user.User;
import com.backend.exception.AlreadyExistsException;
import com.backend.exception.BadRequestException;
import com.backend.exception.ErrorMessages;
import com.backend.exception.NotFoundException;
import com.backend.repository.meeting.MeetingRegistrationRepository;
import com.backend.repository.meeting.MeetingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {
    private final MeetingRegistrationRepository meetingRegistrationRepository;
    private final MeetingRepository meetingRepository;

    public void createOwnerStatus(Meeting meeting, User user) {
        MeetingRegistration registration = buildRegistration(meeting, user, RegistrationRole.OWNER,
                RegistrationStatus.ACCEPTED);
        meetingRegistrationRepository.save(registration);
    }

    public Long applyMeeting(Long meetingId, User user) {
        Meeting meeting = getMeeting(meetingId);
        checkRegistrationDuplication(meeting, user);
        MeetingRegistration registration = buildRegistration(meeting, user, RegistrationRole.MEMBER,
                RegistrationStatus.PENDING);
        return saveRegistration(registration).getId();
    }

    public Long cancelMeeting(Long meetingId, User user) {
        Meeting meeting = getMeeting(meetingId);
        checkIsNotOwner(meeting, user);
        MeetingRegistration registration = findRegistration(meeting, user);
        deleteRegistration(registration);
        return registration.getId();
    }

    @CheckIsOwner
    @Transactional(readOnly = true)
    public RegistrationResponseList getRegistration(Long meetingId, User user) {
        Meeting meeting = getMeeting(meetingId);
        List<RegistrationResponse> responseList = fetchRegistrationResponses(meeting);
        return new RegistrationResponseList(responseList.size(), responseList);
    }

    //TODO [HJ] 로직 점검 , 예외처리 및 Test 필요
    @CheckIsOwner
    public Long acceptApply(Long registrationId, Long meetingId, User user) {
        MeetingRegistration registration = meetingRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.REGISTRATION_NOT_FOUND));

        registration.updateStatus(RegistrationStatus.ACCEPTED);
        meetingRegistrationRepository.save(registration);

        return registration.getId();
    }

    @CheckIsOwner
    public Long rejectApply(Long registrationId, Long meetingId, User user) {
        MeetingRegistration registration = meetingRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.REGISTRATION_NOT_FOUND));

        registration.updateStatus(RegistrationStatus.REJECTED);
        meetingRegistrationRepository.save(registration);

        return registration.getId();
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.MEETING_NOT_FOUND));
    }

    private void checkRegistrationDuplication(Meeting meeting, User user) {
        if (meetingRegistrationRepository.existsByMeetingAndUser(meeting, user)) {
            throw new AlreadyExistsException(ErrorMessages.ALREADY_REGISTRATER);
        }
    }

    private void checkIsNotOwner(Meeting meeting, User user) {
        if (meeting.isUserOwner(user)) {
            throw new BadRequestException(ErrorMessages.CANNOT_CANCEL_OWNER_REGISTRATION);
        }
    }

    private MeetingRegistration findRegistration(Meeting meeting, User user) {
        return meetingRegistrationRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.REGISTRATION_NOT_FOUND));
    }

    private void deleteRegistration(MeetingRegistration registration) {
        meetingRegistrationRepository.delete(registration);
    }

    private MeetingRegistration buildRegistration(Meeting meeting, User user, RegistrationRole role,
                                                  RegistrationStatus status) {
        return MeetingRegistration.builder()
                .meeting(meeting)
                .user(user)
                .role(role)
                .status(status)
                .build();
    }

    private MeetingRegistration saveRegistration(MeetingRegistration registration) {
        return meetingRegistrationRepository.save(registration);
    }

    private List<RegistrationResponse> fetchRegistrationResponses(Meeting meeting) {
        return meetingRegistrationRepository.findByMeeting(meeting).stream()
                .filter(MeetingRegistration::isNotOwner)
                .map(this::convertToRegistrationResponse)
                .toList();
    }

    private RegistrationResponse convertToRegistrationResponse(MeetingRegistration registration) {
        User registeredUser = registration.getUser();
        return RegistrationResponse.builder()
                .id(registration.getId())
                .nickname(registeredUser.getNickname())
                .profileImage(registeredUser.getProfileImage())
                .temperature(registeredUser.getTemperature())
                .participateStatus(registration.getStatus())
                .build();
    }
}
