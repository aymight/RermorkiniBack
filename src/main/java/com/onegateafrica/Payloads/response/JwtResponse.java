package com.onegateafrica.Payloads.response;

import com.onegateafrica.Entities.Consommateur;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {
	private String token;
	private Long id;
	private String phoneNumber;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private List<String> roles;
	private long idRemorqueur;

	public JwtResponse(String accessToken ) {

		this.token = accessToken;

	}


	public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles,String phoneNumber, String firstName, String lastName) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.phoneNumber=phoneNumber;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
