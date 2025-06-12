package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.authDTO.AuthResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    AuthResponseDTO toAuthResponseDTO(UserResponseDTO user, String token, String refreshToken);
    UserResponseDTO toCurrentUserResponseDTO(Users user);

}