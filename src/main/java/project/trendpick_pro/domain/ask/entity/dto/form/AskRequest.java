package project.trendpick_pro.domain.ask.entity.dto.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AskRequest {

    private Long productId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @Builder
    public AskRequest(Long productId, String title, String content) {
        this.productId = productId;
        this.title = title;
        this.content = content;
    }
}