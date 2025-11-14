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

import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiproductos")
public class APIProductoController {

	@Autowired
	private IProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	// Endpoint GET para obtener todos los productos
	@GetMapping("/list")
	public List<Producto> getAllProducts() {
		return productoService.findAll();
	}

	// Endpoint GET para obtener un producto por ID
	@GetMapping("/product/{id}")
	public ResponseEntity<Producto> getProductById(@PathVariable Integer id) {
		Optional<Producto> producto = productoService.get(id);
		return producto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Endpoint POST para crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<Producto> createProduct(@RequestBody Producto producto) {
		Usuario u = usuarioService.findById(1).get();
		producto.setUsuario(u);
		if (producto.getImagen() == null) {
			producto.setImagen("default.jpg");
		}
		Producto savedProduct = productoService.save(producto);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
	}

	// Endpoint PUT para actualizar un producto
	@PutMapping("/update/{id}")
	public ResponseEntity<Producto> updateProduct(@PathVariable Integer id, @RequestBody Producto productDetails) {
		Optional<Producto> producto = productoService.get(id);
		if (!producto.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Producto existingProduct = producto.get();
		existingProduct.setNombre(productDetails.getNombre());
		existingProduct.setDescripcion(productDetails.getDescripcion());
		existingProduct.setPrecio(productDetails.getPrecio());
		existingProduct.setCantidad(productDetails.getCantidad());
		// Mantener la imagen existente a menos que se envie una nueva
		if (productDetails.getImagen() != null) {
			existingProduct.setImagen(productDetails.getImagen());
		}
		productoService.update(existingProduct);
		return ResponseEntity.ok(existingProduct);
	}

	// Endpoint DELETE para eliminar un producto
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
		Optional<Producto> producto = productoService.get(id);
		if (!producto.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Producto p = producto.get();
		if (!p.getImagen().equals("default.jpg")) {
			// Incluir microservicio de imagenes
		}
		productoService.delete(id);
		return ResponseEntity.ok().build();
	}

}
