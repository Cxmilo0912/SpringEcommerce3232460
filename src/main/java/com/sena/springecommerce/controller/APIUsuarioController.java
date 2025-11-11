package com.sena.springecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiusuarios")
public class APIUsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	// GET for all users
	@GetMapping("/list")
	public List<Usuario> getAllUsuarios() {
		return usuarioService.findAll();
	}

	// GET for id
	@GetMapping("/user/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// POST for create user
	@PostMapping("/createuser")
	public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
		usuario.setRol("User");
		Usuario saveUsuario = usuarioService.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveUsuario);
	}

	// PUT for update user
	@PutMapping("/update/{id}")
	public ResponseEntity<Usuario> updateUser(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Usuario existingUser = usuario.get();
		existingUser.setDireccion(usuarioDetails.getDireccion());
		existingUser.setEmail(usuarioDetails.getEmail());
		existingUser.setNombre(usuarioDetails.getNombre());
		existingUser.setPassword(usuarioDetails.getPassword());
		existingUser.setTelefono(usuarioDetails.getTelefono());

		usuarioService.update(existingUser);
		return ResponseEntity.ok(existingUser);
	}

	// DELETE for user
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		usuarioService.delete(id);
		return ResponseEntity.ok().build();
	}

}
