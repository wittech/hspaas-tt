/**
 * TODO
 * Author youngmeng
 * Created 2016-12-14 10:55
 */
public class Test {

    public static void main(String[] args) {
        String regex = "^【华时科技】您的短信验证码为[\\s\\S]*，请尽快完成后续操作。$";

        String content = "【华时科技】您的短信验证码为123456，请尽快完成后续操作。";

        if(content.matches(regex)){
            System.out.println("success");
        }else{
            System.out.println("fail");
        }
    }
}
