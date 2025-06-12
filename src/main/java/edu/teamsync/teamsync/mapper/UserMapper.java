package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // User Creation mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "designation", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "predictedBurnoutRisk", ignore = true)
    Users toEntity(UserCreationDTO userCreationDTO);

    UserCreationDTO toCreationDTO(Users user);

    // User Update mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "predictedBurnoutRisk", ignore = true)
    Users toEntityFromUpdate(UserUpdateDTO userUpdateDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "predictedBurnoutRisk", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateUserFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget Users user);

    UserUpdateDTO toUpdateDTO(Users user);

    // User Response mappings
    UserResponseDTO toResponseDTO(Users user);

    List<UserResponseDTO> toResponseDTOList(List<Users> users);
}