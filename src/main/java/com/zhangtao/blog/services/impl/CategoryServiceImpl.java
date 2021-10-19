package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.dao.CategoryDao;
import com.zhangtao.blog.pojo.Category;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICategoryService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends BaseService implements ICategoryService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private IUserService userService;

    @Override
    public ResponseResult addCategory(Category category) {
        //先检查数据
        //分类名称、拼音、顺序、描述
        if(TextUtils.isEmpty(category.getName())){
            return ResponseResult.FAILED("分类名称不可以为空");
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类拼音不可以为空");
        }
        if(TextUtils.isEmpty(category.getDescription())){
            return ResponseResult.FAILED("描述不可以为空");
        }
        //补全数据
        category.setId(idWorker.nextId() + "");
        category.setStatus("1");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        //保存数据
        categoryDao.save(category);
        //返回结果
        return ResponseResult.SUCCESS("添加分类成功！");
    }

    @Override
    public ResponseResult getCategory(String categoryId) {
        Category categoryFormDb = categoryDao.findOneById(categoryId);
        if(categoryFormDb == null){
            return ResponseResult.FAILED("分类不存在");
        }
        return ResponseResult.SUCCESS("获取分类成功。").setData(categoryFormDb);
    }

    @Override
    public ResponseResult listCategories() {

        //创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime", "order");
        //判断用户省份
        SobUser sobUser = userService.checkSobUser();
        //查询
        //普通用户只能获取没有删除的分类，及state=1
        List<Category> categories;
        if(sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())){
            categories = categoryDao.findAllByState("1");
        }else{
            categories = categoryDao.findAll(sort);
        }
        //返回结果
        return ResponseResult.SUCCESS("获取分类列表成功").setData(categories);
    }

    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        //查询原有的分类数据
        Category categoryFromDb = categoryDao.findOneById(categoryId);
        if (categoryFromDb == null) {
            return ResponseResult.FAILED("修改分类不存在");
        }
        //检查数据的合法性,并修改数据
        if(!TextUtils.isEmpty(category.getName())){
            categoryFromDb.setName(category.getName());
        }
        if (!TextUtils.isEmpty(category.getPinyin())) {
            categoryFromDb.setPinyin(category.getPinyin());
        }
        if(!TextUtils.isEmpty(category.getDescription())){
            categoryFromDb.setDescription(category.getDescription());
        }
        categoryFromDb.setOrder(category.getOrder());
        //保存数据
        categoryDao.save(categoryFromDb);
        //返回结果
        return ResponseResult.SUCCESS("分类更新成功!");
    }

    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryDao.deleteCategoryByUpdateState(categoryId);
        return getDeleteResult(result, "分类");
    }
}
