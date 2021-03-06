package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.entity.UserFriend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    int insert(User record);

    int insertSelective(User record);

    User searchReferPeople(Map map);

    User isPhoneRegisterOem(Map map);

    int updateByBindCard (Map map);

    User comparePassword (Map map);

    int updatePassword (Map map);

    UserFriend searchFriendList (Map map);

    User searchUserById(Map map);

    int udpOpautopay (Map map);


    /**
     * 查询普通用户下的二级id
     * @param map
     * @return
     */
    List<Long> searchTwoId(Map map);

    /**
     * 查询代理商用户下的二级三级id
     * @param map
     * @return
     */
    List<Long> searchTwoThreeId(Map map);

    /**
     * 查询代理商用户下的三级id
     * @param map
     * @return
     */
    List<Long> searchThreeId(Map map);

    /**
     * 查询上级id
     * @param map
     * @return
     */
    Long searchParent(Map map);

    /**
     * 查询等级id
     * @param map
     * @return
     */
    Long searchClassId(Map map);

    /**
     * 根据用户id查询所有上级用户
     * @param map
     * @return
     */
    List<String> findSuperior(Map map);

    /**
     * 找回密码
     * @param map
     * @return
     */
    int retrievePassword(Map map);

    /**
     * 根据用户id查询直推的实名认证的用户
     * @param userId
     * @return
     */
    int bindNumByUserId(Long userId);

    /**
     * 用户升级
     * @param map
     * @return
     */
    int updateUserClass(Map map);
}