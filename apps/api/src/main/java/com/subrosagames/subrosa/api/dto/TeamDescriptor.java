package com.subrosagames.subrosa.api.dto;

import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectDescriptor;
import lombok.Data;

@Data
public class TeamDescriptor implements DomainObjectDescriptor {

    private Optional<String> name;
}
