package com.onegateafrica.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onegateafrica.Entities.Remorqueur;
import com.onegateafrica.Repositories.RemorqueurRepository;
import com.onegateafrica.Service.RemorqueurService;

@Service
@Transactional
public  class RemorqueurServiceImpl implements RemorqueurService {

	private final RemorqueurRepository remorqueurRepository;
	@Autowired
	public RemorqueurServiceImpl(RemorqueurRepository remorqueurRepository){
		this.remorqueurRepository = remorqueurRepository;
	}

	@Override
	public Remorqueur saveOrUpdateRemorqueur(Remorqueur remorqueurLibre) {
		return remorqueurRepository.save(remorqueurLibre);
	}

	@Override
	public Optional <Remorqueur> findRemorqeurByCIN(String cin) {
		return remorqueurRepository.findByCinNumber(cin);
	}

	@Override
	public List<Remorqueur> findAll() {
		return remorqueurRepository.findAll();
	}

	@Override
	public List<Remorqueur> getRemorqueurs() {
		return remorqueurRepository.findAll();
	}

	@Override
	public Optional<Remorqueur> getRemorqueur(Long id) {
		return remorqueurRepository.findById(id);
	}

	@Override
	public void deleteRemorqueur(Long id) {
		remorqueurRepository.deleteById(id);
	}

	@Override
	public Optional<Remorqueur> findRemorqueurByPhoneNumber(String phoneNumber) {
		return remorqueurRepository.findByPhoneNumber(phoneNumber);
	}

	@Override
	public void updateDisponibility(long id, boolean disponibility) {
		remorqueurRepository.updateDisponibility(id , disponibility);
	}

	@Override
	public Optional<Remorqueur> getConsommateurAsRemorqeur(Long idConsommateur) {
		return remorqueurRepository.getUserAsRemorqeur(idConsommateur);
	}
}
