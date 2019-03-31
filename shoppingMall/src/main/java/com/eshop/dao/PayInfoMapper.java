package com.eshop.dao;

import com.eshop.pojo.PayInfo;

/**
 * 
 * @author Paula Lin
 *
 */
public interface PayInfoMapper {
    /**
     * @param id
     * @return
     */
    int deletePayInfoByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int insertPayInfo(PayInfo record);

    /**
     * @param record
     * @return
     */
    int insertPayInfoSelective(PayInfo record);

    /**
     * @param id
     * @return
     */
    PayInfo selectPayInfoByPrimaryKey(Integer id);

    /**
     * @param record
     * @return
     */
    int updatePayInfoByPrimaryKeySelective(PayInfo record);

    /**
     * @param record
     * @return
     */
    int updatePayInfoByPrimaryKey(PayInfo record);
}