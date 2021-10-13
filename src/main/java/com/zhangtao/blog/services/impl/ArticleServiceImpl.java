package com.zhangtao.blog.services.impl;

import java.util.Date;

import javax.websocket.Decoder.Text;

import com.zhangtao.blog.dao.ArticleDao;
import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IArticleService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements IArticleService {

    @Autowired
    private IdWorker IdWorker;

    @Autowired
    private IUserService userService;

    @Autowired
    private ArticleDao articleDao;

    /**
     * 后期可以去做一些定时发布的功能 如果是多人博客系统，得考虑审核的问题--->成功,通知，审核不通过，也可通知
     * <p>
     * 保存成草稿 1、用户手动提交：会发生页面跳转-->提交完即可
     * 2、代码自动提交，每隔一段时间就会提交-->不会发生页面跳转-->多次提交-->如果没有唯一标识，会就重添加到数据库里
     * <p>
     * 不管是哪种草稿-->必须有标题
     * <p>
     * 方案一：每次用户发新文章之前-->先向后台请求一个唯一文章ID 如果是更新文件，则不需要请求这个唯一的ID
     * <p>
     * 方案二：可以直接提交，后台判断有没有ID,如果没有ID，就新创建，并且ID作为此次返回的结果 如果有ID，就修改已经存在的内容。
     * <p>
     * 推荐做法： 自动保存草稿，在前端本地完成，也就是保存在本地。 如果是用户手动提交的，就提交到后台
     *
     *
     * <p>
     * 防止重复提交（网络卡顿的时候，用户点了几次提交）： 可以通过ID的方式
     * 通过token_key的提交频率来计算，如果30秒之内有多次提交，只有最前的一次有效 其他的提交，直接return,提示用户不要太频繁操作.
     * <p>
     * 前端的处理：点击了提交以后，禁止按钮可以使用，等到有响应结果，再改变按钮的状态.
     */
    @Override
    public ResponseResult postArticle(Article article) {
        // 获取用户对象
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 检查数据
        // 文章标题、摘要、分类ID、标签、类型、内容
        String title = article.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("文章标题不可以为空.");
        }
        if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
            return ResponseResult.FAILED("文章标题不可以超过128个字符");
        }
        String summary = article.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            return ResponseResult.FAILED("摘要不可以为空!");
        }
        if (summary.length() > Constants.Article.SUMMARY_MAC_LENGTH) {
            return ResponseResult.FAILED("文章标题不可以超过256个字符");
        }
        String categoryID = article.getCategoryId();
        if (TextUtils.isEmpty(categoryID)) {
            return ResponseResult.FAILED("未指定文章类别");
        }
        String label = article.getLabels();
        if (TextUtils.isEmpty(label)) {
            return ResponseResult.FAILED("文章标签不可以为空!");
        }
        String state = article.getState();
        if (!Constants.Article.STATE_DRAFT.equals(state) && !Constants.Article.STATE_DRAFT.equals(state)) {
            return ResponseResult.FAILED("不支持此操作");
        }
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("类型不可以为空.");
        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("类型格式不对.");
        }
        // 补充数据 id、用户id、创建时间、更新时间
        article.setId(IdWorker.nextId() + "");
        article.setUserId(sobUser.getId());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        articleDao.save(article);
        return ResponseResult.SUCCESS("文章发表成功.");
    }

}