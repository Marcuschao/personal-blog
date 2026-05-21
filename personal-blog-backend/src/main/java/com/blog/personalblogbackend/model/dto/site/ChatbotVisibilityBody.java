package com.blog.personalblogbackend.model.dto.site;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChatbotVisibilityBody {
    @NotBlank
    @Pattern(regexp = "NONE|GUEST|AUTH")
    private String mode;
}
