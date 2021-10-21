package com.zhangtao.blog.controller.admin;

import com.zhangtao.blog.interceptor.CheckTooFrequentCommit;
import com.zhangtao.blog.pojo.Category;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 *管理中心分类的api
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {

    @Autowired
    private ICategoryService categoryService;
    /**
     * 添加分类
     * 需要管理员权限
     * @param category
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.adminPermission()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    /**
     * 删除分类
     *
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 修改分类
     *
     * @param categoryId
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category){
        return categoryService.updateCategory(categoryId, category);
    }

    /**
     * 获取分类
     *
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取分类列表
     *
     * 管理员权限
     *
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list")
    public ResponseResult listCategories(){
        return categoryService.listCategories();
    }
}
