# Guía Rápida: Cómo Agregar Nuevos Ítems

## Introducción
Gracias al nuevo sistema OO, agregar ítems es ahora un proceso simple y estructurado.

## Paso 1: Decidir el Tipo de Ítem

Pregúntate: **¿Qué hace este ítem?**

- **Cura HP** → Extiende `ItemCurativo`
- **Revive Pokémon** → Crea nueva clase que extiende `ItemConsumible`, implementa lógica personalizada
- **Mejora estadísticas** → Similar a `ElixirPielPiedra`, extiende `ItemConsumible`
- **Se lanza (Pokéball)** → Extiende `ObjetoCrafteado`, implementa `ILanzable`
- **Utilidad especial** → Extiende `ItemConsumible`, define lógica propia

## Paso 2: Crear la Clase

### Ejemplo 1: Ítem de Curación Simple

```java
package com.mypokemon.game.items.consumibles;

import java.util.Arrays;
import java.util.List;

/**
 * SuperPoción - Cura 50 HP.
 */
public class SuperPocion extends ItemCurativo {

    public SuperPocion(int cantidad) {
        super("superpocion", "Súper Poción", "Cura 50 HP.", cantidad, 50);
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
```

### Ejemplo 2: Ítem con Lógica Personalizada

```java
package com.mypokemon.game.items.consumibles;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.Inventario;
import com.mypokemon.game.items.enums.TipoUso;
import com.mypokemon.game.items.resultados.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Antídoto - Cura envenenamiento.
 */
public class Antidoto extends ItemConsumible {

    public Antidoto(int cantidad) {
        super("antidoto", "Antídoto", "Cura el envenenamiento.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        // Verificar si el Pokemon está envenenado
        return pokemon.tieneEstado("envenenado") && !pokemon.isDebilitado();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon)) {
            return ResultadoUso.fallo("No tiene efecto.");
        }

        pokemon.curarEstado("envenenado");
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡" + pokemon.getNombre() + " se curó del envenenamiento!");
    }

    @Override
    public TipoUso getTipoUso() {
        return TipoUso.CURAR;
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Usar", "Tirar");
    }
}
```

## Paso 3: Registrar en ItemFactory

Agrega el caso en `crearCrafteado()`:

```java
public static ObjetoCrafteado crearCrafteado(String id, int cantidad) {
    switch (id.toLowerCase()) {
        // ... casos existentes ...
        case "superpocion":
            return new SuperPocion(cantidad);
        case "antidoto":
            return new Antidoto(cantidad);
        default:
            return new ObjetoCrafteadoGenerico(id, id, "Descripción no disponible.", cantidad);
    }
}
```

## Paso 4: Inicializar en Inventario

Agrega a la lista de crafteados en `Inventario.java`:

```java
public Inventario(int capacidad) {
    // ... código existente ...
    
    // Agregar nuevo ítem
    listObjCrafteados.add(ItemFactory.crearCrafteado("superpocion", 0));
    listObjCrafteados.add(ItemFactory.crearCrafteado("antidoto", 0));
}
```

## Paso 5: Agregar Textura y UI (Opcional)

### En CrafteoScreen (si es crafteable):
```java
// Agregar receta
recetas.add(new Receta("Súper Poción", 
    Arrays.asList("5 Plantas Medicinales"), 
    () -> { 
        // Lógica de crafteo
    }
));
```

### En MochilaScreen (para mostrar):
```java
// Cargar textura
private Texture texSuperPocion;

// En constructor:
try {
    texSuperPocion = new Texture(Gdx.files.internal("superpocion.png"));
} catch (Exception e) {
    Gdx.app.error("Mochila", "Missing superpocion.png");
}

// En updateVisibleItems():
Item superpocion = inventory.getItem("superpocion");
if (superpocion != null && superpocion.getCantidad() > 0)
    visibleItems.add(new ItemData("Súper Poción", "Cura 50 HP.", 
            texSuperPocion, superpocion.getCantidad(), superpocion));
```

## Checklist Final

- [ ] Clase del ítem creada y compila correctamente
- [ ] Registrado en `ItemFactory.crearCrafteado()`
- [ ] Agregado a la inicialización del `Inventario`
- [ ] Textura agregada a assets (si aplica)
- [ ] Actualizado `MochilaScreen` para mostrar (si aplica)
- [ ] Agregada receta en `CrafteoScreen` (si aplica)
- [ ] Probado en el juego

## Beneficios de Este Sistema

✅ **Sencillo**: Solo necesitas crear 1 clase nueva
✅ **Encapsulado**: Toda la lógica del ítem está en su clase
✅ **Automático**: `MochilaScreen` usará automáticamente `getOpciones()` y `usar()`
✅ **Mantenible**: Cambios en un ítem no afectan otros

## Tipos de Ítems Pre-definidos

### ItemCurativo
- **Cuándo usar**: Ítems que curan HP
- **Heredan**: `puedeUsar()`, `usar()`, `getTipoUso()`
- **Solo defines**: constructor y `getOpciones()`

### ItemConsumible
- **Cuándo usar**: Cualquier ítem consumible con lógica personalizada
- **Heredan**: Implementación base de `IUsable`
- **Debes definir**: `puedeUsar()`, `usar()`, `getTipoUso()`, `getOpciones()`

### ObjetoCrafteado + ILanzable
- **Cuándo usar**: Ítems lanzables como Pokéballs
- **Implementan**: `puedeLanzar()`, `getTipoLanzable()`

---

**¡Ahora estás listo para agregar cualquier ítem que imagines!** 🎮
