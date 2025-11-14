package com.sena.springecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sena.springecommerce.model.DetalleOrden;
import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IDetalleOrdenService;
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiordenes")
public class APIOrdenController {

	@Autowired
	private IDetalleOrdenService detalleService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IProductoService productoService;

	List<DetalleOrden> detalleTemp = new ArrayList<>();

	Orden ordenTemp = new Orden();

	// GET para obtener todas los Ordenes
	@GetMapping("/list")
	public List<Orden> getAllOrdenes() {
		return ordenService.findAll();
	}

	// GET para obtener un Orden por ID
	@GetMapping("/orden/{id}")
	public ResponseEntity<Orden> getOrdenById(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// GET para mostrar el detalle orden temporal
	@GetMapping("/temporden")
	public ResponseEntity<List<DetalleOrden>> verOrdenTemp() {
		return ResponseEntity.ok(detalleTemp);
	}

	// POST para agregar productos temporales
	@PostMapping("/agregar")

	/*
	 * { "producto": { "id": 78 }, "cantidad": 1 }
	 */

	public ResponseEntity<?> addProduct(@RequestBody DetalleOrden detalle) {

		if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
			return ResponseEntity.badRequest().body("Producto inválido");
		}
		if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
			return ResponseEntity.badRequest().body("Cantidad inválida");
		}

		var producto = productoService.get(detalle.getProducto().getId())
				.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

		// verificar si ya esta existiendo
		boolean existe = false;
		for (DetalleOrden d : detalleTemp) {
			if (d.getProducto().getId().equals(producto.getId())) {
				d.setCantidad(d.getCantidad() + detalle.getCantidad()); // Si ya existe, solo aumenta la cantidad
				d.setTotal(d.getCantidad() * d.getPrecio()); // multiplica la cantidad por el precio
				existe = true;
				break;
			}
			if (d.getCantidad() + detalle.getCantidad() > producto.getCantidad()) {
				return ResponseEntity.badRequest().body("Stock insuficiente del producto: " + producto.getNombre());
			}

		}

		if (!existe) {
			detalle.setProducto(producto);
			detalle.setNombre(producto.getNombre());
			detalle.setPrecio(producto.getPrecio());
			detalle.setTotal(producto.getPrecio() * detalle.getCantidad());
			detalleTemp.add(detalle);
		}

		// Actualizar el total
		double total = detalleTemp.stream().mapToDouble(DetalleOrden::getTotal).sum();
		ordenTemp.setTotal(total);
		return ResponseEntity.ok(detalleTemp);
	}

	// POST para crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<?> createOrden(@RequestParam Integer usuarioid) {
		Optional<Usuario> usuario = usuarioService.findById(usuarioid);

		if (usuario.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con id " + usuarioid + " no encontrado");
		}

		Usuario usuario1 = usuario.get();

		Orden orden = ordenTemp;

		// Buscar el usuario

		orden.setUsuario(usuario1);

		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setFechacreacion(new Date());

		// Calcular total
		double totalOrden = detalleTemp.stream().mapToDouble(DetalleOrden::getTotal).sum();
		orden.setTotal(totalOrden);

		// Guardar la Orden
		Orden savedOrden = ordenService.save(orden);

		// Guardar cada detalle y asociarlo a la orden
		for (DetalleOrden d : detalleTemp) {
			d.setOrden(savedOrden);
			detalleService.save(d);

			// Descontar stock de Producto
			Producto producto = d.getProducto();
			producto.setCantidad((int) (producto.getCantidad() - d.getCantidad()));
			productoService.update(producto);
		}

		// Asociar los detalles
		savedOrden.setDetalle(new ArrayList<>(detalleTemp));

		// Limpiar listas Temporales
		detalleTemp.clear();
		ordenTemp = new Orden();

		return ResponseEntity.status(HttpStatus.CREATED).body(savedOrden);
	}

}
