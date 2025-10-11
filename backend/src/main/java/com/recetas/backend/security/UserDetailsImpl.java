package com.recetas.backend.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recetas.backend.models.Usuario;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nombreUsuario;
    private String email;

    @JsonIgnore
    private String contrasena;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String nombreUsuario, String email, String contrasena,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre()));

        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getContrasena(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
