package farming.user.mapper;

import farming.user.dto.UserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import farming.user.entity.User;

import farming.user.dto.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDto toDto(User user);

    User toEntity(UserRequestDto userRequestDto);

}
