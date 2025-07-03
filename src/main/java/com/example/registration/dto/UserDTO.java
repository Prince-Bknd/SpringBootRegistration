package com.example.registration.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String password;

    // Constructor (Only email required here)
    private UserDTO(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Overloaded constructor for cases where only email is needed
    public UserDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Builder Pattern
    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public static class UserDTOBuilder {
        private Long id;
        private String email;
        private String password;

        public UserDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDTOBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserDTO build() {
            if (this.password != null) {
                return new UserDTO(id, email, password);  // If password is set, use both email and password
            }
            return new UserDTO(id, email);  // If no password, only use email
        }
    }
}
