package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.utils.Constants;

public class BaseService {

    int checkPage(int page){
        if(page < Constants.Page.DEFAULT_PAGE){
            page = Constants.Page.DEFAULT_PAGE;
        }
        return page;
    }

    int checkSize(int size){
        if(size < Constants.Page.MINI_PAGE_SIZE){
            size = Constants.Page.MINI_PAGE_SIZE;
        }
        return size;
    }

    ResponseResult getDeleteResult(int result, String tableName){
        return result > 0?ResponseResult.SUCCESS(tableName + "删除成功！"):ResponseResult.SUCCESS(tableName + "删除失败！");
    }
}
