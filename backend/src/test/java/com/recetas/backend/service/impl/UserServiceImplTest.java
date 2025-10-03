package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Seguidor;
import com.recetas.backend.domain.entity.SeguidorId;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.RolRepository;
import com.recetas.backend.domain.repository.SeguidorRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Rol;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.exception.EmailAlreadyInUseException;
import com.recetas.backend.exception.SeguimientoException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SeguidorRepository seguidorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private NotificacionService notificacionService; // Mock para NotificacionService

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
        when(seguidorRepository.existsById_SeguidorIdAndId_SeguidoId(usuarioSeguidor.getId().longValue(),
                usuarioSeguido.getId().longValue()))
                .thenReturn(false);
        // Mockear el comportamiento de crearNotificacion
        when(notificacionService.crearNotificacion(any(Integer.class), any(TipoNotificacion.class), any(Integer.class),
                any()))
                .thenReturn(null); // O devolver un objeto Notificacion mockeado si es necesario

        assertDoesNotThrow(() -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));

        verify(seguidorRepository).save(any(Seguidor.class));
        verify(notificacionService, times(1)).crearNotificacion(any(Integer.class), eq(TipoNotificacion.NUEVO_SEGUIDOR),
                any(Integer.class), any());
    }

    @Test
    void seguirUsuario_seguidorNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.empty());

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguidor no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void seguirUsuario_seguidoNotFound() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.empty());

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguido no encontrado con id: 2", exception.getMessage());
    }

    @Test
    void seguirUsuario_selfFollow() {
        SeguimientoException exception = assertThrows(SeguimientoException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguidor.getId()));
        assertEquals("Un usuario no puede seguirse a sí mismo.", exception.getMessage());
    }

    @Test
    void seguirUsuario_alreadyFollowing() {
        when(usuarioRepository.findById(usuarioSeguidor.getId())).thenReturn(Optional.of(usuarioSeguidor));
        when(usuarioRepository.findById(usuarioSeguido.getId())).thenReturn(Optional.of(usuarioSeguido));
        when(seguidorRepository.existsById_SeguidorIdAndId_SeguidoId(usuarioSeguidor.getId().longValue(),
                usuarioSeguido.getId().longValue()))
                .thenReturn(true);

        SeguimientoException exception = assertThrows(SeguimientoException.class,
                () -> userService.seguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Ya sigues a este usuario.", exception.getMessage());
    }

    @Test
    void dejarDeSeguirUsuario_success() {
        when(usuarioRepository.existsById(usuarioSeguidor.getId())).thenReturn(true);
        when(usuarioRepository.existsById(usuarioSeguido.getId())).thenReturn(true);
        when(seguidorRepository.existsById(seguidorId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));

        verify(seguidorRepository).deleteById(seguidorId);
    }

    @Test
    void dejarDeSeguirUsuario_seguidorNotFound() {
        when(usuarioRepository.existsById(usuarioSeguidor.getId())).thenReturn(false);

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguidor no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void dejarDeSeguirUsuario_seguidoNotFound() {
        when(usuarioRepository.existsById(usuarioSeguidor.getId())).thenReturn(true);
        when(usuarioRepository.existsById(usuarioSeguido.getId())).thenReturn(false);

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("Usuario seguido no encontrado con id: 2", exception.getMessage());
    }

    @Test
    void dejarDeSeguirUsuario_notFollowing() {
        when(usuarioRepository.existsById(usuarioSeguidor.getId())).thenReturn(true);
        when(usuarioRepository.existsById(usuarioSeguido.getId())).thenReturn(true);
        when(seguidorRepository.existsById(seguidorId)).thenReturn(false);

        SeguimientoException exception = assertThrows(SeguimientoException.class,
                () -> userService.dejarDeSeguirUsuario(usuarioSeguidor.getId(), usuarioSeguido.getId()));
        assertEquals("No sigues a este usuario.", exception.getMessage());
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

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.obtenerSeguidores(usuarioSeguido.getId()));
        assertEquals("Usuario no encontrado con id: 2", exception.getMessage());
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

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> userService.obtenerSeguidos(usuarioSeguidor.getId()));
        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
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

    @Test
    void registrarUsuario_emailAlreadyInUse() {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNombreUsuario("testuser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setContrasena("password");

        when(usuarioRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.of(new Usuario()));

        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class,
                () -> userService.registrarUsuario(signupRequestDto));
        assertEquals("El correo electrónico ya está en uso.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_success() {
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNombreUsuario("newUser");
        signupRequestDto.setEmail("new@example.com");
        signupRequestDto.setContrasena("password");

        Rol userRole = new Rol();
        userRole.setNombre("USER");

        when(usuarioRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupRequestDto.getContrasena())).thenReturn("encodedPassword");
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.of(userRole));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario user = invocation.getArgument(0);
            user.setId(1); // Simulate saving and getting an ID
            return user;
        });

        Usuario result = userService.registrarUsuario(signupRequestDto);

        assertNotNull(result);
        assertEquals("newUser", result.getNombreUsuario());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getContrasena());
        assertNotNull(result.getRol());
        assertEquals("USER", result.getRol().getNombre());

        verify(usuarioRepository, times(1)).findByEmail(signupRequestDto.getEmail());
        verify(passwordEncoder, times(1)).encode(signupRequestDto.getContrasena());
        verify(rolRepository, times(1)).findByNombre("USER");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}
