package com.metamafitness.fitnessbackend.service;

import com.metamafitness.fitnessbackend.model.AdminRole;
import com.metamafitness.fitnessbackend.model.GenericEnum;
import com.metamafitness.fitnessbackend.model.TrainerRole;

public interface AdminRoleService extends GenericService<AdminRole> {

    public AdminRole findByName(GenericEnum.RoleName roleName);
}
