package com.github.fanzezhen.common.mp;

import com.github.fanzezhen.common.mp.generator.Generator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
@Disabled
class GeneratorTest {

    @Test
    @Disabled("测试生成器")
    void testGenerator() {
        Generator.fastAutoGenerator(Generator.Config.ofTable(
            "jdbc:mysql://localhost:3306/dev?useSSL=false&useUnicode=true",
            "root",
            "root",
            "sys_dict", "sys_dict_item"));
        Assertions.assertTrue(true);
    }

}
