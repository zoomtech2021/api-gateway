package com.zhiyong.gateway.facade.api;

import com.zhiyong.gateway.facade.model.Answer;
import com.zhiyong.gateway.facade.model.Course;
import com.zhiyong.gateway.facade.model.Live;
import com.zhiyong.gateway.facade.model.Module;
import com.zhiyong.gateway.facade.model.Question;
import com.zhiyong.gateway.facade.model.Teacher;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName GatewayTestFacade
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/28 下午1:06
 **/
public interface GatewayTestFacade {

    /**
     * 查询老师
     * @return
     */
    List<String> queryTeacher(List<String> teacherIds);

    /**
     * 创建老师
     * @param name
     * @param sku
     * @return
     */
    Teacher saveTeacher(String name, String sku, Integer age);

    /**
     * 创建老师
     * @return
     */
    Teacher[] saveTeacher(Teacher[] teachers);

    /**
     * 保存课程
     * @param courses
     * @return
     */
    Course saveCourse(Course courses);

    /**
     * 保存课程
     * @param courses
     * @return
     */
    List<Course> saveCourse(List<Course> courses);

    /**
     * 保存问答
     * @param question
     * @return
     */
    Question saveQuestion(Question question);

    /**
     * 更新问题答案
     * @param answers
     * @return
     */
    List<Answer> updateAnswer(Long id, List<Answer> answers);

    /**
     * 更新问题答案
     * @param answers
     * @return
     */
    Map<Long, List<Answer>> updateAnswer(Map<Long, List<Answer>> answers);

    /**
     * 保存模块
     * @param name
     * @param courses
     * @param lives
     * @return
     */
    int saveModule(String name, List<Course> courses, List<Live> lives);

    /**
     * 保存模块
     * @param modules
     * @return
     */
    Set<Module> saveModule(Set<Module> modules);

    /**
     * 保存直播
     * @param lives
     * @return
     */
    List<Live> saveLive(List<Live> lives);

    /**
     * 测试日期
     * @param date
     * @return
     */
    Date test(Date date);

    /**
     * 基本类型list嵌套
     * @param dataList
     * @return
     */
    List<List<String>> testList(List<List<String>> dataList);
}
