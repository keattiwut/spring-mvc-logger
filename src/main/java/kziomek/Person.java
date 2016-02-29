package kziomek;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Krzysztof Ziomek
 * @since 28/01/2016.
 */
public class Person {

    @NotEmpty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
