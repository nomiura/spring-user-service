package ru.test.dto;

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
    private String name;

    @Email
    @NotBlank
    private String email;

    @Min(0)
    private int age;

    public UserRequestDto(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
