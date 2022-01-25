package com.zhangtao.blog.services.impl;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.pojo.PageList;
import com.zhangtao.blog.pojo.SearchResult;
import com.zhangtao.blog.responese.ResponseResult;
import com.zhangtao.blog.services.ISolrService;
import com.zhangtao.blog.utils.Constants;
import com.zhangtao.blog.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 搜索内容添加:文章发表
 *
 * 搜索内容删除：文章删除
 * 搜索内容更新：更新文章 todo
 *
 *
 */
@Slf4j
@Service
public class SolrServiceImpl extends BaseService implements ISolrService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public ResponseResult doSearch(String keywords, int page, int size, String categoryId, String sort) {
        log.info("keywords=" + keywords);
        //1.检查page和size
        page = checkPage(page);
        size = checkSize(size);
        SolrQuery solrQuery = new SolrQuery();
        //2.分页设置
        //先设置每页的数量
        solrQuery.setRows(size);
        //再这是开始的地方
        int start = size * (page -1);
        solrQuery.setStart(start);
        //3.设置搜索条件
        //关键字
        solrQuery.set("df", "search_item");
        //条件过滤
        if(TextUtils.isEmpty(keywords)){
            solrQuery.set("q", "*");
        }else{
            solrQuery.set("q", keywords);
        }
        //排序
        //排序有四种case：时间升序（1）降序（2），浏览量的升序（3）和降序（4）
        if(TextUtils.isEmpty(sort)){
            if(sort.equals("1")){
                solrQuery.setSort("blog_creat_time", SolrQuery.ORDER.asc);
            } else if(sort.equals("2")){
                solrQuery.setSort("blog_creat_time", SolrQuery.ORDER.desc);
            } else if(sort.equals("3")){
                solrQuery.setSort("blog_view_count", SolrQuery.ORDER.asc);
            } else if(sort.equals("4")){
                solrQuery.setSort("blog_view_count", SolrQuery.ORDER.desc);
            }
        }
        //分类
        if(!TextUtils.isEmpty(categoryId)){
            solrQuery.setFilterQueries("blog_category_id:" +  categoryId);
        }
        //关键字高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("blog_title, blog_content");
        solrQuery.setHighlightSimplePost("</font>");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightFragsize(500);
        //设置返回字段
        solrQuery.addField("id,blog_content,blog_create_time,blog_labels,blog_url,blog_title,blog_view_count");
        //4.搜索
        try{
            //4.1、 处理搜索的结果
            QueryResponse result = solrClient.query(solrQuery);

            //获取到高亮内容
            Map<String, Map<String, List<String>>> highLighting = result.getHighlighting();
            //将数据转为bean类
            List<SearchResult> resultList = result.getBeans(SearchResult.class);
            for (SearchResult item : resultList) {
                Map<String, List<String>> stringListMap = highLighting.get(item.getId());
                if(stringListMap == null){
                    continue;
                }
                List<String> blogContent  = stringListMap.get("blog_content");
                if (blogContent != null) {
                    item.setBlogContent(blogContent.get(0));
                }
                List<String> blogTitle  = stringListMap.get("blog_title");
                if (blogTitle != null) {
                    item.setBlogTitle(blogTitle.get(0));
                }
            }
            //5.返回搜索结果
            long numFound = result.getResults().getNumFound();
            PageList<SearchResult> pageList = new PageList<>(page, numFound, size);
            pageList.setContents(resultList);
            return ResponseResult.FAILED("搜索成功.").setData(pageList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseResult.FAILED("搜索失败，请稍后重试.");
    }

    @Override
    public void addArticle(Article article){
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", article.getId());
        doc.addField("blog_view_count", article.getViewCount());
        doc.addField("blog_title", article.getTitle());
        //提取文本
        //markdown ->1 ->html
        // 富文本 -> 0 ->纯文本
        String type = article.getType();
        String html;
        if(type.equals(Constants.Article.TYPE_MARKDOWN)){
            // markdown to html
            MutableDataSet options;
            options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    JekyllTagExtension.create(),
                    TocExtension.create(),
                    SimTocExtension.create()
            ));
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node document = parser.parse(article.getContent());
            html = renderer.render(document);
        }else{
            html = article.getContent();
        }
        String content = Jsoup.parse(html).text();
        doc.addField("blog_content", content);
        doc.addField("blog_labels", article.getLabel());
        doc.addField("blog_create_time", new Date());
        doc.addField("blog_url","article/" + article.getId());
        doc.addField("blog_category_id", article.getCategoryId());
        try{
            solrClient.add(doc);
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(String articleId) {
        try{
            solrClient.deleteById(articleId);
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(String articleId, Article article) {
        article.setId(articleId);
        this.addArticle(article);
    }
}
