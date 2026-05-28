package com.sistemasdistr.basico.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto { private Integer id;

    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false)
    private Integer showOnCreate;
}
