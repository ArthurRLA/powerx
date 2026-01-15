package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.GroupDto;
import br.ind.powerx.gestaoOperacional.services.GroupService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/groups")
public class RestGroupController {

	private final GroupService groupService;
	
	@Autowired
	public RestGroupController(GroupService groupService) {
		this.groupService = groupService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllApurationTypes(
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			List<GroupDto> dto = groupService.getGroupsSelect();
			
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
}




























