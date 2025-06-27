package cn.com.traninfo.fastlcdp.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;

/**
 * 通用实体服务类，提供MyBatis Plus的增删改查功能
 * @param <T> 实体类型
 */
@Service
public class EntityService<T> {
    
    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private BaseMapper<T> baseMapper;

    /**
     * 保存或更新实体（根据主键判断）
     * @param entity 实体对象
     * @return 是否成功
     */
    @Transactional
    public boolean save(T entity) {
        try {
            // 这里简化处理，实际应该根据主键判断
            return baseMapper.insertOrUpdate(entity);
        } catch (Exception e) {
            logger.error("保存或更新实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存或更新实体失败", e);
        }
    }

    /**
     * 批量插入记录
     * @param entityList 实体对象集合
     * @return 是否成功
     */
    @Transactional
    public boolean saveBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        try {
            baseMapper.insertOrUpdate(entityList);
            return true;
        } catch (Exception e) {
            logger.error("批量保存实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量保存实体失败", e);
        }
    }

    /**
     * 根据条件更新记录
     * @param updateWrapper 更新条件
     * @return 是否成功
     */
    @Transactional
    public boolean update(Wrapper<T> updateWrapper) {
        try {
            return SqlHelper.retBool(baseMapper.update(updateWrapper));
        } catch (Exception e) {
            logger.error("根据条件更新实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据条件更新实体失败", e);
        }
    }

    /**
     * 根据实体ID删除记录
     * @param entity 实体ID
     * @return 是否成功
     */
    @Transactional
    public boolean delete(T entity) {
        try {
            return SqlHelper.retBool(baseMapper.deleteById(entity));
        } catch (Exception e) {
            logger.error("根据ID删除实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据ID删除实体失败", e);
        }
    }
    
    /**
     * 根据条件删除记录
     * @param queryWrapper 查询条件
     * @return 是否成功
     */
    @Transactional
    public boolean delete(Wrapper<T> queryWrapper) {
        try {
            return SqlHelper.retBool(baseMapper.delete(queryWrapper));
        } catch (Exception e) {
            logger.error("根据条件删除实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据条件删除实体失败", e);
        }
    }
    
    /**
     * 批量删除记录
     * @param entityList 主键ID集合
     * @return 是否成功
     */
    @Transactional
    public boolean delete(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        try {
            return SqlHelper.retBool(baseMapper.deleteByIds(entityList));
        } catch (Exception e) {
            logger.error("批量删除实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量删除实体失败", e);
        }
    }

    /**
     * 根据ID查询记录
     * @param id 主键ID
     * @return 实体对象
     */
    public T getById(Serializable id) {
        try {
            return baseMapper.selectById(id);
        } catch (Exception e) {
            logger.error("根据ID查询实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据ID查询实体失败", e);
        }
    }
    
    /**
     * 根据条件查询一条记录
     * @param queryWrapper 查询条件
     * @return 实体对象
     */
    public T getOne(Wrapper<T> queryWrapper) {
        try {
            return baseMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            logger.error("根据条件查询单个实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据条件查询单个实体失败", e);
        }
    }

    /**
     * 根据ID判断记录是否存在
     * @param id 主键ID
     * @return 是否存在
     */
    public boolean existsById(Serializable id) {
        return getById(id) != null;
    }

    /**
     * 创建更新条件构造器
     * @return UpdateWrapper
     */
    public UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

}
