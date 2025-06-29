package cn.com.traninfo.fastlcdp.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SearchService<T> {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private BaseMapper<T> baseMapper;

    /**
     * 查询所有记录
     * @return 实体对象列表
     */
    public List<T> list(Class<T> entityClass) {
        try {
            return baseMapper.selectList(null);
        } catch (Exception e) {
            logger.error("查询所有实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询所有实体失败", e);
        }
    }

    /**
     * 根据条件查询记录列表
     * @param queryWrapper 查询条件
     * @return 实体对象列表
     */
    public List<T> list(Wrapper<T> queryWrapper) {
        try {
            return baseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            logger.error("根据条件查询实体列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据条件查询实体列表失败", e);
        }
    }

    /**
     * 根据ID集合查询记录列表
     * @param idList 主键ID集合
     * @return 实体对象列表
     */
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return List.of();
        }
        try {
            return baseMapper.selectBatchIds(idList);
        } catch (Exception e) {
            logger.error("根据ID集合查询实体列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据ID集合查询实体列表失败", e);
        }
    }

    /**
     * 根据Map条件查询记录列表
     * @param columnMap 条件Map
     * @return 实体对象列表
     */
    public List<T> listByMap(Map<String, Object> columnMap) {
        try {
            return baseMapper.selectByMap(columnMap);
        } catch (Exception e) {
            logger.error("根据Map条件查询实体列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据Map条件查询实体列表失败", e);
        }
    }

    /**
     * 分页查询记录
     * @param page 分页对象
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
        try {
            return baseMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            logger.error("分页查询实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("分页查询实体失败", e);
        }
    }

    /**
     * 分页查询记录（简化版）
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    public IPage<T> page(long current, long size) {
        try {
            Page<T> page = new Page<>(current, size);
            return baseMapper.selectPage(page, null);
        } catch (Exception e) {
            logger.error("分页查询实体失败: {}", e.getMessage(), e);
            throw new RuntimeException("分页查询实体失败", e);
        }
    }

    /**
     * 查询记录总数
     * @return 总数
     */
    public long count() {
        try {
            return baseMapper.selectCount(null);
        } catch (Exception e) {
            logger.error("查询实体总数失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询实体总数失败", e);
        }
    }

    /**
     * 根据条件查询记录总数
     * @param queryWrapper 查询条件
     * @return 总数
     */
    public long count(Wrapper<T> queryWrapper) {
        try {
            return baseMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            logger.error("根据条件查询实体总数失败: {}", e.getMessage(), e);
            throw new RuntimeException("根据条件查询实体总数失败", e);
        }
    }

    /**
     * 判断记录是否存在
     * @param queryWrapper 查询条件
     * @return 是否存在
     */
    public boolean exists(Wrapper<T> queryWrapper) {
        return count(queryWrapper) > 0;
    }

    /**
     * 创建查询条件构造器
     * @return QueryWrapper
     */
    public QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

}
