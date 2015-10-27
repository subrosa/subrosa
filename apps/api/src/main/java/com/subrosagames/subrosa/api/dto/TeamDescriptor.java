package com.subrosagames.subrosa.api.dto;

import com.google.common.base.Optional;
import com.subrosagames.subrosa.domain.DomainObjectDescriptor;
import lombok.Data;

@Data
public class TeamDescriptor implements DomainObjectDescriptor {

    private Optional<String> name;
}
