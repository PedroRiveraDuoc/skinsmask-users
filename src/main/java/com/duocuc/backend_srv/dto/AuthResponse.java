package com.duocuc.backend_srv.dto;

/**
 * AuthResponse represents the structure of the response returned after user authentication.
 */
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String message; // Optional message for response context (e.g., success/failure)

    /**
     * Full Constructor for detailed responses including token, user details, and message.
     *
     * @param token    The JWT token.
     * @param id       The user's ID.
     * @param username The user's username.
     * @param email    The user's email.
     * @param message  Optional message.
     */
    public AuthResponse(String token, Long id, String username, String email, String message) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.message = message;
    }

    /**
     * Constructor for token-only response.
     *
     * @param token   The JWT token.
     * @param message Optional message.
     */
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
