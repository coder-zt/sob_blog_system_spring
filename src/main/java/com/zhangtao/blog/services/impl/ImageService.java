package com.zhangtao.blog.services.impl;

import com.zhangtao.blog.dao.ImageDao;
import com.zhangtao.blog.pojo.Image;
import com.zhangtao.blog.pojo.SobUser;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.IImageService;
import com.zhangtao.blog.services.IUserService;
import com.zhangtao.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class ImageService extends BaseService implements IImageService {



    @Value("${blog.image.path}")
    private String imagePath;

    @Value("${blog.image.max-size}")
    private long imageMaxSize;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ImageDao imageDao;

    @Autowired
    private IUserService userService;

    /** 上传路径可以配置
     *
     * 限制文件的大小
     * 上传的内容： 命名->可以用ID --->每天一个文件夹
     * 保存路径到数据库
     * ID|存储路径|url|原名称|用户ID|状态|创建日期|更新日期
     *
     * @param file
     * @param original
     * @return
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file, String original) {
        //判断文件是否为空
        if (file == null) {
            return ResponseResult.FAILED("图片不能为空");
        }
        //文件类型限制 png、jpg、fig
        String contentType = file.getContentType();
        if (TextUtils.isEmpty(contentType)) {
            return ResponseResult.FAILED("图片格式错误");
        }
        String originFileName = file.getOriginalFilename();
        String type = null;
        type = getType(contentType, originFileName);
        if(type == null){
            return ResponseResult.FAILED("图片格式不支持");
        }
        //获取相关数据
        String name = file.getName();
        //限制文件大小
        long size = file.getSize();
        log.info("maxSize ===> " + imageMaxSize + "  size ===> " + size);
        if(size > imageMaxSize){
            return ResponseResult.FAILED("图片最大仅支持" + imageMaxSize / 1024 / 1024 +"Mb");
        }
        //创建图片创建保存文件夹
        //规则：配置目录/日期/类型/ID.类型
        long currentMillions = System.currentTimeMillis();
        String currentDay = new SimpleDateFormat("yyyy_MM_dd").format(currentMillions);
        String dayPath = imagePath + File.separator + currentDay;
        File dayDirectory = new File(dayPath);
        //判断日期文件夹
        if (!dayDirectory.exists()) {
            dayDirectory.mkdir();
        }
        String targetName = idWorker.nextId() + "";
        String targetPath = dayPath + File.separator + type + File.separator + targetName + "." + type;
        File targetFile = new File(targetPath);
        //判断文件类型文件夹
        if(!targetFile.getParentFile().exists()){
            targetFile.getParentFile().mkdir();
        }
        //保存文件
        try {
            if(!targetFile.exists()){
                targetFile.createNewFile();
                log.info("file targetPath ===> " + targetFile);
            }
            file.transferTo(targetFile);
            Image image = new Image();
            image.setId(targetName);
            image.setContentType(contentType);
            image.setCreateTime(new Date());
            image.setUpdateTime(new Date());
            image.setName(originFileName);
            image.setPath(targetPath);
            String resultPath = currentMillions + "_" + targetName + "." + type;
            image.setUrl(resultPath);
            image.setOriginal(original);
            image.setState("1");
            SobUser sobUser = userService.checkSobUser();
            log.info("sobuser id ====> " + sobUser.getId());
            image.setUserId(sobUser.getId());
            imageDao.save(image);
            //返回结果
            Map<String, String> result = new HashMap<>();
            result.put("path", resultPath);
            result.put("name", originFileName);
            return ResponseResult.SUCCESS("图片上传成功!").setData(result);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return ResponseResult.FAILED("图片上传失败，请稍后重试！");
    }

    private String getType(String contentType, String originFileName) {
        log.info("originFileName ===> " + originFileName);
        log.info("contentType ===> " + contentType);
        if(Constants.ImageType.TYPE_PNG_WHIT_PREFIX.equals(contentType)
                && originFileName.endsWith(Constants.ImageType.TYPE_PNG)){
            return Constants.ImageType.TYPE_PNG;
        }else if(Constants.ImageType.TYPE_JPG_WHIT_PREFIX.equals(contentType)
                && originFileName.endsWith(Constants.ImageType.TYPE_JPG)){
            return Constants.ImageType.TYPE_JPG;
        }else if(Constants.ImageType.TYPE_GIF_WHIT_PREFIX.equals(contentType)
                && originFileName.endsWith(Constants.ImageType.TYPE_GIF)){
            return Constants.ImageType.TYPE_GIF;
        }
        return null;
    }

    private final Object mLock = new Object();

    @Override
    public void viewImage(String imageId) throws IOException {
        log.info("imageId ===> " + imageId);
        String[] splits = imageId.split("_");
        long saveTime = Long.parseLong(splits[0]);
        String dayPath;
//        synchronized (mLock){
//            dayPath = simpleDateFormat.format(saveTime);
//        }
        dayPath = new SimpleDateFormat("yyyy_MM_dd").format(saveTime);
        String name = splits[1].replace("=",". ");
        log.info("name ===> " + name);
        String type = name.substring(name.length()-3);
        String filePath = imagePath + File.separator + dayPath + File.separator + type + File.separator + name;
        File file = new File(filePath);
        OutputStream writer = null;
        FileInputStream fis = null;
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try {
            response.setContentType("image/png");
            writer = response.getOutputStream();
            fis = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = fis.read(buff)) != -1){
                writer.write(buff, 0, len);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                fis.close();
            }
            if(writer != null){
                writer.close();
            }
        }
    }

    @Override
    public ResponseResult listImages(int page, int size, String original) {
        log.info("original is " + original);
        //检查数据
        page = checkPage(page);
        size = checkSize(size);
        SobUser sobUser = userService.checkSobUser();
        final String userID = sobUser.getId();
        //创建查询条件
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<Image> all = imageDao.findAll(new Specification<Image>() {
            @Override
            public Predicate toPredicate(Root<Image> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate preUserId =cb.equal(root.get("userId").as(String.class), userID);
                Predicate preState =cb.equal(root.get("state").as(String.class), "1");
                Predicate and = null;
                if (!TextUtils.isEmpty(original)) {
                    Predicate preOriginal =cb.equal(root.get("original").as(String.class), original);
                    and = cb.and(preState, preUserId, preOriginal);
                }else{
                    and = cb.and(preState, preUserId);
                }
                return and;
            }
        }, pageable);
        return ResponseResult.SUCCESS("获取图片列表成功!").setData(all);
    }

    @Override
    public ResponseResult deleteImage(String imageId) {
        int result = imageDao.deleteImageByUpdateState(imageId);
        return getDeleteResult(result, "图片");
    }

    @Autowired
    private RedisUtils redisUtils;
    @Override
    public void getQrCodeImage(String code) {
        //检查code是否过期
        String loginState = (String) redisUtils.get(Constants.User.KEY_PC_LOGIN_ID + code);
        if (TextUtils.isEmpty(loginState)) {
            // TODO: 2021/10/28 返回一张提示二维码过期图片 
            return;
        }
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String servletPath = request.getServletPath();
        StringBuffer requestURI = request.getRequestURL();
        String originalDomain = requestURI.toString().replace(servletPath, "");
        String content = originalDomain + Constants.APP_DOWNLOAD_URL + "====" + code;
        log.info("content === " + content);
        byte[] result = QrCodeUtils.encodeQRCode(content);
        try {
            response.setContentType("image/png");
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(result);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
