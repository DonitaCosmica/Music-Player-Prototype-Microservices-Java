package model;

import enums.RoleName;

public class Role {
  private String RoleId;
  private RoleName role;

  public String getRoleId() {
    return RoleId;
  }

  public void setRoleId(String RoleId) {
    this.RoleId = RoleId;
  }

  public RoleName getRole() {
    return role;
  }

  public void setRole(RoleName role) {
    this.role = role;
  }
  
  
}
