package com.zhangtao.blog.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.zhangtao.blog.dao.LabelDao;
import com.zhangtao.blog.pojo.Hourse;
import com.zhangtao.blog.pojo.Label;
import com.zhangtao.blog.pojo.User;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.RedisUtils;
import com.zhangtao.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.*;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController//@Controller
@Transactional
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LabelDao labelDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisUtils redisUtils;
    //@ResponseBody 当类注解由Controller改变为RestController,不再需要该注解
//    @RequestMapping(value="/hello_world", method = RequestMethod.GET)
    @GetMapping("/hello_world")
    public ResponseResult helloWorld(){
        log.info("hello world!");
        return ResponseResult.SUCCESS().setData(redisUtils.get(Constants.User.KEY_REDIS_CAPTCHA + "12456"));
    }

    @GetMapping("/test_json")
    public ResponseResult getUserInfo(){
        //测试
        return ResponseResult.SUCCESS().setData(new User("拜登", 75, "male",
                new Hourse("华盛顿", "白宫")));
    }

    @PostMapping("/test_login")
    public ResponseResult testLogin(@RequestBody User user){
        log.info("user name ---> " + user.getName());
        log.info("user password ---> " + user.getPassword());
        return ResponseResult.SUCCESS().setData(user);
    }

    @PostMapping("/label")
    public ResponseResult addLabel(@RequestBody Label label){
        //判断数据是否有效
        if(TextUtils.isEmpty(label.getName())){
            return ResponseResult.FAILED("标签名字为空");
        }
        label.setCount(label.getCount() < 0?0:label.getCount());
        //补全数据
        label.setId(String.valueOf(idWorker.nextId()));
        label.setCreateTime(new Date());
        label.setUpdate_time(new Date());
        //保存数据
        labelDao.save(label);
        return ResponseResult.SUCCESS("添加标签成功");
    }

    @DeleteMapping("/label/{labelId}")
    public ResponseResult deleteLabel(@PathVariable("labelId")String labelId){
        log.info("delete id is " + labelId);
        int count = labelDao.customDeleteLabelById(labelId);
        log.info("count ===> " + count);
        if(count == 0){
            return ResponseResult.FAILED("删除标签失败");
        }
        return ResponseResult.SUCCESS("删除标签成功");
    }

    @PutMapping("/label/{labelId}")
    public ResponseResult updateLabel(@PathVariable("labelId") String labelId, @RequestBody Label label){
        Label updateLabel = labelDao.findOneById(labelId);
        if(updateLabel == null){
            return ResponseResult.FAILED("目标标签不存在！");
        }
        updateLabel.setName(label.getName());
        updateLabel.setCount(label.getCount());
        updateLabel.setUpdate_time(new Date());
        labelDao.save(updateLabel);
        return ResponseResult.SUCCESS("修改成功");
    }

    @GetMapping("/label/{labelId}")
    public ResponseResult getLabelById(@PathVariable("labelId")String labelId){
        Label label = labelDao.findOneById(labelId);
        if(label == null){
            return ResponseResult.FAILED("标签不存在！");
        }
        return ResponseResult.SUCCESS("获取表签信息成功").setData(label);
    }

    @GetMapping("/label/list/{page}/{size}")
    public ResponseResult listLabel( @PathVariable("page")int page, @PathVariable("size")int size){
        page = page>0?page:1;
        size = size>0?size: Constants.DEFAULT_SIZE;
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        Pageable pageable =new PageRequest(page-1, size, sort);
        Page<Label> result = labelDao.findAll(pageable);
        return ResponseResult.SUCCESS("查询成功").setData(result);
    }

    @GetMapping("/label/search/{keyValue}")
    public ResponseResult doSearch(@PathVariable("keyValue") final String keyValue){
        List<Label> all = labelDao.findAll(new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate namePredicate = cb.like(root.get("name").as(String.class), "%" + keyValue + "%");//相似查找：like
                Predicate countPredicate = cb.equal(root.get("count").as(Integer.class), 999);//值比较
                return cb.and(namePredicate, countPredicate);
            }
        });
        if (all.size() == 0) {
            return ResponseResult.FAILED("查询失败,结果为空！");
        }
        return ResponseResult.SUCCESS("查询成功").setData(all);
    }

    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        String text = specCaptcha.text().toLowerCase();
        log.info("captcha ---> text: " + text);
        redisUtils.set(Constants.User.KEY_REDIS_CAPTCHA + "123456", text, 60*10);
        // 验证码存入session
        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
}
//Successfully created project 'sob_blog_system_spring' on GitHub, but initial commit failed: Author identity unknown *** Please tell me who you are. Run git config --global user.email "you@example.com" git config --global user.name "Your Name" to set your account's default identity. Omit --global to set the identity only in this repository. unable to auto-detect email address (got 'Administrator@DESKTOP-MC7GUVD.(none)')