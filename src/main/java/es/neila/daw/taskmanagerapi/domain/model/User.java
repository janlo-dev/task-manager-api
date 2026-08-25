package es.neila.daw.taskmanagerapi.domain.model;

import java.util.UUID;

public class User {

    private final UUID id;
    private String name;
    private  String email;
    private String password;

    public User(UUID id, String name, String email, String password) {

        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("User email cannot be empty");
        }
        if (password == null || password.isBlank()){
            throw new IllegalArgumentException("User password cannot be empty");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void rename(String newName){
        if(newName == null || newName.isBlank()){
            throw new IllegalArgumentException("User name cannot be empty");
        }
        this.name = newName;
    }

    public void changeEmail(String newEmail){
        if(newEmail == null || newEmail.isBlank()){
            throw new IllegalArgumentException("User emial cannot be empty");
        }
        this.email = newEmail;
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword(){return password;}
}
