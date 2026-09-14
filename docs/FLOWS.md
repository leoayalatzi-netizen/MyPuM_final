# Flujos
1. Login local → usuario activo.
2. Apertura de turno → fondo inicial.
3. Venta → carrito → cobro → persistencia local → cola de sync.
4. Inventario/precios → actualización local + auditoría.
5. Cierre → conteo → diferencia → cierre de turno.
6. WorkManager sincroniza cuando existe conectividad.
