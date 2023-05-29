package project.trendpick_pro.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.trendpick_pro.domain.ask.entity.dto.request.AskRequest;
import project.trendpick_pro.domain.ask.entity.dto.response.AskResponse;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.review.entity.dto.request.ReviewRequest;
import project.trendpick_pro.domain.review.entity.dto.response.ReviewResponse;
import project.trendpick_pro.domain.review.service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trendpick/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final Rq rq;

    @PostMapping("/write")
    public String createReview(@Valid ReviewRequest reviewRequest, @RequestParam(value = "productId") Long productId, Model model) {
        Member actor = rq.getMember();
        ReviewResponse reviewResponse = reviewService.createReview(actor, reviewRequest);

        model.addAttribute("reviewResponse", reviewResponse);
        return "";

    }

    @GetMapping("/list/{reviewId}")
    public String showReview(@PathVariable Long reviewId, Model model){
        ReviewResponse reviewResponse = reviewService.showReview(reviewId);
        model.addAttribute("reviewResponse", reviewResponse);
        return "";
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);

        return "";
    }

    @PostMapping("/edit/{reviewId}")
    public String modifyAsk(@PathVariable Long reviewId, @Valid ReviewRequest reviewRequest, Model model) {
        ReviewResponse reviewResponse = reviewService.modify(reviewId, reviewRequest);

        model.addAttribute("reviewResponse", reviewResponse);
        return "";
    }
}
