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
	/**
	 * true if this subform is required for a create operation.
	 * @return
	 */
	boolean isRequired() default false;
	String[] properties();
	int columns() default 1;
}
