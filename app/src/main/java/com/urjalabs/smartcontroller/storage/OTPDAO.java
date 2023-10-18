package com.urjalabs.smartcontroller.storage;

import com.urjalabs.smartcontroller.models.OTP;

public interface OTPDAO {
    public int addOTP(OTP otp);
    public boolean verifyOTP(int otp, String email);
    public void clean();
}
