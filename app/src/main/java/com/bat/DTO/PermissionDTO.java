package com.bat.DTO;

public class PermissionDTO {
    private int perm_id;
    private int role_id;
    private String resource;
    private byte perm_value;
    private byte status;

    public PermissionDTO() {
    }

    public PermissionDTO(int perm_id, int role_id, String resource, byte perm_value, byte status) {
        this.perm_id = perm_id;
        this.role_id = role_id;
        this.resource = resource;
        this.perm_value = perm_value;
        this.status = status;
    }

    public int getPerm_id() {
        return perm_id;
    }

    public void setPerm_id(int perm_id) {
        this.perm_id = perm_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public byte getPerm_value() {
        return perm_value;
    }

    public void setPerm_value(byte perm_value) {
        this.perm_value = perm_value;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PermissionDTO{" +
               "perm_id=" + perm_id +
               ", role_id=" + role_id +
               ", resource='" + resource + '\'' +
               ", perm_value=" + perm_value +
               ", status=" + status +
               '}';
    }
}
