package uk.ltd.mediamagic.flow.crud;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SubForms.class)
public @interface SubForm {
	String title();
	String[] properties();
	int columns() default 1;
}
