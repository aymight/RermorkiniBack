package com.onegateafrica.ServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Repositories.ConsommateurRepository;
import com.onegateafrica.Service.ConsommateurService;

@Service
@Transactional
public class ConsommateurServiceImpl implements ConsommateurService {


	private final ConsommateurRepository consommateurRepository;

	public ConsommateurServiceImpl(ConsommateurRepository consommateurRepository){
		this.consommateurRepository=consommateurRepository;

	}
	@Override
	public Consommateur saveOrUpdateConsommateur(Consommateur consommateur) {
		return consommateurRepository.save(consommateur);
	}



	@Override
	public List<Consommateur> getConsommateurs() {
		return consommateurRepository.findAll();
	}

	@Override
	public Optional<Consommateur> getConsommateur(Long id) {
		return consommateurRepository.findById(id);
	}

	@Override
	public void deleteConsommateur(Long id) {
		consommateurRepository.deleteById(id);
	}

	@Override
	public Boolean existsByEmail(String email) {
		if (consommateurRepository.existsByEmail(email)) {
			return true;
		}

		return false;

	}

	@Override
	public Consommateur getConsommateurByPhoneNumber(String PhoneNumber) {
		return consommateurRepository.findByPhoneNumber(PhoneNumber);
	}
	@Override
	public Optional<Consommateur> getConsommateurByEmail(String Email) {
		return consommateurRepository.findByEmail(Email);
	}

	@Override
	public int keepAlive(String email) {
		return consommateurRepository.keepalive(email, new Date());
	}

	@Override
	public int kill() {
		return consommateurRepository.kill();
	}

}
