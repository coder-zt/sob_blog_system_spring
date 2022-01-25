package com.zhangtao.blog.services.impl;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.zhangtao.blog.dao.ArticleDao;
import com.zhangtao.blog.dao.ArticleNoContentDao;
import com.zhangtao.blog.pojo.Article;
import com.zhangtao.blog.utils.Constants;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SolrTestService {

    @Autowired
    private SolrClient solrClient;

    public void add(){
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "320149739176591");
        doc.addField("blog_view_count", 10);
        doc.addField("blog_title", "这是我写的第一篇文章");
        doc.addField("blog_content", "这是我写的第一篇文章的内容，且内容如下：");
        doc.addField("blog_create_time", new Date());
        doc.addField("blog_labels", "测试_博客");
        doc.addField("blog_url", "https://www.sunofbeach.net");
        doc.addField("blog_category_id", "739176591");
        try{
            solrClient.add(doc);
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void update(){
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "320149739176591");
        doc.addField("blog_view_count", 10);
        doc.addField("blog_title", "这是我写的第一篇文章-更新");
        doc.addField("blog_content", "这是我写的第一篇文章的内容，且内容如下：-更新");
        doc.addField("blog_labels", "测试_博客");
        doc.addField("blog_url", "https://www.sunofbeach.net");
        doc.addField("blog_category_id", "739176591");
        try{
            solrClient.add(doc);
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete() {
        try{
            solrClient.deleteById("320149739176591");
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void deleteAll() {
        try{
            solrClient.deleteByQuery("*");
            solrClient.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Autowired
    private ArticleDao articleDao;
    public void importAll(){
        List<Article> all = articleDao.findAll();
        for (Article article : all) {
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
    }


}
