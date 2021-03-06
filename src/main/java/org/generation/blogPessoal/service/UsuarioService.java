package org.generation.blogPessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Usuario cadastrarUsuario(Usuario usuario) {
		Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());
		if (usuarioExistente.isPresent()) {
			return null;
		} else {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			String senhaEconder = encoder.encode(usuario.getSenha());
			usuario.setSenha(senhaEconder);

			return usuarioRepository.save(usuario);
		}
	}

	public Optional<UserLogin> Logar(Optional<UserLogin> user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(user.get().getUsuario());

		if (usuario.isPresent()) {
			if (encoder.matches(user.get().getSenha(), usuario.get().getSenha())) {
				String auth = user.get().getUsuario() + ":" + user.get().getSenha();

				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes((Charset.forName("US-ASCII"))));
				String authHeader = "Basic " + new String(encodedAuth);

				user.get().setToken(authHeader);
				user.get().setNome(usuario.get().getNome());

				return user;
			}
		}
		return null;
	}
}