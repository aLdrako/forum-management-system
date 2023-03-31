package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import com.telerikacademy.web.fms.services.contracts.UserServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeMvcController extends BaseMvcController {

    private final PostServices postServices;
    private final UserServices userServices;

    public HomeMvcController(PostServices postServices, UserServices userServices) {
        this.postServices = postServices;
        this.userServices = userServices;
    }

    @ModelAttribute("mostCommentedPosts")
    public List<Post> getMostCommentedPosts() {
        return postServices.getMostCommented();
    }
    @ModelAttribute("usersCount")
    public int getUsersCount() {
        return userServices.getAll().size();
    }
    @ModelAttribute("postsCount")
    public int getPostsCount() {
        return postServices.getAll(Map.of()).size();
    }

    @ModelAttribute("mostRecentPosts")
    public List<Post> getMostRecentPosts() {
        return postServices.getMostRecent();
    }

    @GetMapping
    public String showHomePage() {
        return "index";
    }
}
