package com.onegateafrica.Payloads.request;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {

	private String phoneNumber;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private String otp;



}
