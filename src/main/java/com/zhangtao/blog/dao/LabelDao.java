package com.zhangtao.blog.dao;

import com.zhangtao.blog.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LabelDao extends JpaRepository<Label, String>, JpaSpecificationExecutor<Label> {

    @Modifying
    int deleteOneById(String id);

    @Modifying
    @Query(value = "delete from `tb_labels` where id = ?;", nativeQuery = true)
    int customDeleteLabelById(String id);

    Label findOneById(String id);
}
