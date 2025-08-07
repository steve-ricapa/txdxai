package com.example.txdxai.utilconfig;

import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.model.User;
import com.example.txdxai.rest.dto.UserDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // ✅ User → UserDto
        Converter<User, UserDto> userToUserDto = context -> {
            User source = context.getSource();
            UserDto dto = new UserDto();
            dto.setId(source.getId());
            dto.setUsername(source.getUsername());
            dto.setEmail(source.getEmail());
            dto.setCompanyId(source.getCompany().getId());
            dto.setRoleName(source.getRole() != null ? source.getRole().name() : null);
            return dto;
        };

        // ✅ UserDto → User
        Converter<UserDto, User> userDtoToUser = context -> {
            UserDto dto = context.getSource();
            User user = new User();
            user.setId(dto.getId());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            if (dto.getRoleName() != null) {
                try {
                    user.setRole(Role.valueOf(dto.getRoleName()));
                } catch (IllegalArgumentException e) {
                    // Por si acaso el string recibido no corresponde al enum
                    throw new RuntimeException("Rol inválido: " + dto.getRoleName());
                }
            }
            return user;
        };

        modelMapper.createTypeMap(User.class, UserDto.class).setConverter(userToUserDto);
        modelMapper.createTypeMap(UserDto.class, User.class).setConverter(userDtoToUser);

        return modelMapper;
    }
}
