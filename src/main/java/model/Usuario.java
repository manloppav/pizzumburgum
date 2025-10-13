package model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@MappedSuperclass
public class Usuario extends BaseEntity {

    @Column(unique = true)
    String username;
    String password;

    @Column(name = "firstname")
    @NotEmpty
    String firstName;

    @Column(name = "lastname")
    @NotEmpty
    String lastName;

    @Column(name = "email")
    @Email
    @NotEmpty
    String email;

    @Column(name = "birthdate")
    @NotNull
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    LocalDate birthDate;

}
