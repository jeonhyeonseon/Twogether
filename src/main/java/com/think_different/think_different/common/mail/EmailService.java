package com.think_different.think_different.common.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("[TwoGether] 이메일 인증코드");
        message.setText("인증코드: " + code);
        javaMailSender.send(message);
    }

    // 아이디/비밀번호 찾기 인증코드
    public void sendAccountFindCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("[TwoGether] 아이디/비밀번호 찾기 인증코드");
        message.setText("인증코드: " + code + "\n\n본인이 요청하지 않았다면 이 메일을 무시해주세요.");
        javaMailSender.send(message);
    }
}
