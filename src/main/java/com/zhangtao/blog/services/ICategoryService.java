package com.zhangtao.blog.services;

import com.zhangtao.blog.pojo.Category;
import com.zhangtao.blog.responese.ResponseResult;

public interface ICategoryService {

    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult listCategories(int page, int size);

    ResponseResult updateCategory(String categoryId, Category category);

    ResponseResult deleteCategory(String categoryId);
}
