package cn.com.traninfo.fastlcdp.erdesigner.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * EntityService单元测试类
 */
@ExtendWith(MockitoExtension.class)
class EntityServiceTest {

    @Mock
    private BaseMapper<TestEntity> baseMapper;

    @InjectMocks
    private EntityService<TestEntity> entityService;

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

    // ==================== 保存方法测试 ====================

    @Test
    void testSave_Success() {
        // Given
        when(baseMapper.insertOrUpdate(testEntity)).thenReturn(true);

        // When
        boolean result = entityService.save(testEntity);

        // Then
        assertTrue(result);
        verify(baseMapper, times(1)).insertOrUpdate(testEntity);
    }

    @Test
    void testSave_Exception() {
        // Given
        when(baseMapper.insertOrUpdate(testEntity)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityService.save(testEntity);
        });
        assertEquals("保存或更新实体失败", exception.getMessage());
        verify(baseMapper, times(1)).insertOrUpdate(testEntity);
    }

    @Test
    void testSaveBatch_Success() {
        // Given
        when(baseMapper.insertOrUpdate((Collection<TestEntity>) testEntityList)).thenReturn(Collections.emptyList());

        // When
        boolean result = entityService.saveBatch(testEntityList);

        // Then
        assertTrue(result);
        verify(baseMapper, times(1)).insertOrUpdate((Collection<TestEntity>) testEntityList);
    }

    @Test
    void testSaveBatch_EmptyList() {
        // When
        boolean result = entityService.saveBatch(Collections.emptyList());

        // Then
        assertFalse(result);
        verify(baseMapper, never()).insertOrUpdate(any(TestEntity.class));
    }

    @Test
    void testSaveBatch_NullList() {
        // When
        boolean result = entityService.saveBatch(null);

        // Then
        assertFalse(result);
        verify(baseMapper, never()).insertOrUpdate(any(Collection.class));
    }

    @Test
    void testSaveBatch_Exception() {
        // Given
        when(baseMapper.insertOrUpdate((Collection<TestEntity>) testEntityList)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityService.saveBatch(testEntityList);
        });
        assertEquals("批量保存实体失败", exception.getMessage());
        verify(baseMapper, times(1)).insertOrUpdate((Collection<TestEntity>) testEntityList);
    }

    // ==================== 更新方法测试 ====================

    @Test
    void testUpdate_WithWrapper_Success() {
        // Given
        UpdateWrapper<TestEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", 1L);
        
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.update(wrapper)).thenReturn(1);
            sqlHelperMock.when(() -> SqlHelper.retBool(1)).thenReturn(true);

            // When
            boolean result = entityService.update(wrapper);

            // Then
            assertTrue(result);
            verify(baseMapper, times(1)).update(wrapper);
        }
    }

    @Test
    void testUpdate_WithWrapper_NotFound() {
        // Given
        UpdateWrapper<TestEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", 999L);
        
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.update(wrapper)).thenReturn(0);
            sqlHelperMock.when(() -> SqlHelper.retBool(0)).thenReturn(false);

            // When
            boolean result = entityService.update(wrapper);

            // Then
            assertFalse(result);
            verify(baseMapper, times(1)).update(wrapper);
        }
    }

    @Test
    void testUpdate_WithWrapper_Exception() {
        // Given
        UpdateWrapper<TestEntity> wrapper = new UpdateWrapper<>();
        when(baseMapper.update(wrapper)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityService.update(wrapper);
        });
        assertEquals("根据条件更新实体失败", exception.getMessage());
        verify(baseMapper, times(1)).update(wrapper);
    }

    // ==================== 删除方法测试 ====================

    @Test
    void testDelete_ByEntity_Success() {
        // Given
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.deleteById(testEntity)).thenReturn(1);
            sqlHelperMock.when(() -> SqlHelper.retBool(1)).thenReturn(true);

            // When
            boolean result = entityService.delete(testEntity);

            // Then
            assertTrue(result);
            verify(baseMapper, times(1)).deleteById(testEntity);
        }
    }

    @Test
    void testDelete_ByEntity_NotFound() {
        // Given
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.deleteById(testEntity)).thenReturn(0);
            sqlHelperMock.when(() -> SqlHelper.retBool(0)).thenReturn(false);

            // When
            boolean result = entityService.delete(testEntity);

            // Then
            assertFalse(result);
            verify(baseMapper, times(1)).deleteById(testEntity);
        }
    }

    @Test
    void testDelete_ByWrapper_Success() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Test Entity");
        
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.delete(wrapper)).thenReturn(1);
            sqlHelperMock.when(() -> SqlHelper.retBool(1)).thenReturn(true);

            // When
            boolean result = entityService.delete(wrapper);

            // Then
            assertTrue(result);
            verify(baseMapper, times(1)).delete(wrapper);
        }
    }

    @Test
    void testDelete_Collection_Success() {
        // Given
        try (MockedStatic<SqlHelper> sqlHelperMock = mockStatic(SqlHelper.class)) {
            when(baseMapper.deleteByIds(testEntityList)).thenReturn(3);
            sqlHelperMock.when(() -> SqlHelper.retBool(3)).thenReturn(true);

            // When
            boolean result = entityService.delete(testEntityList);

            // Then
            assertTrue(result);
            verify(baseMapper, times(1)).deleteByIds(testEntityList);
        }
    }

    @Test
    void testDelete_Collection_EmptyList() {
        // When
        boolean result = entityService.delete(Collections.emptyList());

        // Then
        assertFalse(result);
        verify(baseMapper, never()).deleteByIds(any());
    }

    @Test
    void testDelete_Collection_Exception() {
        // Given
        when(baseMapper.deleteByIds(testEntityList)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entityService.delete(testEntityList);
        });
        assertEquals("批量删除实体失败", exception.getMessage());
        verify(baseMapper, times(1)).deleteByIds(testEntityList);
    }

    // ==================== 查询方法测试 ====================

    @Test
    void testGetById_Found() {
        // Given
        when(baseMapper.selectById(1L)).thenReturn(testEntity);

        // When
        TestEntity result = entityService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testEntity.getId(), result.getId());
        assertEquals(testEntity.getName(), result.getName());
        verify(baseMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        when(baseMapper.selectById(999L)).thenReturn(null);

        // When
        TestEntity result = entityService.getById(999L);

        // Then
        assertNull(result);
        verify(baseMapper, times(1)).selectById(999L);
    }

    @Test
    void testGetOne_WithWrapper() {
        // Given
        QueryWrapper<TestEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Test Entity");
        when(baseMapper.selectOne(wrapper)).thenReturn(testEntity);

        // When
        TestEntity result = entityService.getOne(wrapper);

        // Then
        assertNotNull(result);
        assertEquals(testEntity.getName(), result.getName());
        verify(baseMapper, times(1)).selectOne(wrapper);
    }

    @Test
    void testExistsById_True() {
        // Given
        when(baseMapper.selectById(1L)).thenReturn(testEntity);

        // When
        boolean result = entityService.existsById(1L);

        // Then
        assertTrue(result);
        verify(baseMapper, times(1)).selectById(1L);
    }

    @Test
    void testExistsById_False() {
        // Given
        when(baseMapper.selectById(999L)).thenReturn(null);

        // When
        boolean result = entityService.existsById(999L);

        // Then
        assertFalse(result);
        verify(baseMapper, times(1)).selectById(999L);
    }

    // ==================== 便捷方法测试 ====================

    @Test
    void testUpdate_CreateWrapper() {
        // When
        UpdateWrapper<TestEntity> result = entityService.update();

        // Then
        assertNotNull(result);
        assertTrue(result instanceof UpdateWrapper);
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