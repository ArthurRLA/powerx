package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.PositionDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RegisterDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.RoleDto;
import br.ind.powerx.gestaoOperacional.model.dtos.StateDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ChangePasswordDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.UserDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserEditDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserSelectDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserUpdateDTO;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;

	private final CustomerRepository customerRepository;

	private final PasswordEncoder encoder;
	
	private final AuthenticationService authenticationService;

	@Autowired
	public UserService(UserRepository userRepository, CustomerRepository customerRepository,
			PasswordEncoder encoder, AuthenticationService authenticationService) {
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.encoder = encoder;
		this.authenticationService = authenticationService;
	}

	public void save(RegisterDTO registerDTO) throws BadRequestException {
		if (this.userRepository.findByEmail(registerDTO.email()) != null)
			throw new BadRequestException();

		String encryptedPassword = encoder.encode(registerDTO.password());

		User user = new User();
		user.setId(null);
		user.setName(registerDTO.name());
		user.setEmail(registerDTO.email());
		user.setPhone(registerDTO.phone());
		user.setPasswordHash(encryptedPassword);
		user.setRole(registerDTO.role());
		user.setPosition(registerDTO.position());
		user.setState(registerDTO.state());
		user.setActive(true);
		userRepository.save(user);
	}

	public void save(User userToSave) {
		String encryptedPassword = new BCryptPasswordEncoder().encode(userToSave.getPasswordHash());
		User user = new User();
		user.setId(null);
		user.setUnysoftCode(userToSave.getUnysoftCode());
		user.setName(userToSave.getName());
		user.setCpf(userToSave.getCpf());
		user.setBirthday(userToSave.getBirthday());
		user.setAddress(userToSave.getAddress());
		user.setEmail(userToSave.getEmail());
		user.setPhone(userToSave.getPhone());
		user.setPasswordHash(encryptedPassword);
		user.setRole(userToSave.getRole());
		user.setPosition(userToSave.getPosition());
		user.setState(userToSave.getState());
		user.setActive(true);
		user.setStartOfActivities(userToSave.getStartOfActivities());

		for (Customer c : userToSave.getCustomers()) {
			user.addCustomer(c);
		}

		userRepository.save(user);

	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public void update(UserUpdateDTO userUpdateDTO, List<Long> customerIds) {
		User existingUser = getUserById(userUpdateDTO.id());

		updateUserDetails(existingUser, userUpdateDTO);

		updateUserCustomers(existingUser, customerIds);

		userRepository.save(existingUser);
	}

	private User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + id));
	}

	private void updateUserDetails(User user, UserUpdateDTO dto) {
		user.setUnysoftCode(dto.unysoftCode());
		user.setName(dto.name());
		user.setCpf(dto.cpf());
		user.setAddress(dto.address());
		user.setBirthday(dto.birthday());
		user.setEmail(dto.email());
		user.setPhone(dto.phone());
		user.setRole(dto.role());
		user.setPosition(dto.position());
		user.setState(dto.state());
		user.setActive(dto.active());
	}

	private void updateUserDetails(User user, User dto) {
		user.setUnysoftCode(dto.getUnysoftCode());
		user.setName(dto.getName());
		user.setCpf(dto.getCpf());
		user.setAddress(dto.getAddress());
		user.setBirthday(dto.getBirthday());
		user.setEmail(dto.getEmail());
		user.setRole(dto.getRole());
		user.setPosition(dto.getPosition());
		user.setState(dto.getState());
		user.setActive(true);
	}

	private void updateUserCustomers(User user, List<Long> customerIds) {
		List<Customer> updatedCustomers;

		if (customerIds != null) {
			updatedCustomers = customerRepository.findAllById(customerIds);
		} else {
			updatedCustomers = new ArrayList<>();
			;
		}

		for (Customer customer : new ArrayList<>(user.getCustomers())) {
			user.removeCustomer(customer);
		}

		for (Customer customer : updatedCustomers) {
			user.addCustomer(customer);
		}
	}

	public Page<User> filterUsers(List<Position> positions, List<State> states, boolean active, Pageable pageable) {
		Specification<User> spec = Specification.where(null);

		System.out.println("States no service :" + states);
		System.out.println("Positions no service :" + positions);

		if ((positions != null && !positions.isEmpty())) {
			spec = spec.and(UserSpecifications.positionsIn(positions));
		}

		if ((states != null && !states.isEmpty())) {
			spec = spec.and(UserSpecifications.statesIn(states));
		}

		spec = spec.and(UserSpecifications.isActive(active));

		return userRepository.findAll(spec, pageable);
	}

	public List<User> findAllByActiveTrue() {
		return userRepository.findAllByActiveTrue();
	}

	@Transactional
	public void update(Long id, User user) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User não encontrado"));
		updateUserDetails(existingUser, user);
	}

	public List<User> findAllByActiveTrueOrderByNameAsc() {
		return userRepository.findAllByActiveTrueOrderByNameAsc();
	}

	public List<User> findAllOrderByNameAsc() {
		return userRepository.findAllOrderByNameAsc();
	}

	public List<UserSelectDto> getUsersSelect() {
		return userRepository.findAllByActiveTrueOrderByNameAsc().stream()
				.map(u -> new UserSelectDto(u.getId(), u.getName()))
				.toList();
	}

	public UserDetailsDto getUserDetails(Long id) {
		return findById(id).stream()
				.map(u -> {
					var dto = new UserDetailsDto();
					dto.setId(u.getId());
					dto.setUnysoftCode(u.getUnysoftCode());
					dto.setName(u.getName());
					dto.setBirthDate(u.getBirthday());
					dto.setCpf(u.getCpf());
					dto.setAddress(u.getAddress());
					dto.setEmail(u.getEmail());
					dto.setPosition(u.getPosition().getName());
					dto.setState(u.getState());
					dto.setPhone(u.getPhone());
					dto.setCreationDate(u.getCreationDate());
					dto.setStartOfActivities(u.getStartOfActivities());
					dto.setLastUpdate(u.getLastUpdate());
					dto.setActive(u.isActive() ? "Ativo" : "Inativo");
					dto.setRole(u.getRole());

					return dto;
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Usuario com ID: " + id + " não encontrado"));
	}

	public UserEditDetailsDto getUserEditDetails(Long id) {
		return findById(id).stream()
				.findFirst()
				.map(u -> {
					var dto = new UserEditDetailsDto();

					dto.setUnysoftCode(u.getUnysoftCode());
					dto.setName(u.getName());
					dto.setCpf(u.getCpf());
					dto.setBirthDate(u.getBirthday());
					dto.setAddress(u.getAddress());
					dto.setPhone(u.getPhone());
					dto.setEmail(u.getEmail());
					dto.setCurrentPosition(u.getPosition());
					dto.setCurrentState(u.getState());
					dto.setCurrentRole(u.getRole());
					dto.setActive(u.isActive());

					dto.setCurrentCustomers(
							u.getCustomers().stream()
									.map(Customer::getId)
									.toList());

					List<PositionDto> allPositions = Arrays.asList(Position.values()).stream()
							.map(p -> {
								var pDto = new PositionDto();
								pDto.setId(p);
								pDto.setName(p.getName());

								return pDto;
							})
							.toList();

					List<StateDto> allStates = Arrays.asList(State.values()).stream()
							.map(s -> {
								var sDto = new StateDto();
								sDto.setId(s);
								sDto.setName(s.getState());

								return sDto;
							})
							.toList();

					List<RoleDto> allRoles = new ArrayList<>();
					allRoles.add(new RoleDto("ROLE_ADMIN", "Administrador"));
					allRoles.add(new RoleDto("ROLE_USER", "Usuário Comum"));
					allRoles.add(new RoleDto("ROLE_READER", "Usuário Leitor"));

					List<CustomerDto> customersWithoutUser = customerRepository
							.findAllByUserIdNullOrderByFantasyNameAsc().stream()
							.map(c -> {
								var cDto = new CustomerDto();
								cDto.setId(c.getId());
								cDto.setName(c.getFantasyName());

								return cDto;
							})
							.toList();

					List<CustomerDto> currentCustomers = u.getCustomers().stream()
							.map(c -> {
								var cDto = new CustomerDto();
								cDto.setId(c.getId());
								cDto.setName(c.getFantasyName());

								return cDto;
							})
							.toList();

					List<CustomerDto> allAvailableCustomers = new ArrayList<>();
					allAvailableCustomers.addAll(customersWithoutUser);
					allAvailableCustomers.addAll(currentCustomers);

					dto.setAllPositions(allPositions);
					dto.setAllStates(allStates);
					dto.setAllRoles(allRoles);
					dto.setAllAvailableCustomers(allAvailableCustomers);

					return dto;
				})
				.orElseThrow(() -> new EntityNotFoundException("Usuario com Id: " + id + " não encontrado"));
	}
	
	@Transactional
	public void changePassword(ChangePasswordDTO changePasswordDTO) {
		User currentUser = authenticationService.getUserAuthenticated();
		
		// Verificar se a senha atual está correta
		if (!encoder.matches(changePasswordDTO.getCurrentPassword(), currentUser.getPasswordHash())) {
			throw new IllegalArgumentException("Senha atual incorreta");
		}
		
		// Verificar se a nova senha é diferente da atual
		if (encoder.matches(changePasswordDTO.getNewPassword(), currentUser.getPasswordHash())) {
			throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual");
		}
		
		// Criptografar e salvar a nova senha
		String encodedNewPassword = encoder.encode(changePasswordDTO.getNewPassword());
		currentUser.setPasswordHash(encodedNewPassword);
		
		userRepository.save(currentUser);
	}
}
