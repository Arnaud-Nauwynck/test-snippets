package fr.an.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.EAGER;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
@SuppressWarnings("rawtypes")

@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface ManyToOne {
	Class targetEntity() default void.class;
    CascadeType[] cascade() default {};
    FetchType fetch() default EAGER;
    boolean optional() default true;
}
