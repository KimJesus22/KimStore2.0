import { persistentAtom } from '@nanostores/persistent';

// 1. Definimos exactamente que forma tiene un Producto de tu Backend
export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  imageUrl?: string;
}

// 2. Un Item del Carrito es igual a un Producto, pero agregando la "cantidad"
export interface CartItem extends Producto {
  cantidad: number;
}

// 3. Creamos el Atomo Persistente, diciendole estrictamente que es un arreglo de CartItem
export const cart = persistentAtom<CartItem[]>('kimstore_cart', [], {
  encode: JSON.stringify,
  decode: JSON.parse,
});

// 4. Funciones fuertemente tipadas
export function agregarAlCarrito(producto: Producto): void {
  const carritoActual = cart.get();
  const indexExistente = carritoActual.findIndex((item) => item.id === producto.id);

  if (indexExistente >= 0) {
    const nuevoCarrito = [...carritoActual];
    nuevoCarrito[indexExistente].cantidad += 1;
    cart.set(nuevoCarrito);
  } else {
    cart.set([...carritoActual, { ...producto, cantidad: 1 }]);
  }
}

export function obtenerCantidadTotal(): number {
  const carritoActual = cart.get();
  return carritoActual.reduce((total, item) => total + item.cantidad, 0);
}

export function eliminarDelCarrito(productoId: number): void {
  const carritoActual = cart.get();
  cart.set(carritoActual.filter((item) => item.id !== productoId));
}

export function limpiarCarrito(): void {
  cart.set([]);
}
