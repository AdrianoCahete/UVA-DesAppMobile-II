package dev.adrianocahete.mediacalc;

public class User {
    private int id;
    private String nome;
    private String matricula;
    private String avatarUrl;
    private String apiKey;
    private String createdAt;
    private String changedBy; // 'api' or 'user'

    public User() {
    }

    public User(String nome, String matricula, String apiKey) {
        this.nome = nome;
        this.matricula = matricula;
        this.apiKey = apiKey;
        this.changedBy = "user";
    }

    public User(String nome, String matricula, String avatarUrl, String apiKey, String changedBy) {
        this.nome = nome;
        this.matricula = matricula;
        this.avatarUrl = avatarUrl;
        this.apiKey = apiKey;
        this.changedBy = changedBy;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getMatricula() {
        return matricula;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getChangedBy() {
        return changedBy;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public boolean isDataFromApi() {
        return "api".equals(changedBy);
    }
    public boolean canUserEdit() {
        return !"api".equals(changedBy);
    }
}
