package com.onegateafrica.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


import com.onegateafrica.Entities.Consommateur;

public interface ConsommateurService {
	
	 Consommateur saveOrUpdateConsommateur(Consommateur user);

	 List<Consommateur> getConsommateurs();

	 Optional<Consommateur> getConsommateur(Long id);

	 void deleteConsommateur(Long id);

	 Boolean existsByEmail(String email);

	 Consommateur getConsommateurByPhoneNumber(String PhoneNumber);

	 Optional<Consommateur> getConsommateurByEmail(String Email);

	 int keepAlive(String email);
	 int kill();


}
