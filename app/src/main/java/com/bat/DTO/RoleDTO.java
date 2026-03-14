package com.bat.DTO;

public class RoleDTO {
    private int role_id;
    private String name;
    private byte status;

    public RoleDTO() {
    }

    public RoleDTO(int role_id, String name, byte status) {
        this.role_id = role_id;
        this.name = name;
        this.status = status;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
               "role_id=" + role_id +
               ", name='" + name + '\'' +
               ", status=" + status +
               '}';
    }
}
