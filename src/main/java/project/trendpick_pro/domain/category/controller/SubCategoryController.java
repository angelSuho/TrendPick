package project.trendpick_pro.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.trendpick_pro.domain.category.service.SubCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/getSubCategories")
    public List<String> getSubCategories(@RequestParam String mainCategory) {
        return subCategoryService.getAll(mainCategory);
    }
}
