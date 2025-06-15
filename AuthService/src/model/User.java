package model;

public class User {
  private String userId;
  private String username;
  private String email;
  private String password;
  private String name;
  private int age;
  private boolean active;
  
  public User(User user) {
    userId = user.getUserId();
    username = user.getUsername();
    email = user.getEmail();
    password = user.getPassword();
    name = user.getName();
    age = user.getAge();
    active = user.isActive();
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
  
  @Override
  public String toString() {
    return "User{" + "id=" + userId + ", email='" + email + '\'' +
           ", username='" + username + '\'' + ", password='" + password + '\'' +
           ", name='" + name + '\'' + ", active=" + active + '}';
  }
}
