package com.onegateafrica.ServiceImpl;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.User;
import com.onegateafrica.Repositories.ConsommateurRepository;
import com.onegateafrica.Repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final ConsommateurRepository ConsommateurRepository;
	@Autowired
	public UserDetailsServiceImpl(ConsommateurRepository ConsommateurRepository){
		this.ConsommateurRepository=ConsommateurRepository;
	}


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Consommateur> consommateur = ConsommateurRepository.findByEmail(email);
				//.orElseThrow(() -> new UsernameNotFoundException("User Not Found with Email: " + email));;
				if(consommateur.get()==null) throw new UsernameNotFoundException(email);
		return UserDetailsImpl.build(consommateur.get());
	}

}