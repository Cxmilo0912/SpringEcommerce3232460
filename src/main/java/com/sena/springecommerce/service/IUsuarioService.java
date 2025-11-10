package com.sena.springecommerce.service;

import java.util.List;
import java.util.Optional;

import com.sena.springecommerce.model.Usuario;

public interface IUsuarioService {
	//Metodos CRUD

	public Usuario save(Usuario usuario);  //C=CREATE

	public Optional<Usuario> get(Integer id);  //R=READ

	public void update(Usuario usuario);  //U=UPDATE

	public void delete(Integer id);  //D=DELETE

	Optional<Usuario> findById(Integer id);

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findAll();

}
