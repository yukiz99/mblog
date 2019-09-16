package club.msecret.mblog.controller.admin;

import com.alibaba.fastjson.JSON;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import club.msecret.mblog.entity.Article;
import club.msecret.mblog.entity.Category;
import club.msecret.mblog.entity.CategoryOfArticle;
import club.msecret.mblog.entity.Tag;
import club.msecret.mblog.entity.TagOfArticle;
import club.msecret.mblog.service.ArticleService;
import club.msecret.mblog.service.CategoryService;
import club.msecret.mblog.service.TagService;
import club.msecret.mblog.utils.TimeUtil;

@Controller
@RequestMapping("/admin")
public class BlogManageController {

    @Resource
    private ArticleService articleService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    /*@RequestMapping("/blogList")
    public ModelAndView blogManage() {
        ModelAndView mav = new ModelAndView("admin/blogManage");
        List<Article> articles = articleService.findAll();
        mav.addObject("articles", articles);
        return mav;
    }*/

    @RequestMapping("/blogList")
    public ModelAndView blogList(String category) {
        ModelAndView mav = new ModelAndView("admin/blogManage");
        List<Category> categories = categoryService.findAllCategories();
        mav.addObject("categories", categories);
        mav.addObject("cate", category == null ? " ": category);
        return mav;
    }

    @RequestMapping("/getBlog")
    @ResponseBody
    public String getBlog(String category) {
        Map<String,Object> result = new HashMap<>();
        List<Article> articles;
        if (category == null)
            articles = articleService.findAll();
        else
            articles = articleService.findArticlesByCategoryName(category);
        result.put("code", 0);
        result.put("msg", articles.size() != 0? "查询成功" : "此分类下没有文章");
        result.put("count", articles.size());
        result.put("data", articles);
        return JSON.toJSONString(result);
    }


    @GetMapping("/hideBlog")
    @ResponseBody
    public String hideBlog(@RequestParam("articleId") Long articleId) {
        articleService.hideBlog(articleId);
        return "success";
    }

    @GetMapping("/showBlog")
    @ResponseBody
    public String showBlog(Long articleId) {
        articleService.showBlog(articleId);
        return "success";
    }

    @RequestMapping("/addBlog")
    public ModelAndView addBlog() {
        ModelAndView mav = new ModelAndView("admin/addBlog");
        List<Category> categories = categoryService.findAllCategories();
        List<Tag> tags = tagService.findAllTags();
        mav.addObject("categories", categories);
        mav.addObject("tags", tags);
        return mav;
    }

    @RequestMapping("/deleteBlog")
    @ResponseBody
    public String deleteBlog(Long articleId) {
        articleService.deleteBlogById(articleId);
        return "success";
    }


    @PostMapping("/checkAddBlog")
    @ResponseBody
    public String checkAddBlog(String articleId,String articleTitle, String outline, String articleContent, String category, String tags, String status) {
        Article article = new Article();
        CategoryOfArticle cov = new CategoryOfArticle();
        List<TagOfArticle> toas = new ArrayList<>();
        TimeUtil timeUtil = new TimeUtil();

        Long aid = timeUtil.transferStampToArticleId(articleId);
        article.setArticleId(aid);
        article.setArticleTitle(articleTitle);
        article.setArticleContent(articleContent);
        article.setOutline(outline);
        article.setStatus(status == null ? 0: 1);
        article.setPublishTime(timeUtil.transferStampToTime(articleId));

        cov.setArticleId(aid);
        cov.setCid(Integer.parseInt(category));

        if (!tags.equals("")) {
            String[] ts = tags.split(",");
            if (ts.length > 0)
                for (String t : ts) toas.add(new TagOfArticle(Integer.parseInt(t), aid));
        }

        articleService.addArticle(article);
        categoryService.addCategoryOfArticle(cov);
        if (toas.size() !=0)
            tagService.addTagsOfArticle(toas);

        return "success";
    }



}