package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.dtos.GroupDto;
import br.ind.powerx.gestaoOperacional.repositories.GroupRepository;

@Service
public class GroupService {

	private final GroupRepository groupRepository;
	
	@Autowired
	public GroupService(GroupRepository groupRepository) {
		this.groupRepository = groupRepository;
	}
	
	public List<Group> findAll(){
		return groupRepository.findAll();
	}

	public List<GroupDto> getGroupsSelect() {
		return groupRepository.findAllOrderByNameAsc().stream()
				.map( g -> {
					var dto = new GroupDto();
					dto.setId(g.getId());
					dto.setName(g.getName());
					
					return dto;
				})
				.toList();
	}
}
