package com.zhiyong.gateway.facade.tools;

import com.zhiyong.gateway.facade.model.Question;
import java.util.List;
import org.junit.Test;

/**
 * @ClassName PojoTypeParserTest
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/8 下午4:56
 **/
public class PojoTypeParserTest {

    @Test
    public void test() {
        String typeName = "questions";
        String typeJson = new PojoTypeParser<List<List<String>>>() {
        }.run(typeName).toJson();

        System.out.println(typeJson);
    }
}
