package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.ChangePasswordDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.ProfileUpdateDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.UserDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserEditDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserSelectDto;
import br.ind.powerx.gestaoOperacional.services.UserService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/users")
public class RestUserController {

	private final UserService userService;
	
	@Autowired
	public RestUserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers(
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			List<UserSelectDto> dto = userService.getUsersSelect();
			
			String eTag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}
			
			return ResponseEntity
					.ok()
					.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar usuários: " + e.getMessage());
		}
	}
	
	@GetMapping("/details/{id}")
	public ResponseEntity<?> getUserDetails(
			@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			UserDetailsDto dto = userService.getUserDetails(id);
			
			String eTag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}
			
			return ResponseEntity
					.ok()
					.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar usuários: " + e.getMessage());
		}
	}
	
	@GetMapping("/edit-data/{id}")
	public ResponseEntity<?> getUserEditDetails(
			@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			UserEditDetailsDto dto = userService.getUserEditDetails(id);
			
			String eTag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}
			
			return ResponseEntity
					.ok()
					.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar usuários: " + e.getMessage());
		}
	}
	
	@PostMapping("/update-profile")
	public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDTO dto) {
		try {
			userService.updateProfileData(dto);
			return ResponseEntity.ok().body("Perfil atualizado com sucesso");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao atualizar perfil: " + e.getMessage());
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
		try {
			userService.changePassword(changePasswordDTO);
			return ResponseEntity.ok().body("Senha alterada com sucesso");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao trocar senha: " + e.getMessage());
		}
	}
}




























