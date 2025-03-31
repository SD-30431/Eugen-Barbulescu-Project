package com.example.sd_backend2.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private boolean isAdmin;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    @OneToMany(mappedBy = "author")
    private List<Review> reviews;

    @OneToMany(mappedBy = "author")
    private List<AuthActivity> authActivities;

    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "subscriberId"),
            inverseJoinColumns = @JoinColumn(name = "subscribedToId")
    )
    private Set<Author> following;

    @ManyToMany(mappedBy = "following")
    private Set<Author> followers;

    public Author() {
    }

    public Author(String name, String password, boolean isAdmin) {
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<AuthActivity> getAuthActivities() {
        return authActivities;
    }

    public void setAuthActivities(List<AuthActivity> authActivities) {
        this.authActivities = authActivities;
    }

    public Set<Author> getFollowing() {
        return following;
    }

    public void setFollowing(Set<Author> following) {
        this.following = following;
    }

    public Set<Author> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Author> followers) {
        this.followers = followers;
    }
}
