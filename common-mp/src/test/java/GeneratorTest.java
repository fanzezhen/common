import com.github.fanzezhen.common.mp.generator.GeneratorBean;
import com.github.fanzezhen.common.mp.generator.GeneratorTool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
public class GeneratorTest {

    @Test
    @Ignore
    public void testGenerator() {
        GeneratorTool.generator(new GeneratorBean("jdbc:mysql://localhost:3306/dev?useSSL=false&useUnicode=true", "root", "root")
                .setParentPackageName("com.github.fanzezhen.common.mp.generator.tmp"));
    }
}
