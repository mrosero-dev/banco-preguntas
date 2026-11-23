package co.edu.unicauca.lisw2t5g02.model;

/** User returned by the Identity Service. */
public record User(long id, String fullName, String email, Role role) {

    @Override
    public String toString() {
        return fullName + " (" + email + ")";
    }
}
