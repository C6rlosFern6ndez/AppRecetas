package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Seguidor;
import com.recetas.backend.domain.entity.SeguidorId;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.SeguidorRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SeguidorRepository seguidorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Usuario usuarioSeguidor;
    private Usuario usuarioSeguido;
    private SeguidorId seguidorId;
    private Seguidor seguidor;

    @BeforeEach
    void setUp() {
        usuarioSeguidor = new Usuario();
        usuarioSeguidor.setId(1);
        usuarioSeguidor.setNombreUsuario("seguidorUser");
        usuarioSeguidor.setEmail("seguidor@example.com");
        usuarioSeguidor.setContrasena("password");

        usuarioSeguido = new Usuario();
        usuarioSeguido.setId(2);
        usuarioSeguido.setNombreUsuario("seguidoUser");
        usuarioSeguido.setEmail("seguido@example.com");
        usuarioSeguido.setContrasena("password");

        seguidorId = new SeguidorId(usuarioSeguidor.getId(), usuarioSeguido.getId());
        seguidor = new Seguidor(seguidorId, usuarioSeguidor, usuarioSeguido);
    }

    @Test
    void seguirUsuario_success() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));
        when(seguidorRepository.existsById(seguidorId)).thenReturn(false);
        when(seguidorRepository.save(any(Seguidor.class))).thenReturn(seguidor);

        assertDoesNotThrow(() -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));

        verify(usuarioRepository, times(2)).findById(anyInt());
        verify(seguidorRepository, times(1)).existsById(seguidorId);
        verify(seguidorRepository, times(1)).save(any(Seguidor.class));
    }

    @Test
    void seguirUsuario_seguidorNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguidor no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void seguirUsuario_seguidoNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguido no encontrado", exception.getMessage());

        verify(usuarioRepository, times(2)).findById(anyInt());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void seguirUsuario_selfFollow() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguidor.getId()));
        assertEquals("Un usuario no puede seguirse a sí mismo.", exception.getMessage());

        verify(usuarioRepository, times(2)).findById(usuarioSeguidor.getId());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void seguirUsuario_alreadyFollowing() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));
        when(seguidorRepository.existsById(seguidorId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Ya sigues a este usuario.", exception.getMessage());

        verify(usuarioRepository, times(2)).findById(anyInt());
        verify(seguidorRepository, times(1)).existsById(seguidorId);
        verifyNoMoreInteractions(seguidorRepository);
    }

    @Test
    void dejarDeSeguirUsuario_success() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));
        when(seguidorRepository.existsById(seguidorId)).thenReturn(true);
        doNothing().when(seguidorRepository).deleteById(seguidorId);

        assertDoesNotThrow(() -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));

        verify(usuarioRepository, times(2)).findById(anyInt());
        verify(seguidorRepository, times(1)).existsById(seguidorId);
        verify(seguidorRepository, times(1)).deleteById(seguidorId);
    }

    @Test
    void dejarDeSeguirUsuario_seguidorNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguidor no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void dejarDeSeguirUsuario_seguidoNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguido no encontrado", exception.getMessage());

        verify(usuarioRepository, times(2)).findById(anyInt());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void dejarDeSeguirUsuario_notFollowing() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));
        when(seguidorRepository.existsById(seguidorId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("No sigues a este usuario.", exception.getMessage());

        verify(usuarioRepository, times(2)).findById(anyInt());
        verify(seguidorRepository, times(1)).existsById(seguidorId);
        verifyNoMoreInteractions(seguidorRepository);
    }

    @Test
    void obtenerSeguidores_success() {
        Set<Seguidor> seguidoresSet = new HashSet<>();
        seguidoresSet.add(seguidor);
        usuarioSeguido.setSeguidores(seguidoresSet);

        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));

        Set<Usuario> seguidores = userService.obtenerSeguidores(usuarioSeguido.getId());

        assertEquals(1, seguidores.size());
        assertTrue(seguidores.contains(usuarioSeguidor));
        verify(usuarioRepository, times(1)).findById(usuarioSeguido.getId());
    }

    @Test
    void obtenerSeguidores_userNotFound() {
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.obtenerSeguidores(usuarioSeguido.getId()));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioSeguido.getId());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void obtenerSeguidores_noFollowers() {
        usuarioSeguido.setSeguidores(new HashSet<>()); // Empty set
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));

        Set<Usuario> seguidores = userService.obtenerSeguidores(usuarioSeguido.getId());

        assertTrue(seguidores.isEmpty());
        verify(usuarioRepository, times(1)).findById(usuarioSeguido.getId());
    }

    @Test
    void obtenerSeguidos_success() {
        Set<Seguidor> seguidosSet = new HashSet<>();
        seguidosSet.add(seguidor);
        usuarioSeguidor.setSeguidos(seguidosSet);

        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));

        Set<Usuario> seguidos = userService.obtenerSeguidos(usuarioSeguidor.getId());

        assertEquals(1, seguidos.size());
        assertTrue(seguidos.contains(usuarioSeguido));
        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
    }

    @Test
    void obtenerSeguidos_userNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.obtenerSeguidos(usuarioSeguidor.getId()));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void obtenerSeguidos_noFollowedUsers() {
        usuarioSeguidor.setSeguidos(new HashSet<>()); // Empty set
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));

        Set<Usuario> seguidos = userService.obtenerSeguidos(usuarioSeguidor.getId());

        assertTrue(seguidos.isEmpty());
        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
    }

    @Test
    void findById_success() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        Usuario foundUser = userService.findById(usuarioSeguidor.getId());
        assertNotNull(foundUser);
        assertEquals(usuarioSeguidor.getId(), foundUser.getId());
        verify(usuarioRepository, times(1)).findById(usuarioSeguidor.getId());
    }

    @Test
    void findById_notFound() {
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        Usuario foundUser = userService.findById(99);
        assertNull(foundUser);
        verify(usuarioRepository, times(1)).findById(anyInt());
    }

    @Test
    void saveUser_success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSeguidor);

        Usuario savedUser = userService.saveUser(usuarioSeguidor);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getContrasena());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void findByEmail_success() {
        when(usuarioRepository.findByEmail(usuarioSeguidor.getEmail())).thenReturn(Optional.of(usuarioSeguidor));

        Optional<Usuario> foundUser = userService.findByEmail(usuarioSeguidor.getEmail());

        assertTrue(foundUser.isPresent());
        assertEquals(usuarioSeguidor.getId(), foundUser.get().getId());
        verify(usuarioRepository, times(1)).findByEmail(usuarioSeguidor.getEmail());
    }

    @Test
    void findByEmail_notFound() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<Usuario> foundUser = userService.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
}
