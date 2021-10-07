import com.zhangtao.blog.utils.EmailSender;

import javax.mail.MessagingException;

public class TestEmailSender {
    public static void main(String[] args) throws MessagingException {
        EmailSender.subject("博客系统测试").from("博客系统")
                .text("验证码是：12llww")
                .to("691076379@qq.com")
                .send();
    }
}
