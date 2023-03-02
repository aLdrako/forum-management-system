package com.company.web.forummanagementsystem.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.company.web.forummanagementsystem.helpers.DateTimeFormat.formatToString;

//@JsonPropertyOrder({"id", "firstName", "lastName", "email", "username", "password", "joiningDate", "phoneNumber", "admin", "blocked", "deleted"})
@Entity
@Table(name = "users")
@SecondaryTables({
        @SecondaryTable(name = "photos", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id")),
        @SecondaryTable(name = "phones", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Lob
    @Column(name = "photo", table = "photos")
    private byte[] photo;
    @Column(name = "phone_number", table = "phones")
    private String phoneNumber;
    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "user_id")
    private Permission permission;
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(GenerationTime.ALWAYS)
    @Column(name = "join_date")
    private LocalDateTime joiningDate;

    public User() {}

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password, String phoneNumber, Permission permission, LocalDateTime joiningDate) {
        this(id, firstName, lastName, email, username, password, phoneNumber);
        this.permission = permission;
        this.joiningDate = joiningDate;
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password, LocalDateTime joiningDate, String phoneNumber) {
        this(id, firstName, lastName, email, username, password, phoneNumber);
        this.joiningDate = joiningDate;
    }

    public User(Long id, String firstName, String lastName, String email, String username, String password, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<byte[]> getPhoto() {
        return Optional.ofNullable(photo);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Optional<String> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJoiningDate() {
        return formatToString(joiningDate);
    }

    public void setJoiningDate(LocalDateTime joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
