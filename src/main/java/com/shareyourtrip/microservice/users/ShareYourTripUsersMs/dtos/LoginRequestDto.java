package com.shareyourtrip.microservice.users.ShareYourTripUsersMs.dtos;


import com.shareyourtrip.microservice.users.ShareYourTripUsersMs.exceptions.Messages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = Messages.MANDATORY_EMAIL)
    @Email(message = Messages.EMAIL_WRONG_FORMAT)
    @Getter
    @Setter
    private String email;

    @NotBlank(message = Messages.MANDATORY_PASSWORD)
    private String password;

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
