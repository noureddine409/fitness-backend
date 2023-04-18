package com.metamafitness.fitnessbackend.model;

public class   GenericEnum {

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum ProgramLevel{
        BEGINNER,
        INTERMIDIATE,
        HARD,
        All_LEVELS
    }

    public enum ProgramCategory {
        FITNESS,
        NUTRITION,
        MINDSET
    }

    public enum ProgramOption {
        STRENGTH,
        DANCE,
        NUTRITION_RECIPES,
        MUSCLE_BUILDING,
        HIIT,
        CARDIO,
        MOBILITY_FLEXIBILITY,
        RECOVERY,
        PILATES,
        YOGA,
        SLIM_SCULPT
    }

    public enum ProgramEquipment {
        DUMBBELLS,
        ADJUSTABLE_BENCH,
        CORE_COMFORT_MAT,
        POWER_LOOPS,
        YOGA_MAT,
        RESISTANCE_LOOPS
    }

    public enum ProgramState {
        IN_PROGRESS,
        ARCHIVED,
        SUBMITTED,
        APPROVED

    }

    public enum SectionLevel {
        FIRST_LEVEL,
        SECOND_LEVEL,
        THIRD_LEVEL

    }

    public enum RoleName {
        ADMIN,
        USER,
        TRAINER,

        DEV
    }


    public enum JwtTokenType {
        ACCESS,
        REFRESH,
        RESET
    }

    public enum Region {
        IN,
        MA,
        DZ,
        AL,
        AR,
        AU,
        AT,
        BH,
        BD,
        BE,
        BR,
        VG,
        IO,
        BG,
        CM,
        CA,
        CF,
        CL,
        CN,
        HR,
        CZ,
        DK,
        EC,
        EG,
        ET,
        FI,
        FR,
        DE,
        GH,
        GI,
        GR,
        HN,
        HU,
        IS,
        ID,
        IR,
        IQ,
        IE,
        IT,
        IL,
        JM,
        JP,
        JO,
        KZ,
        KE,
        KP,
        KR,
        KW,
        LB,
        LR,
        LY,
        LU,
        US,
        GB,
        UA,
        AE,
        UY,
        UZ,
        TR,
        TN,
        TH,
        TW,
        SY,
        SE,
        CH,
        SZ,
        SJ,
        ES,
        ZA,
        SN,
        SA,
        RS,
        QA,
        PT,
        PL,
        PS,
        OM,
        AN

    }

}
