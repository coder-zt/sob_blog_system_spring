package com.zhangtao.blog.services.impl;

import java.util.*;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zhangtao.blog.dao.ArticleDao;
import com.zhangtao.blog.dao.ArticleNoContentDao;
import com.zhangtao.blog.dao.CommentDao;
import com.zhangtao.blog.dao.LabelDao;
import com.zhangtao.blog.pojo.*;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IArticleService;
import com.zhangtao.blog.services.ISolrService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements IArticleService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private Random random;

    @Autowired
    private IUserService userService;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private ISolrService solrService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private Gson gson;

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
        // 检查用户，获取到用户对象
        SobUser sobUser = userService.checkSobUser();
        // 未登录
        if (sobUser == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        // 检查数据
        // title、分类ID、内容、类型、摘要、标签
        String title = article.getTitle();
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED("标题不可以为空.");
        }

        // 2种，草稿和发布
        String state = article.getState();
        if (!Constants.Article.STATE_PUBLISH.equals(state) && !Constants.Article.STATE_DRAFT.equals(state)) {
            // 不支持此操作
            return ResponseResult.FAILED("不支持此操作");
        }
        // 0表示富文本，1表示markdown
        String type = article.getType();
        if (TextUtils.isEmpty(type)) {
            return ResponseResult.FAILED("类型不可以为空.");
        }
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("类型格式不对.");
        }

        // 以下检查是发布的检查，草稿不需要检查
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
                return ResponseResult.FAILED("文章标题不可以超过" + Constants.Article.TITLE_MAX_LENGTH + "个字符");
            }
            String content = article.getContent();
            if (TextUtils.isEmpty(content)) {
                return ResponseResult.FAILED("内容不可为空.");
            }

            String summary = article.getSummary();
            if (TextUtils.isEmpty(summary)) {
                return ResponseResult.FAILED("摘要不可以为空.");
            }
            if (summary.length() > Constants.Article.SUMMARY_MAX_LENGTH) {
                return ResponseResult.FAILED("摘要不可以超出" + Constants.Article.SUMMARY_MAX_LENGTH + "个字符.");
            }
            String labels = article.getLabels();
            // 标签-标签1-标签2
            if (TextUtils.isEmpty(labels)) {
                return ResponseResult.FAILED("标签不可以为空.");
            }
        }

        String articleId = article.getId();
        if (TextUtils.isEmpty(articleId)) {
            // 新内容,数据里没有的
            // 补充数据：ID、创建时间、用户ID、更新时间
            article.setId(idWorker.nextId() + "");
            article.setCreateTime(new Date());
        } else {
            // 更新内容，对状态进行处理，如果已经是发布的，则不能再保存为草稿
            Article articleFromDb = articleDao.findOneById(articleId);
            if (Constants.Article.STATE_PUBLISH.equals(articleFromDb.getState())
                    && Constants.Article.STATE_DRAFT.equals(state)) {
                // 已经发布了，只能更新，不能保存草稿
                return ResponseResult.FAILED("已发布文章不支持成为草稿.");
            }
        }
        article.setUserId(sobUser.getId());
        article.setUpdateTime(new Date());
        // 保存到数据库里
        articleDao.save(article);
        redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE + article.getCategoryId());
        redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE);
        if ((Constants.Article.STATE_PUBLISH.equals(state))) {
            solrService.addArticle(article);
        }
        // 打散标签，入库，统计
        setupLabels(article.getLabel());
        // 返回结果,只有一种case使用到这个ID
        // 如果要做程序自动保存成草稿（比如说每30秒保存一次，就需要加上这个ID了，否则会创建多个Item）
        return ResponseResult.SUCCESS(Constants.Article.STATE_DRAFT.equals(state) ? "草稿保存成功" : "文章发表成功.")
                .setData(article.getId());
    }

    private void setupLabels(String labels) {
        List<String> labelList = new ArrayList<>();
        if(labels.contains("_")){
            labelList.addAll(Arrays.asList(labels.split("_")));
        }else{
            labelList.add(labels);
        }
        for (String label : labelList) {
            int result = labelDao.updateLabelCount(label);
            if (result == 0) {
                Label newLabel = new Label();
                newLabel.setCount(1);
                newLabel.setName(label);
                newLabel.setId(idWorker.nextId() + "");
                newLabel.setCreateTime(new Date());
                newLabel.setUpdateTime(new Date());
            }else{
                labelDao.updateLabelTime(new Date(), label);
            }
        }
    }

    /**
     *
     * 管理-获取文章列表
     *
     * @param page
     * @param size
     * @param state 删除、草稿、发表、置顶
     * @param keyword
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult listArticle(int page, int size, String state, String keyword, String categoryId) {
        //检查参数
        page = checkPage(page);
        size = checkSize(size);
        if(page == 1){
            String articleListJson = (String) redisUtils.get(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE + categoryId);
            if (!TextUtils.isEmpty(articleListJson)) {
                PageList<ArticleNoContent> articleList = gson.fromJson(articleListJson,new TypeToken<PageList<ArticleNoContent>>() {}.getType());
                return ResponseResult.SUCCESS("获取评论列表成功").setData(articleList);
            }
        }
        //创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page, size, sort);
        Page<ArticleNoContent> all = articleNoContentDao.findAll(new Specification<ArticleNoContent>() {
            @Override
            public Predicate toPredicate(Root<ArticleNoContent> root, CriteriaQuery<?> aq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!TextUtils.isEmpty(state)) {
                    Predicate preState = cb.equal(root.get("state").as(String.class), state);
                    predicates.add(preState);
                }
                if (!TextUtils.isEmpty(keyword)) {
                    Predicate perKeyword = cb.like(root.get("title").as(String.class), "%" + keyword + "%");
                    predicates.add(perKeyword);
                }
                if (!TextUtils.isEmpty(categoryId)) {
                    Predicate preCategory = cb.equal(root.get("categoryId").as(String.class), categoryId);
                    predicates.add(preCategory);
                }
                Predicate[] perArray = new Predicate[predicates.size()];
                predicates.toArray(perArray);
                return cb.and(perArray);
            }
        }, pageable);
        PageList<ArticleNoContent> result = new PageList<>();
        result.parsePage(all);
        if(page == 1){
            redisUtils.set(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE + categoryId, gson.toJson(result), Constants.TimeValueInSecond.MINUTE_5 );
        }
        //返回结果
        return ResponseResult.SUCCESS("获取文章列表成功.").setData(result);
    }


    @Override
    public ResponseResult getArticleById(String articleId) {
        //从redis中获取文章
        String articleJson = (String) redisUtils.get(Constants.Article.KEY_REDIS_ARTICLE_CACHE + articleId);
        if (!TextUtils.isEmpty(articleJson)) {
            Article article = gson.fromJson(articleJson, Article.class);
            //增加阅读量
            redisUtils.incr(Constants.Article.KEY_REDIS_ARTICLE_VIEW_COUNT + articleId, 1);
            return ResponseResult.SUCCESS("获取文章成功.").setData(article);
        }
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED("该文章不存在");
        }
        //普通用户只可以获取置顶、发表的文章
        if(Constants.Article.STATE_PUBLISH.equals(article.getState())
        && Constants.Article.STATE_TOP.equals(article.getState())){
            //正常访问
            redisUtils.set(Constants.Article.KEY_REDIS_ARTICLE_CACHE + articleId, gson.toJson(article), Constants.TimeValueInSecond.MINUTE_5);
            String viewCount = (String) redisUtils.get(Constants.Article.KEY_REDIS_ARTICLE_VIEW_COUNT + articleId);
            if (TextUtils.isEmpty(viewCount)) {
                long newViewCount = article.getViewCount() + 1;
                redisUtils.set(Constants.Article.KEY_REDIS_ARTICLE_VIEW_COUNT + articleId, String.valueOf(newViewCount));
            }else{
                //增加阅读量
                long newCount = redisUtils.incr(Constants.Article.KEY_REDIS_ARTICLE_VIEW_COUNT + articleId, 1);
                article.setViewCount(newCount);
                articleDao.save(article);
                //更新solr的阅读量
                solrService.updateArticle(articleId, article);
            }
            ResponseResult.SUCCESS("获取文章成功.").setData(article);
        }
        //判断管理员身份
        SobUser sobUser = userService.checkSobUser();
        if(sobUser == null || !Constants.User.ROLE_ADMIN.equals(sobUser.getRoles()) ){
            return ResponseResult.PERMISSION_FORBID();
        }
        return ResponseResult.SUCCESS("获取文章成功.").setData(article);
    }

    /**
     * 更新文章信息
     * 标题、内容、标签、分类、摘要
     * @param articleId
     * @param article
     * @return
     */
    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        Article articleFormDb = articleDao.findOneById(articleId);
        if (articleFormDb == null) {
            return ResponseResult.FAILED("需要修改的文章不存在");
        }
        String title = article.getTitle();
        if (!TextUtils.isEmpty(title)) {
            articleFormDb.setTitle(title);
        }
        String content = article.getContent();
        if (!TextUtils.isEmpty(content)) {
            articleFormDb.setTitle(content);
        }
        String label = article.getLabel();
        if (!TextUtils.isEmpty(label)) {
            articleFormDb.setTitle(label);
        }
        String categoryId = article.getCategoryId();
        if (!TextUtils.isEmpty(categoryId)) {
            articleFormDb.setTitle(categoryId);
        }
        String summary = article.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            articleFormDb.setTitle(summary);
        }
        articleFormDb.setCover(article.getCover());
        articleFormDb.setUpdateTime(new Date());
        articleDao.save(articleFormDb);
        redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE + article.getCategoryId());
        redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_LIST_CACHE);
        return ResponseResult.SUCCESS("文章更新成功.");
    }

    @Autowired
    private CommentDao commentDao;

    @Override
    public ResponseResult deleteArticle(String articleId) {
        //删除文章的评论B
        commentDao.deleteAllByArticleId(articleId);
        int result = articleDao.deleteAllById(articleId);
        if(result > 0){
            redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_CACHE + articleId);
            solrService.deleteArticle(articleId);
        }
        return getDeleteResult(result, "文章");
    }

    @Override
    public ResponseResult deleteArticleByState(String articleId) {
        int result = articleDao.deleteArticleByUpdateState(articleId);
        if (result>0) {
            redisUtils.del(Constants.Article.KEY_REDIS_ARTICLE_CACHE + articleId);
            solrService.deleteArticle(articleId);
        }
        return getDeleteResult(result, "文章");
    }

    @Override
    public ResponseResult topArticle(String articleId) {
       Article articleFromDb = articleDao.findOneById(articleId);
       if(articleFromDb == null){
           return ResponseResult.FAILED("文章不存在.");
       }
       String state = articleFromDb.getState();
       if(Constants.Article.STATE_PUBLISH.equals(state)){
           return ResponseResult.SUCCESS("文章置顶成功.");
       }
        if(Constants.Article.STATE_TOP.equals(state)){
            return ResponseResult.SUCCESS("取消置顶成功.");
        }
        return ResponseResult.FAILED("置顶文章失败");
    }

    @Override
    public ResponseResult getTopArticle() {
        List<ArticleNoContent> result = articleNoContentDao.findAll(new Specification<ArticleNoContent>() {
            @Override
            public Predicate toPredicate(Root<ArticleNoContent> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return cb.equal(root.get("state").as(String.class), Constants.Article.STATE_TOP);
            }
        });
        return ResponseResult.SUCCESS("获取置顶文章成功").setData(result);
    }

    @Override
    public ResponseResult listRecommendArticles(String articleId, int size) {
        Article articleFromDb = articleDao.findOneById(articleId);
        if (articleFromDb == null) {
            return ResponseResult.FAILED("文章不存在");
        }
        String labels = articleFromDb.getLabels();
        List<String> articleLabels = new ArrayList<>();
        if(!labels.contains("-")){
            articleLabels.add(labels);
        }else{
            String[] splitLabels = labels.split("_");
            articleLabels.addAll(Arrays.asList(splitLabels));
        }
        String targetLabel = articleLabels.get(random.nextInt(articleLabels.size()));
        List<ArticleNoContent> listResultList = articleNoContentDao.listArticleByLikeLabel("%" + targetLabel + "%",
                articleId, size);
        if (listResultList.size() < size) {
            int dxSize = size - listResultList.size();
            List<ArticleNoContent> dxList = articleNoContentDao.listLastedArticleBySize(articleId, dxSize);
            listResultList.addAll(dxList);
        }
        return ResponseResult.SUCCESS("获取推荐文章成功").setData(listResultList);
    }

    @Override
    public ResponseResult listArticle(int page, int size, String label) {
        //检查数据
        page = checkPage(page);
        size = checkSize(size);
        //创建条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        //查询数据
        Page<ArticleNoContent> all = articleNoContentDao.findAll(new Specification<ArticleNoContent>() {
            @Override
            public Predicate toPredicate(Root<ArticleNoContent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate labelsPre = cb.like(root.get("labels").as(String.class), "%" + label + "%");
                Predicate publishPre = cb.equal(root.get("state").as(String.class), Constants.Article.STATE_PUBLISH);
                Predicate topPre = cb.equal(root.get("state").as(String.class), Constants.Article.STATE_TOP);
                Predicate or = cb.or(topPre, publishPre);
                return cb.and(or, labelsPre);
            }
        }, pageable);
        return ResponseResult.SUCCESS("获取文章成功").setData(all);
    }

    @Override
    public ResponseResult listLabels(int size) {
        size = checkSize(size);
        Sort sort = new Sort(Sort.Direction.DESC, "count");
        Pageable pageable = new PageRequest(0, size, sort);
        Page<Label> all = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("获取标签云成功");
    }

}