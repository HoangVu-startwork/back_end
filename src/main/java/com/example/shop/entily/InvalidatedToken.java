package com.example.shop.entily;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jdk.jshell.Snippet;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
public class InvalidatedToken {
    @Id
    private String id;

    private Date expiryDate;

    // Private constructor to prevent instantiation from outside
    private InvalidatedToken() {
    }

    // Builder method to start building a new InvalidatedToken
    public static Builder builder() {
        return new Builder();
    }

    // Getters for id and expiryDate
    public String getId() {
        return id;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    // Builder class for constructing InvalidatedToken objects
    public static class Builder {
        private String id;
        private Date expiryDate;

        // Private constructor to prevent instantiation from outside
        private Builder() {
        }

        // Method to set the id field
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        // Method to set the expiryDate field
        public Builder expiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        // Method to build the InvalidatedToken object
        public InvalidatedToken build() {
            InvalidatedToken token = new InvalidatedToken();
            token.id = this.id;
            token.expiryDate = this.expiryDate;
            return token;
        }
    }

}
