package com.onegateafrica.Controllers;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.onegateafrica.Entities.Location;
import com.onegateafrica.Payloads.request.PushTokenDto;

import com.onegateafrica.Payloads.request.UpdateForm;
import com.onegateafrica.Payloads.response.BannResponse;
import com.onegateafrica.Payloads.response.JwtResponse;
import com.onegateafrica.Repositories.LocationRepository;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.BannissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.onegateafrica.Controllers.utils.DataValidationUtils;
import com.onegateafrica.Controllers.utils.ImageIO;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Repositories.RoleRepository;
import com.onegateafrica.Service.ConsommateurService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ConsommateurController {
	private final ConsommateurService consommateurService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final BannissementService bannissementService ;

	private static String imageDirectory = System.getProperty("user.dir") + "/images/";
	private final RoleRepository roleRepository;
	private final JwtUtils jwtUtils;

	private final LocationRepository locationRepository ;

	@Autowired
	public ConsommateurController(RoleRepository roleRepository, ConsommateurService consommateurService, BCryptPasswordEncoder bCryptPasswordEncoder, BannissementService bannissementService, JwtUtils jwtUtils, LocationRepository locationRepository) {
		this.consommateurService = consommateurService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.roleRepository=roleRepository;
		this.bannissementService = bannissementService;
		this.jwtUtils = jwtUtils;
		this.locationRepository = locationRepository;
	}
	private void makeDirectoryIfNotExist(String imageDirectory) {
		File directory = new File(imageDirectory);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}



	@GetMapping("/verifierBannClient/{idConsommateur}")
	public ResponseEntity<Object> verfierBannOfRemorqueur(@PathVariable Long idConsommateur) {
		if (idConsommateur != null) {
			try {

				BannResponse bannResponse = bannissementService.verifierBannOfClient(idConsommateur);

				return ResponseEntity.status(HttpStatus.OK).body(bannResponse);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur ");
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur ");

	}

	@PutMapping("/updateConsommateur")
	public ResponseEntity<?> registerClient(@RequestBody UpdateForm body) {
		if (DataValidationUtils.isValid(body.getPhoneNumber())) {
			Consommateur consommateur = consommateurService.getConsommateur(body.getId()).get();
			if (DataValidationUtils.isValid(body.getFirstName()) &&
					DataValidationUtils.isValid(body.getLastName()) &&
					DataValidationUtils.isValid(body.getEmail())) {
				if(body.getEmail() != null && !body.getEmail().equals("")){
					consommateur.setEmail(body.getEmail());
				}
				if(body.getFirstName() != null && !body.getFirstName().equals("")){
					consommateur.setFirstName(body.getFirstName());
				}
				if(body.getLastName() != null && !body.getLastName().equals("")) {
					consommateur.setLastName(body.getLastName());
				}
				if(body.getFirstName() != null && !body.getFirstName().equals("")) {
					if(body.getLastName() != null && !body.getLastName().equals("")) {
						consommateur.setUserName(body.getFirstName() + body.getLastName());
					}
					else {
						consommateur.setUserName(body.getFirstName() + consommateur.getLastName());
					}
				}
				else{
					if(body.getLastName() != null && !body.getLastName().equals("")) {
						consommateur.setUserName(consommateur.getFirstName() + body.getLastName());
					}
				}
				if(body.getPassword() != null && !body.getPassword().equals("")) {
					consommateur.setPassword(bCryptPasswordEncoder.encode(body.getPassword()));
				}
				if(body.getPhoneNumber() != null && !body.getPhoneNumber().equals("")) {
					consommateur.setPhoneNumber(body.getPhoneNumber());
				}

				consommateur = consommateurService.saveOrUpdateConsommateur(consommateur);
				List<String> roles =consommateur.getRoles().stream()
						.map(item -> item.getRoleName().toString())
						.collect(Collectors.toList());
				String jwt = jwtUtils.generateJwtToken(consommateur.getEmail(), consommateur.getRoles());
				return ResponseEntity.ok(new JwtResponse(jwt,consommateur.getId(),
						consommateur.getUserName(),
						consommateur.getEmail(),
						roles,
						consommateur.getPhoneNumber(),
						consommateur.getFirstName(),
						consommateur.getLastName()));

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID FIELDS");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check phone number");
		}
	}

	@PutMapping("/updateConsommateurPushToken/{idConsommateur}")
	public ResponseEntity<Object> updateConsommateurPushToken(@RequestBody PushTokenDto pushTokenDto , @PathVariable Long idConsommateur) {
		if(pushTokenDto !=null && pushTokenDto.getToken()!=null) {
			try {
				Consommateur consommateur = consommateurService.getConsommateur(idConsommateur).get();
				consommateur.setExpoPushToken(pushTokenDto.getToken());
				consommateurService.saveOrUpdateConsommateur(consommateur);
				return ResponseEntity.status(HttpStatus.CREATED) .body(consommateur);
			}
			catch(Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("le token ne peut pas étre null");
	}

	@PutMapping("/updateProfilePicture")
	public ResponseEntity<String> updateClient(@RequestParam MultipartFile image, @RequestParam String phoneNumber) {
		if(phoneNumber == null || image == null){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some of the mandatory fields are null");
		}
		Consommateur consommateur = consommateurService.getConsommateurByPhoneNumber(phoneNumber);
		String photoProfilFileName = phoneNumber + "_" + image.getOriginalFilename();
		Boolean isPhotoProfilUploaded = ImageIO.uploadImage(image, photoProfilFileName);
		if (isPhotoProfilUploaded == true && !image.isEmpty()) {
			consommateur.setUserPicture(photoProfilFileName);
			consommateurService.saveOrUpdateConsommateur(consommateur);
			return ResponseEntity.status(HttpStatus.OK).body(consommateur.getUserPicture());
		} else if (isPhotoProfilUploaded == false) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID FIELDS");

	}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");
	}

	@GetMapping("/findAllConsommateur")
	public List < Consommateur > getClients() {
		return consommateurService.getConsommateurs();
	}

	@GetMapping("/findConsommateur/{id}")
	public Optional < Consommateur > getClient(@PathVariable Long id) {
		if (DataValidationUtils.isValidId(id)) {
			return consommateurService.getConsommateur(id);
		} else {
			return null;
		}
	}

	@DeleteMapping("/deleteConsommateur/{id}")
	public ResponseEntity < String > deleteClient(@PathVariable Long id) {
		try {
			if (DataValidationUtils.isValidId(id)) {
				consommateurService.deleteConsommateur(id);
				return ResponseEntity.status(HttpStatus.OK).body("deleted");
			} else {
				return null;
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete client");
		}
	}

	@GetMapping(value = "/public/picture", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody
	byte[] getUserCINPicture(
			@RequestParam("phoneNumber") String phoneNumber
	) {

		/**
		 * http://localhost:8080/api/cinPicture?cinNumber=[cinNumber]
		 */
		if (DataValidationUtils.isValid(phoneNumber)) {
			System.out.println(phoneNumber);
			Consommateur consommateur = consommateurService.getConsommateurByPhoneNumber(phoneNumber);
			String imageName = consommateur.getUserPicture();
			if (consommateur == null || imageName==null || imageName.isBlank()) {
				return ImageIO.getProfilImagePlaceholder();
			} else {
				try {
					byte[] image = ImageIO.getImage(imageName);
					return image;
				} catch (Exception ex) {
					ex.printStackTrace();
					return ImageIO.getProfilImagePlaceholder();
				}
			}
		} else {
			return ImageIO.getProfilImagePlaceholder();
		}
	}


	@PutMapping ("/noterConsommateur/{idConsommateur}")
	public ResponseEntity<String> noterConsommateur(
			@PathVariable("idConsommateur") Long idConsommateur,
			@RequestParam("nombreEtoile") Double nombreEtoile) {
		try {
			if (idConsommateur == null && nombreEtoile == null) {
				return ResponseEntity.badRequest().body("ERROR");
			} else {
				Optional<Consommateur> consommateur = consommateurService.getConsommateur(idConsommateur);
				if (consommateur == null) {
					return ResponseEntity.badRequest().body("consommateur not found");
				} else {
					double nombreDeVoteAncien = consommateur.get().getNombreDeVote();
					double ancienNote = consommateur.get().getNoteConsommateurMoyenne();
					double nouveauNombreDeVote = consommateur.get().getNombreDeVote() + 1 ;
					double nouveauNote = (ancienNote  + nombreEtoile) / (nouveauNombreDeVote);

					consommateur.get().setNoteConsommateurMoyenne(nouveauNote);
					consommateur.get().setNombreDeVote(nouveauNombreDeVote);
					consommateurService.saveOrUpdateConsommateur(consommateur.get());
					return ResponseEntity.ok().body("SUCCESS");
				}
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("id Consommmateur invalide");
		}
	}



	@GetMapping(value = "/public/pictureByID", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody
	byte[] getUserPictureById(
			@RequestParam("id") Long id
	) {

		/**
		 * http://localhost:8080/api/cinPicture?cinNumber=[cinNumber]
		 */
			System.out.println(id);
			Optional<Consommateur> consommateur = consommateurService.getConsommateur(id);
			if (consommateur.isPresent()) {
				String imageName = consommateur.get().getUserPicture();
				if (consommateur.get() == null || imageName == null || imageName.isBlank()) {
					return ImageIO.getProfilImagePlaceholder();
				} else {
					try {
						byte[] image = ImageIO.getImage(imageName);
						return image;
					} catch (Exception ex) {
						ex.printStackTrace();
						return ImageIO.getProfilImagePlaceholder();
					}
				}
			}
		return ImageIO.getProfilImagePlaceholder();
	}


	@GetMapping("/getConsommateurByPhoneNumber/{PhoneNumber}")
	public Consommateur getClientByPhoneNumber(@PathVariable String PhoneNumber) {
		if (DataValidationUtils.isValid(PhoneNumber)) {
			return consommateurService.getConsommateurByPhoneNumber(PhoneNumber);
		} else {
			return null;
		}
	}




	@GetMapping("/getConsommateurById/{id}")
	public  ResponseEntity< Object > getClientById(@PathVariable Long id) {
		if(id != null ) {
			try{
				Consommateur consommateur = consommateurService.getConsommateur(id).get();
				return ResponseEntity.status(HttpStatus.OK).body(consommateur);
			}
			catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur");
			}
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id du consommateur ne peut pas étre null");
	}



	@GetMapping("/getListeEmplacementFavoris/{idConsommateur}")
	public ResponseEntity<Object> getListeEmplacementFavoris (@PathVariable Long idConsommateur) {
		if(idConsommateur !=null){
			try{
				Consommateur consommateur = consommateurService.getConsommateur(idConsommateur).get();
				return ResponseEntity.status(HttpStatus.OK).body(consommateur.getListeEmplacementsFavoris());
			}
			catch (Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
			}

		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id consommateur ne peut pas étre null");
	}

	@PutMapping("/ajouterEmplacementAuFavoris/{idConsommateur}/{idLocation}")
	public ResponseEntity<Object> ajouterEmplacementAuListeFavoris(@PathVariable Long idConsommateur , @PathVariable Long idLocation ,@RequestParam String nomEmplacement){
		if(idConsommateur !=null && idLocation!=null && nomEmplacement!=null){
			try {
				Consommateur consommateur = consommateurService.getConsommateur(idConsommateur).get();
				Location emplacementAajouter = locationRepository.findById(idLocation).get() ;


				List<Location> listeDesFavoris = consommateur.getListeEmplacementsFavoris();
				emplacementAajouter.setNomAuListeFavoris(nomEmplacement);
				emplacementAajouter.setConsommateur(consommateur);
				listeDesFavoris.add(emplacementAajouter);

				consommateur.setListeEmplacementsFavoris(listeDesFavoris);

				consommateurService.saveOrUpdateConsommateur(consommateur);

				return ResponseEntity.status(HttpStatus.OK).body("success");

			}

			catch (Exception e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
			}



		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("les ids ne peuvent pas étre null");
	}
}
