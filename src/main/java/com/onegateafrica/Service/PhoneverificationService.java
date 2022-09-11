package com.onegateafrica.Service;

public interface PhoneverificationService {
   VerificationResult sendVerification(String phone);
   VerificationResult checkVerification(String phone, String code);
}
