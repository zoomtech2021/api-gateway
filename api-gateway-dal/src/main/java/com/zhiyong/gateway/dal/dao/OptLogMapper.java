package com.zhiyong.gateway.dal.dao;

import com.zhiyong.gateway.dal.domain.OptLog;
import com.zhiyong.gateway.dal.domain.OptLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OptLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int countByExample(OptLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int insert(OptLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int insertSelective(OptLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    List<OptLog> selectByExample(OptLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    OptLog selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") OptLog record, @Param("example") OptLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") OptLog record, @Param("example") OptLogExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(OptLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table opt_log
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OptLog record);
}