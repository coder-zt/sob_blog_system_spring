package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Category;
import com.zhangtao.blog.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {

    /**
     * 根据分类的id查找分类的信息
     *
     * @param categoryId
     */
    Category findOneById(String categoryId);

    /**
     * 根据分类的id删除分类
     *
     * @param categoryId
     * @return
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_categories` set `status` = 0 where `id` = ?;")
    int deleteCategoryByUpdateState(String categoryId);

    @Query(nativeQuery = true, value = "select * from `tb_categories` where `status` = ?;")
    List<Category> findAllByState(String s);
}
