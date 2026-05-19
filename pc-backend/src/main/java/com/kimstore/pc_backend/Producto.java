package com.kimstore.pc_backend; // Asegúrate de que esto coincida con tu paquete

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity // Esto le dice a Spring Boot: "¡Convierte esta clase en una tabla de MariaDB!"
public class Producto {

    @Id // Esto le dice que este atributo es la Llave Primaria (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esto hace que el ID sea Autoincrementable (1, 2, 3...)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private Double precio;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser un número negativo")
    private Integer stock;

    // Constructores (Concepto clave de POO)
    public Producto() {
    }

    public Producto(String nombre, String descripcion, Double precio, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters (Encapsulamiento en POO)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
