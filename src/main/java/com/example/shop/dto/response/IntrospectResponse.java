package com.example.shop.dto.response;

public class IntrospectResponse {
    private boolean valid;

    public IntrospectResponse(boolean valid) {
        this.valid = valid;
    }

    public static class Builder {
        private boolean valid;

        public Builder active(boolean valid) {
            this.valid = valid;
            return this;
        }

        public IntrospectResponse build() {
            return new IntrospectResponse(valid);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
