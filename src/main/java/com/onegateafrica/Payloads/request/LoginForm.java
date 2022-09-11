package com.onegateafrica.Payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {

	//private String username;
	private String email;
	private String password;

}