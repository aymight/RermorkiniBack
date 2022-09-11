package com.onegateafrica.Entities;

import lombok.Data;

@Data
public class OTPSystem {
  private String phoneNumber;
  private String otp;
  private long expireTime;
}
