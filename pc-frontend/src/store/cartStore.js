import { persistentAtom } from '@nanostores/persistent';

// 1. Creamos el "Atomo" persistente.
// Guarda la lista como JSON en el navegador bajo el nombre 'kimstore_cart'
export const cart = persistentAtom('kimstore_cart', [], {
  encode: JSON.stringify,
  decode: JSON.parse,
});

// 2. Funcion para anadir un producto al carrito
export function agregarAlCarrito(producto) {
  const carritoActual = cart.get();
  const indexExistente = carritoActual.findIndex(item => item.id === producto.id);

  if (indexExistente >= 0) {
    carritoActual[indexExistente].cantidad += 1;
    cart.set([...carritoActual]);
  } else {
    cart.set([...carritoActual, { ...producto, cantidad: 1 }]);
  }
}

// 3. Funcion para calcular cuantos articulos hay en total
export function obtenerCantidadTotal() {
  const carritoActual = cart.get();
  return carritoActual.reduce((total, item) => total + item.cantidad, 0);
}

// 4. Funcion para eliminar un producto solo del carrito
export function eliminarDelCarrito(productoId) {
  const carritoActual = cart.get();
  cart.set(carritoActual.filter(item => item.id !== productoId));
}

// 5. Funcion para vaciar el carrito
export function limpiarCarrito() {
  cart.set([]);
}
