package cn.com.traninfo.fastlcdp.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * SearchService单元测试类
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private BaseMapper<TestEntity> baseMapper;

    @InjectMocks
    private SearchService<TestEntity> searchService;

    private TestEntity testEntity;
    private List<TestEntity> testEntityList;

    @BeforeEach
    void setUp() {
        testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("Test Entity");
        testEntity.setEmail("test@example.com");

        TestEntity entity2 = new TestEntity();
        entity2.setId(2L);
        entity2.setName("Test Entity 2");
        entity2.setEmail("test2@example.com");

        TestEntity entity3 = new TestEntity();
        entity3.setId(3L);
        entity3.setName("Test Entity 3");
        entity3.setEmail("test3@example.com");

        testEntityList = Arrays.asList(testEntity, entity2, entity3);
    }

    // ==================== 列表查询方法测试 ====================

    @Test
    void testList_All() {
        // Given
        when(baseMapper.selectList(null)).thenReturn(testEntityList);

        // When
        List<TestEntity> result = searchService.list(TestEntity.class);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(baseMapper, times(1)).selectList(null);
    }

    @Test
    void testList_All_Exception() {
        // Given
        when(baseMapper.selectList(null)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.list(TestEntity.class);
        });
        assertEquals("查询所有实体失败", exception.getMessage());
        verify(baseMapper, times(1)).selectList(null);
    }

    @Test
    void testList_WithWrapper() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Entity");
        when(baseMapper.selectList(any(QueryWrapper.class))).thenReturn(testEntityList);

        // When
        List<TestEntity> result = searchService.list(wrapper);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(baseMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testList_WithWrapper_Exception() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        when(baseMapper.selectList(wrapper)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.list(wrapper);
        });
        assertEquals("根据条件查询实体列表失败", exception.getMessage());
        verify(baseMapper, times(1)).selectList(wrapper);
    }

    @Test
    void testListByIds_Success() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(baseMapper.selectBatchIds(ids)).thenReturn(testEntityList);

        // When
        List<TestEntity> result = searchService.listByIds(ids);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(baseMapper, times(1)).selectBatchIds(ids);
    }

    @Test
    void testListByIds_EmptyList() {
        // When
        List<TestEntity> result = searchService.listByIds(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(baseMapper, never()).selectBatchIds(any());
    }

    @Test
    void testListByIds_Exception() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(baseMapper.selectBatchIds(ids)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.listByIds(ids);
        });
        assertEquals("根据ID集合查询实体列表失败", exception.getMessage());
        verify(baseMapper, times(1)).selectBatchIds(ids);
    }

    @Test
    void testListByMap_Success() {
        // Given
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", "Test Entity");
        when(baseMapper.selectByMap(columnMap)).thenReturn(testEntityList);

        // When
        List<TestEntity> result = searchService.listByMap(columnMap);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(baseMapper, times(1)).selectByMap(columnMap);
    }

    @Test
    void testListByMap_Exception() {
        // Given
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", "Test Entity");
        when(baseMapper.selectByMap(columnMap)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.listByMap(columnMap);
        });
        assertEquals("根据Map条件查询实体列表失败", exception.getMessage());
        verify(baseMapper, times(1)).selectByMap(columnMap);
    }

    // ==================== 分页查询方法测试 ====================

    @Test
    void testPage_WithWrapper() {
        // Given
        Page<TestEntity> page = new Page<>(1, 10);
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Entity");
        
        Page<TestEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(testEntityList);
        resultPage.setTotal(3);
        
        when(baseMapper.selectPage(any(IPage.class), any(QueryWrapper.class))).thenReturn(resultPage);

        // When
        IPage<TestEntity> result = searchService.page(page, wrapper);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getRecords().size());
        assertEquals(3, result.getTotal());
        verify(baseMapper, times(1)).selectPage(any(IPage.class), any(QueryWrapper.class));
    }

    @Test
    void testPage_Simple() {
        // Given
        Page<TestEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(testEntityList);
        resultPage.setTotal(3);
        
        when(baseMapper.selectPage(any(IPage.class), isNull())).thenReturn(resultPage);

        // When
        IPage<TestEntity> result = searchService.page(1, 10);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getRecords().size());
        assertEquals(3, result.getTotal());
        verify(baseMapper, times(1)).selectPage(any(IPage.class), isNull());
    }

    @Test
    void testPage_WithWrapper_Exception() {
        // Given
        Page<TestEntity> page = new Page<>(1, 10);
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        when(baseMapper.selectPage(page, wrapper)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.page(page, wrapper);
        });
        assertEquals("分页查询实体失败", exception.getMessage());
        verify(baseMapper, times(1)).selectPage(page, wrapper);
    }

    @Test
    void testPage_Simple_Exception() {
        // Given
        when(baseMapper.selectPage(any(IPage.class), isNull())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.page(1, 10);
        });
        assertEquals("分页查询实体失败", exception.getMessage());
        verify(baseMapper, times(1)).selectPage(any(IPage.class), isNull());
    }

    // ==================== 计数方法测试 ====================

    @Test
    void testCount_All() {
        // Given
        when(baseMapper.selectCount(null)).thenReturn(10L);

        // When
        long result = searchService.count();

        // Then
        assertEquals(10L, result);
        verify(baseMapper, times(1)).selectCount(null);
    }

    @Test
    void testCount_All_Exception() {
        // Given
        when(baseMapper.selectCount(null)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.count();
        });
        assertEquals("查询实体总数失败", exception.getMessage());
        verify(baseMapper, times(1)).selectCount(null);
    }

    @Test
    void testCount_WithWrapper() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Entity");
        when(baseMapper.selectCount(wrapper)).thenReturn(5L);

        // When
        long result = searchService.count(wrapper);

        // Then
        assertEquals(5L, result);
        verify(baseMapper, times(1)).selectCount(wrapper);
    }

    @Test
    void testCount_WithWrapper_Exception() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        when(baseMapper.selectCount(wrapper)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            searchService.count(wrapper);
        });
        assertEquals("根据条件查询实体总数失败", exception.getMessage());
        verify(baseMapper, times(1)).selectCount(wrapper);
    }

    // ==================== 存在性检查方法测试 ====================

    @Test
    void testExists_True() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Test Entity");
        when(baseMapper.selectCount(wrapper)).thenReturn(1L);

        // When
        boolean result = searchService.exists(wrapper);

        // Then
        assertTrue(result);
        verify(baseMapper, times(1)).selectCount(wrapper);
    }

    @Test
    void testExists_False() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Non-existent Entity");
        when(baseMapper.selectCount(wrapper)).thenReturn(0L);

        // When
        boolean result = searchService.exists(wrapper);

        // Then
        assertFalse(result);
        verify(baseMapper, times(1)).selectCount(wrapper);
    }

    // ==================== 便捷方法测试 ====================

    @Test
    void testQuery_CreateWrapper() {
        // When
        QueryWrapper<TestEntity> result = searchService.query();

        // Then
        assertNotNull(result);
        assertTrue(result instanceof QueryWrapper);
    }

    // ==================== 测试实体类 ====================

    static class TestEntity {
        private Long id;
        private String name;
        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}