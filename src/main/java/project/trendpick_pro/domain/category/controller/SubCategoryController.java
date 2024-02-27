package project.trendpick_pro.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/getSubCategories")
    public List<String> getSubCategories(@RequestParam String mainCategory) {
        return subCategoryService.findAll(mainCategory);
    }
}
