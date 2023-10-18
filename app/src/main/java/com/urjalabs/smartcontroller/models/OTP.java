package com.urjalabs.smartcontroller.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OTP {
    @JsonProperty("email")
    private String email;

    @JsonProperty("otp")
    private int otp;

    @JsonProperty("time")
    private String time;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
