package com.huashi.common.notice.util;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.huashi.common.notice.exception.SendException;

@Component
public class EmailSendUtil {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private TaskExecutor   threadPoolTaskExecutor;

    @Value("${notice.email.username}")
    private String         sendEmailAddress;

    /**
     * 邮件昵称，即显示名称（不设置将显示邮箱地址）
     */
    @Value("${notice.email.knick}")
    private String         sendEmailKnick;

    private final Logger   logger = LoggerFactory.getLogger(getClass());

    /**
     * TODO 发送邮件
     * 
     * @param to 收件人
     * @param cc 抄送人
     * @param subject 邮件主题
     * @param nickName 昵称
     * @param content 邮件内容
     * @throws MessagingException
     */
    protected void send(String to, String cc, String subject, String nickName, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            // 可选的，可以用来修改显示给接收者的名字
            // helper.setFrom(sendEmailAddress);
            String nick = "";
            try {
                nick = javax.mail.internet.MimeUtility.encodeText(nickName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            helper.setFrom(new InternetAddress(nick + " <" + sendEmailAddress + ">"));
            helper.setTo(to.split(";"));
            if (StringUtils.isNotEmpty(cc)) {
                {
                    helper.setCc(cc.split(";"));
                }
            }
            helper.setSubject(subject);
            helper.setText(content, true);// true为支持html类型
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO 发送邮件
     * 
     * @param to 收件人
     * @param subject 主题
     * @param content 邮件内容
     * @throws MessagingException
     */
    protected void send(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            // 可选的，可以用来修改显示给接收者的名字
            // helper.setFrom(sendEmailAddress);
            String nick = "";
            try {
                nick = javax.mail.internet.MimeUtility.encodeText(sendEmailKnick);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            helper.setFrom(new InternetAddress(nick + " <" + sendEmailAddress + ">"));
            helper.setTo(to.split(";"));
            helper.setSubject(subject);
            helper.setText(content, true);// true为支持html类型
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO 发送短信
     * 
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @throws SenderException
     */
    public void sendEmail(String to, String subject, String content) throws SendException {
        if (StringUtils.isEmpty(to)) {
            logger.info("收件人邮箱为空");
            throw new SendException("收件人邮箱为空");
        }

        if (StringUtils.isEmpty(subject)) {
            logger.info("邮件主题主题为空");
        }

        if (StringUtils.isEmpty(content)) {
            logger.info("邮件内容为空");
        }

        threadPoolTaskExecutor.execute(new SendMailThread(to, subject, content));
    }

    public void sendEmail(String to, String subject, String content, String attachment) throws SendException {
        if (StringUtils.isEmpty(to)) {
            logger.info("收件人邮箱为空");
            throw new SendException("收件人邮箱为空");
        }

        if (StringUtils.isEmpty(subject)) {
            logger.info("邮件主题主题为空");
        }

        if (StringUtils.isEmpty(content)) {
            logger.info("邮件内容为空");
        }

        threadPoolTaskExecutor.execute(new SendMailThread(to, subject, content));
    }

    /**
     * TODO 内部线程类，利用线程池异步发邮件
     *
     * @author zhengying
     * @version V1.0.0
     * @date 2016年1月10日 下午5:36:19
     */
    private class SendMailThread implements Runnable {

        private String to;
        private String subject;
        private String content;

        private SendMailThread(String to, String subject, String content) {
            super();
            this.to = to;
            this.subject = subject;
            this.content = content;
        }

        @Override
        public void run() {
            send(to, subject, content);
        }
    }
}
