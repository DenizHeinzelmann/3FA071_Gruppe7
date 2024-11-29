package interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)  // Make the annotation available at runtime for reflection
@Target(ElementType.METHOD)         // Can only be applied to methods
public @interface Route {
    String path();           // The URI path
    String [] method() default {"GET"};  // The HTTP method, default is GET
}