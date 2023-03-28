package com.telerikacademy.web.fms.controllers.mvc;

import com.telerikacademy.web.fms.models.Post;
import com.telerikacademy.web.fms.services.contracts.PostServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController extends BaseMvcController {

    private final PostServices postServices;

    public HomeMvcController(PostServices postServices) {
        this.postServices = postServices;
    }

    @GetMapping
    public String showHomePage() {
        return "index";
    }
    @ModelAttribute("mostCommentedPosts")
    public List<Post> getMostCommentedPosts() {
        return postServices.getMostCommented();
    }
    @ModelAttribute("mostRecentPosts")
    public List<Post> getMostRecentPosts() {
        return postServices.getMostRecent();
    }
}
