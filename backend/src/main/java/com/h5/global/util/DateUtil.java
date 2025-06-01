package com.h5.global.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate, DEFAULT_FORMATTER);
        return Period.between(birth, LocalDate.now()).getYears();
    }

    public static int calculateAge(LocalDate birthDate, LocalDate date) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
