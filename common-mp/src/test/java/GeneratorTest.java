import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
@Disabled
class GeneratorTest {

    @Test
    @Disabled("测试生成器")
    void testGenerator() {
//        GeneratorTool.generator(new GeneratorBean("jdbc:mysql://localhost:3306/dev?useSSL=false&useUnicode=true", "root", "root")
//                .setParentPackageName("com.github.fanzezhen.common.mp.generator.tmp"));
        Assertions.assertEquals(2, 1 + 1);
    }
}
