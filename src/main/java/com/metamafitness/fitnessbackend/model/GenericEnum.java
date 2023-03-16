package com.metamafitness.fitnessbackend.model;

public class GenericEnum {

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum RoleName {
        ADMIN,
        USER,
        TRAINER,

        DEV
    }


    public enum JwtTokenType {
        ACCESS,
        REFRESH
    }

}
