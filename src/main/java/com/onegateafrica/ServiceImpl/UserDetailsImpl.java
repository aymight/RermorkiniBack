package com.onegateafrica.ServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String email;
	private String firstName;
	private String phoneNumber;
	private String lastName;
	private String userName ;
	private boolean isActivated ;


	@JsonIgnore
	private String password;


	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String userName, String email, String password,String phoneNumber,String firstName, String lastName,
						   Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.phoneNumber=phoneNumber;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public static UserDetailsImpl build(Consommateur consommateur) {
		List<GrantedAuthority> authorities = consommateur.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
				.collect(Collectors.toList());
		return new UserDetailsImpl(
				consommateur.getId(),
				consommateur.getUserName(),
				consommateur.getEmail(),
				consommateur.getPassword(),
				consommateur.getPhoneNumber(),
				consommateur.getFirstName(),
				consommateur.getLastName(),
				authorities);
	}



	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	@Override
	public String getUsername() {
		return  this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		else if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}

