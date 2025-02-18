package project.trendpick_pro.global.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties
public class DataProperties {

    private List<String> tags;
    private List<String> mainCategory;
    private List<String> outer;
    private List<String> top;
    private List<String> bottom;
    private List<String> shoes;
    private List<String> bag;
    private List<String> accessory;

    private List<String> brand;
    private List<String> colors;

    private Sizes sizes;

    @Data
    public static class Sizes {
        private List<String> tops;
        private List<String> bottoms;
        private List<String> shoes;
    }

}
