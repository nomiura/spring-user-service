package ru.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDto {

    @Schema(description = "Уникальный номер пользователя", example = "1L")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Михаил")
    private String name;

    @Schema(description = "Почта пользователя", example = "mishanya@femboy.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "33")
    private int age;
}
