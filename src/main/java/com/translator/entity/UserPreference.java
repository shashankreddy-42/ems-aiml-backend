package com.translator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences", uniqueConstraints = @UniqueConstraint(name = "uk_pref_user", columnNames = "user_id"))
public class UserPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pref_user"))
    private User user;

    @Column(name = "default_source_language", length = 10)
    private String defaultSourceLanguage = "en";

    @Column(name = "default_target_language", length = 10)
    private String defaultTargetLanguage = "te";

    @Column(length = 10)
    private String theme = "light";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getDefaultSourceLanguage() { return defaultSourceLanguage; }
    public void setDefaultSourceLanguage(String defaultSourceLanguage) { this.defaultSourceLanguage = defaultSourceLanguage; }
    public String getDefaultTargetLanguage() { return defaultTargetLanguage; }
    public void setDefaultTargetLanguage(String defaultTargetLanguage) { this.defaultTargetLanguage = defaultTargetLanguage; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
