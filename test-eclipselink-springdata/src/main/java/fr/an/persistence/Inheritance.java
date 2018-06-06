package fr.an.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.InheritanceType;

@Target({TYPE})
@Retention(RUNTIME)
public @interface Inheritance {
	// enum { SINGLE_TABLE, TABLE_PER_CLASS, JOINED } 
    InheritanceType strategy() default SINGLE_TABLE;
}