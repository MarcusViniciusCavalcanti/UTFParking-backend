package br.edu.utfpr.tsi.utfparking.domain.users.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Car.class)
public class Car_ {
    public static volatile SingularAttribute<Car, Long> id;
    public static volatile SingularAttribute<Car, String> plate;
    public static volatile SingularAttribute<Car, String> model;
    public static volatile SingularAttribute<Car, User> user;
    public static volatile SingularAttribute<Car, LocalDateTime> lastAccess;
    public static volatile SingularAttribute<Car, LocalDate> createdAt;
    public static volatile SingularAttribute<Car, LocalDate> updatedAt;

}
