package hexlet.code.mapper;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.dto.User.UserDTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder encoder;

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDTO model);

    public abstract UserDTO map(User model);

    public abstract void update(UserUpdateDTO update, @MappingTarget User destination);

    @BeforeMapping
    protected void hashOnMap(UserCreateDTO model) {
        model.setPassword(encoder.encode(model.getPassword()));
    }

    @AfterMapping
    protected void hashOnUpdate(UserUpdateDTO update, @MappingTarget User destination) {
        var pwd = update.getPassword();
        if (pwd != null && pwd.isPresent()) {
            destination.setPasswordDigest(encoder.encode(pwd.get()));
        }
    }
}
