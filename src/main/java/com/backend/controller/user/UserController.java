package com.backend.controller.user;

import com.backend.annotation.CheckUserNotNull;
import com.backend.annotation.CurrentMember;
import com.backend.dto.common.ResponseMessage;
import com.backend.dto.common.SuccessResponse;
import com.backend.dto.user.request.update.UserUpdateRequest;
import com.backend.dto.user.response.read.MailResponse;
import com.backend.dto.user.response.read.UserResponse;
import com.backend.dto.user.response.update.UserUpdateResponse;
import com.backend.entity.user.User;
import com.backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    @CheckUserNotNull
    ResponseEntity<UserResponse.MyPage> getMyPage(@CurrentMember User user) {
        UserResponse.MyPage response = userService.getMyPage(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/nickname")
    @CheckUserNotNull
    ResponseEntity<UserUpdateResponse.Nickname> updateNickName(@RequestBody UserUpdateRequest.Nickname request,
                                                               @CurrentMember User user) {
        Long id = userService.updateNickname(request, user.getId());
        UserUpdateResponse.Nickname response = UserUpdateResponse.Nickname.success(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile-image")
    @CheckUserNotNull
    ResponseEntity<UserUpdateResponse.ProfileImage> updateProfileImage(
            @RequestBody UserUpdateRequest.ProfileImage request,
            @CurrentMember User user) {
        Long id = userService.updateProfileImage(request, user.getId());
        UserUpdateResponse.ProfileImage response = UserUpdateResponse.ProfileImage.success(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notification")
    @CheckUserNotNull
    ResponseEntity<UserResponse.Notification> getUserNotification(@CurrentMember User user) {
        UserResponse.Notification response = userService.getUserNotification(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/notification")
    @CheckUserNotNull
    ResponseEntity<UserUpdateResponse.Notification> updateUserNotification(
            @RequestBody UserUpdateRequest.Notification request,
            @CurrentMember User user) {
        Long id = userService.updateUserNotification(request, user.getId());
        UserUpdateResponse.Notification response = UserUpdateResponse.Notification.success(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("email/verification")
    @CheckUserNotNull
    public ResponseEntity<MailResponse> getIsVerification(@CurrentMember User user){
        MailResponse response = userService.isVerification(user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/verification")
    @CheckUserNotNull
    public ResponseEntity<SuccessResponse> sendMessage(@CurrentMember User user,
                                                       @RequestParam("email") @Valid String email) {
        userService.sendCodeToEmail(user.getId(), email);
        SuccessResponse response = SuccessResponse.create(user.getId(), ResponseMessage.MAIL_SEND_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/email/verification")
    @CheckUserNotNull
    public ResponseEntity<SuccessResponse> verificationEmail(@CurrentMember User user,
                                                             @RequestParam("email") @Valid  String email,
                                                             @RequestParam("code") String authCode) {
        Long id = userService.verifiedCode(user.getId(), email, authCode);
        SuccessResponse response = SuccessResponse.create(id, ResponseMessage.MAIL_VERIFICATION_SUCCESS);
        return ResponseEntity.ok(response);
    }
}
