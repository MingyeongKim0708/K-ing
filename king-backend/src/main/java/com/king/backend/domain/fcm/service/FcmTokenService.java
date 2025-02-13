package com.king.backend.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.king.backend.domain.cast.repository.CastRepository;
import com.king.backend.domain.content.repository.ContentRepository;
import com.king.backend.domain.favorite.repository.FavoriteRepository;
import com.king.backend.domain.fcm.entity.FcmToken;
import com.king.backend.domain.fcm.repository.FcmTokenRepository;
import com.king.backend.domain.user.dto.domain.OAuth2UserDTO;
import com.king.backend.domain.user.entity.User;
import com.king.backend.domain.user.errorcode.UserErrorCode;
import com.king.backend.domain.user.repository.UserRepository;
import com.king.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CastRepository castRepository;
    private final ContentRepository contentRepository;

    public void registerToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2UserDTO oauthUser = (OAuth2UserDTO) authentication.getPrincipal();
        Long userId = Long.parseLong(oauthUser.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        fcmTokenRepository.findByUserAndToken(user, token)
                .ifPresentOrElse(existing -> {
                    // 존재하면 업데이트 시각만 변경
                }, () -> {
                    FcmToken newToken = new FcmToken();
                    newToken.setUser(user);
                    newToken.setToken(token);
                    fcmTokenRepository.save(newToken);
                });
    }

    public String sendMessageByToken(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        return FirebaseMessaging.getInstance().send(message);
    }

    public void deleteToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2UserDTO oauthUser = (OAuth2UserDTO) authentication.getPrincipal();
        Long userId = Long.parseLong(oauthUser.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Optional<FcmToken> tokenRecord = fcmTokenRepository.findByUserAndToken(user, token);
        tokenRecord.ifPresent(fcmTokenRepository::delete);
    }

    /**
     * 매일 08:00, 16:00에 실행되어, 즐겨찾기한 연예인/컨텐츠에 대한 알림을 전송합니다.
     */
    @Scheduled(cron = "0 0 8,16 * * *", zone = "Asia/Seoul")
    public void sendScheduledNotifications() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        OAuth2UserDTO oauthUser = (OAuth2UserDTO) authentication.getPrincipal();
//        String language = oauthUser.getLanguage();
//        Long userId = Long.parseLong(oauthUser.getName());
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
//
//        List<Favorite> favorites = favoriteRepository.findAllByUser(user);
//        if (favorites == null || favorites.isEmpty()) {
//            return;
//        }
//
//        Random random = new Random();
//        Favorite randomFav = favorites.get(random.nextInt(favorites.size()));
//
//        String title;
//        String body;
//        if ("cast".equalsIgnoreCase(randomFav.getType())) {
//            Cast cast = castRepository.findById(randomFav.getTargetId()).orElseThrow(() -> new CustomException(CastErrorCode.CAST_NOT_FOUND));
//            CastTranslation castTrans = cast.getTranslation(language);
//            String name = castTrans.getName();
//
//        } else if ("content".equalsIgnoreCase(randomFav.getType())) {
//            title = "콘텐츠 알림";
//            // 예시: "놀라운 토요일에 나온 000장소를 방문해보세요."
//            body = "놀라운 토요일에 나온 " + randomFav.getPlaceName() + "를 방문해보세요!";
//        } else {
//            title = "새로운 알림";
//            body = randomFav.getPlaceName() + "에 관한 알림이 있습니다.";
//        }

//        List<Favorite> favorites = favoriteRepository.findAllByUser(user);
//        sendMessageByToken(token, title, body)
//
//        List<FcmToken> tokens = fcmTokenRepository.findByUser(user);
//
//        fcmService.sendMessageByToken(token.getToken(), "새 컨텐츠 알림", "XXX 연예인의 새로운 컨텐츠가 업로드되었습니다.");
    }
}