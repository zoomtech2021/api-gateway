package com.zhiyong.gateway.biz.facade;

import com.zhiyong.gateway.facade.api.GatewayTestFacade;
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
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName GatewayTestFacadeImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/28 下午1:18
 **/
@DubboService(version = "1.0.0")
public class GatewayTestFacadeImpl implements GatewayTestFacade {

    @Override
    public List<String> queryTeacher(List<String> teacherIds) {
        return teacherIds;
    }

    @Override
    public Teacher saveTeacher(String name, String sku, Integer age) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setSku(sku);
        teacher.setAge(age);
        return teacher;
    }

    @Override
    public Teacher[] saveTeacher(Teacher[] teachers) {
        return teachers;
    }

    @Override
    public Course saveCourse(Course courses) {
        return courses;
    }

    @Override
    public List<Course> saveCourse(List<Course> courses) {
        return courses;
    }

    @Override
    public Question saveQuestion(Question question) {
        return question;
    }

    @Override
    public List<Answer> updateAnswer(Long id, List<Answer> answers) {
        return answers;
    }

    @Override
    public Map<Long, List<Answer>> updateAnswer(Map<Long, List<Answer>> answers) {
        return answers;
    }

    @Override
    public int saveModule(String name, List<Course> courses, List<Live> lives) {
        return 0;
    }

    @Override
    public Set<Module> saveModule(Set<Module> modules) {
        return modules;
    }

    @Override
    public List<Live> saveLive(List<Live> lives) {
        return lives;
    }

    @Override
    public Date test(Date date) {
        return date;
    }

    @Override
    public List<List<String>> testList(List<List<String>> dataList) {
        return dataList;
    }
}
