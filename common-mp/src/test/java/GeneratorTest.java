import com.github.fanzezhen.common.mp.generator.GeneratorBean;
import com.github.fanzezhen.common.mp.generator.GeneratorTool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
@Ignore
public class GeneratorTest {

    @Test
    @Ignore("测试生成器")
    public void testGenerator() {
//        GeneratorTool.generator(new GeneratorBean("jdbc:mysql://localhost:3306/dev?useSSL=false&useUnicode=true", "root", "root")
//                .setParentPackageName("com.github.fanzezhen.common.mp.generator.tmp"));
        Assert.assertEquals(2, 1 + 1);
    }
}
