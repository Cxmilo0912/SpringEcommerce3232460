package com.sena.springecommerce.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IOrdenRepository;

@Service
public class OrdenServiceImplement implements IOrdenService {

	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
		// TODO Auto-generated method stub
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		// TODO Auto-generated method stub
		return ordenRepository.findAll();
	}

	@Override
	public List<Orden> findByUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		// TODO Auto-generated method stub
		return ordenRepository.findById(id);
	}

	@Override
	public String generarNumeroOrden() {
		List<Orden> ordenes = ordenRepository.findAll();

		// Filtra las órdenes que tengan número no nulo y no vacío
		List<Integer> numeros = ordenes.stream().map(Orden::getNumero).filter(Objects::nonNull)
				.filter(s -> !s.trim().isEmpty()).map(s -> {
					try {
						return Integer.parseInt(s);
					} catch (NumberFormatException e) {
						return null; // ignora valores inválidos
					}
				}).filter(Objects::nonNull).collect(Collectors.toList());

		int numero;
		String numeroConcatenado;

		if (numeros.isEmpty()) {
			numeroConcatenado = "00000001";
		} else {
			numero = numeros.stream().max(Integer::compareTo).orElse(0);
			numero++;
			numeroConcatenado = String.format("%08d", numero);
		}

		return numeroConcatenado;
	}

}
