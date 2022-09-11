package com.onegateafrica.Entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private String phoneNumber;
	@JsonIgnore
	@NotNull
	private String password;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String email;

	@Column(name = "username")
	private String userName;

	@Column(name="isRemorqueur")
	private Boolean isRemorqueur=false;

	private String userPicture;

	@Column(name="is_activated")
	private boolean isActivated=true;
	//to delete
	@Column(name="is_blocked")
	private boolean isBlocked=false;
	@Column(name="is_connected")
	private Boolean isConnected=true;
	@Column(name="last_activity")
	private Date lastActivity;
	@Column(name="longitude")
	private Float longitude;
	@Column(name="latitude")
	private Float latitude;
}
