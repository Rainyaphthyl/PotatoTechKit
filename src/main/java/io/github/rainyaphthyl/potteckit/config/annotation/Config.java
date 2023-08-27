package io.github.rainyaphthyl.potteckit.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    Type[] types() default {};

    Domain[] domains();

    boolean serverSide() default false;

    /**
     * Destructive to some vanilla features, causing players to forget the vanilla operations and refuse to redstone with them; Or solving some vanilla problems that could have been solved with vanilla technologies.
     */
    boolean notVanilla() default false;

    /**
     * Making some manual operations so convenient that players refuse to design automatic machines for them.
     */
    boolean cheating() default false;
}
