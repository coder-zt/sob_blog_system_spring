package com.zhangtao.blog.services.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zhangtao.blog.dao.ArticleNoContentDao;
import com.zhangtao.blog.dao.CommentDao;
import com.zhangtao.blog.pojo.ArticleNoContent;
import com.zhangtao.blog.pojo.Comment;
import com.zhangtao.blog.pojo.PageList;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ICommentService;
import com.zhangtao.blog.services.ISolrService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class CommentServiceImpl extends BaseService implements ICommentService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private Gson gson;

    @Override
    public ResponseResult postComment(Comment comment) {
        SobUser sobUser = userService.checkSobUser();
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //????????????
        String articleId = comment.getArticleId();
        if(TextUtils.isEmpty(articleId)){
            return ResponseResult.FAILED("??????id????????????.");
        }
        ArticleNoContent articleNoContentFromDb = articleNoContentDao.findOneById(articleId);
        if(articleNoContentFromDb == null){
            return ResponseResult.FAILED("?????????????????????.");
        }
        String content = comment.getContent();
        if(TextUtils.isEmpty(content)){
            return ResponseResult.FAILED("????????????????????????.");
        }
        //????????????
        comment.setId(idWorker.nextId() + "");
        comment.setState(Constants.Comment.STATE_PUBLISH);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setUserAvatar(sobUser.getAvatar());
        comment.setUserName(sobUser.getUserName());
        comment.setUserId(sobUser.getId());
        //????????????
        commentDao.save(comment);
        redisUtils.del(Constants.Comment.REDIS_COMMENT_FIRST_PAGE_CACHE + articleId);
        return ResponseResult.SUCCESS("????????????.");
    }

    @Override
    public ResponseResult listArticleComments(String articleId, int page, int size) {
        //????????????
        page = checkPage(page);
        size = checkSize(size);
        //?????????????????????
        if(page == 1){
            String commentJson = (String) redisUtils.get(Constants.Comment.REDIS_COMMENT_FIRST_PAGE_CACHE + articleId);
            if (!TextUtils.isEmpty(commentJson)) {
                PageList<Comment> commentList = gson.fromJson(commentJson,new TypeToken<PageList<Comment>>() {}.getType());
                return ResponseResult.SUCCESS("????????????????????????").setData(commentList);
            }
        }
        //??????
        Sort sort = new Sort(Sort.Direction.DESC, "state","createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<Comment> all = commentDao.findAll(new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return cb.equal(root.get("state").as(String.class), "1");
            }
        }, pageable);
        PageList<Comment> result = new PageList<>();
        result.parsePage(all);
        if(page == 1){
            redisUtils.set(Constants.Comment.REDIS_COMMENT_FIRST_PAGE_CACHE + articleId, gson.toJson(result), Constants.TimeValueInSecond.MINUTE_5);
        }
        return ResponseResult.SUCCESS("????????????????????????").setData(result);
    }

    @Override
    public ResponseResult deleteCommentById(String commentId) {
        //????????????
        SobUser sobUser = userService.checkSobUser();
        if(sobUser == null){
            return  ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        //??????????????????????????????
        Comment commentFromDb = commentDao.findOneById(commentId);
        if (commentFromDb == null) {
            return ResponseResult.FAILED("???????????????.");
        }
        if(sobUser.getId().equals(commentFromDb.getUserId()) ||
                Constants.User.ROLE_ADMIN.equals(sobUser.getRoles())){
            commentDao.deleteByUpdateState(commentId);
            return ResponseResult.SUCCESS("??????????????????.");
        }
        return ResponseResult.PERMISSION_FORBID();
    }


    @Override
    public ResponseResult listComments(int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<Comment> all = commentDao.findAll(pageable);
        return ResponseResult.SUCCESS("????????????????????????").setData(all);
    }

    @Override
    public ResponseResult topComment(String commentId) {
        Comment commentFromDb = commentDao.findOneById(commentId);
        if(commentFromDb == null){
           return ResponseResult.FAILED("???????????????.");
        }
        if(commentFromDb.getState().equals(Constants.Comment.STATE_TOP)){
            commentFromDb.setState(Constants.Comment.STATE_PUBLISH);
            return ResponseResult.SUCCESS("??????????????????.");
        }else if(commentFromDb.getState().equals(Constants.Comment.STATE_PUBLISH)){
            commentFromDb.setState(Constants.Comment.STATE_TOP);
            return ResponseResult.SUCCESS("??????????????????.");
        }else{
            return ResponseResult.FAILED("??????????????????.");
        }
    }

    @Override
    public ResponseResult getCommentCount() {
        long count = commentDao.count();
        return ResponseResult.SUCCESS("????????????????????????.").setData(count);
    }
}
