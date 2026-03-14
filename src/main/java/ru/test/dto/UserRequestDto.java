package ru.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

    private Long id;

    @NotBlank
    @Schema(description = "Имя пользователя", example = "Татьяна")
    private String name;

    @Email
    @NotBlank
    @Schema(description = "Почта пользователя", example = "tryhard4girls@blondie.go")
    private String email;

    @Min(0)
    @Schema(description = "Возраст пользователя", example = "45")
    private int age;

    public UserRequestDto(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
