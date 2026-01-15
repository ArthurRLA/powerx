package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerEditDto {
    private Long id;
    private String unysoftCode;
    private String cnpj;
    private String registeredName;
    private String fantasyName;
    private String address;
    private boolean active;
    private Long groupId;
    private Long mechanicApurationId;
    private Long industryId;
    private Long flagId;
    private Long userId;
    private List<Long> employeeIds;

    private List<GroupDto>   groups;
    private List<UserDto>    users;
    private List<MechanicApurationDto> mechanicApurations;
    private List<IndustryDTO>     industries;
    private List<FlagDTO>         flags;
    private List<EmployeeDTO>     employees;
}
