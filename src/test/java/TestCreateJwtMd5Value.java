import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class TestCreateJwtMd5Value {
    public static void main(String[] args) {
        String jwtKeyMd5Str = DigestUtils.md5DigestAsHex("lllsxjj".getBytes());
        System.out.println(jwtKeyMd5Str);
    }
}
