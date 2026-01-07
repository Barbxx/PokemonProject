# Guía Rápida de Pruebas - Sistema Pokémon

## ✅ Compilación Exitosa
El proyecto se ha compilado correctamente sin errores.

## 🎮 Cómo Probar el Sistema

### 1. Iniciar el Juego
```bash
.\gradlew.bat lwjgl3:run
```

### 2. Pokémon Inicial
- **Nombre**: Piplup
- **Tipo**: Agua
- **PS Inicial**: 53
- **Ataques**: Burbuja (14), Destructor (15)
- **Imagen**: `piplup atras.png` (vista de espalda del jugador)

### 3. Probar Encuentros Aleatorios
Camina sobre el pasto para encontrar Pokémon salvajes según la zona:

#### Zona 1 (Nivel Dificultad 1) - 15% probabilidad
- Stantler, Pichu, Mime Jr., Chimchar, Scyther

#### Zona 2 (Nivel Dificultad 2) - 25% probabilidad
- Turtwig, Gastly, Goomy, Togepi

#### Zona 3 (Nivel Dificultad 3) - 35% probabilidad
- Growlithe H., Qwilfish H., Piplup, Basculin

#### Zona 4 (Nivel Dificultad 4) - 50% probabilidad
- Sneasel H., Gible, Voltorb H., Cleffa

#### Zona 5 (Nivel Dificultad 5) - 80% probabilidad
- Bergmite, Snorunt, Zorua H.

### 4. Durante la Batalla

#### Controles de Teclado
- **Flechas**: Navegar entre opciones
- **ENTER o Z**: Confirmar selección
- **B o X**: Volver al menú anterior
- **ESC**: Salir del juego

#### Opciones de Batalla
1. **Atacar**: Selecciona un movimiento
2. **Mochila**: Usa items (Bayas, Pociones)
3. **Pokémon**: Ver tu equipo
4. **Capturar**: Solo aparece cuando el enemigo tiene ≤20% PS
5. **Huir**: Escapar del combate

### 5. Probar Captura

#### Pasos:
1. Reducir PS del enemigo a 20% o menos
2. El botón "Capturar" se volverá visible
3. Presionar para intentar captura
4. Si tienes Poké Balls:
   - **Éxito**: "¡[Pokémon] fue capturado! +2 Puntos de Investigación"
   - **Fallo**: "¡Oh no! [Pokémon] escapó de la Poké Ball."
5. Si no tienes Poké Balls:
   - "¡No quedan Poké Balls! Necesitas fabricar más."

### 6. Probar Inmunidades

#### Ejemplo 1: Gastly (Fantasma)
- Usar ataque Normal (Placaje, Destructor)
- Mensaje esperado: "¡Gastly es inmune a Normal!"
- Daño: 0

#### Ejemplo 2: Gible (Tierra/Dragón)
- Usar ataque Eléctrico (Impactrueno, Trueno)
- Mensaje esperado: "¡Gible es inmune a Eléctrico!"
- Daño: 0

### 7. Probar Orden de Turno

#### Test con Pokémon Rápido (Basculin - VEL 116)
1. Tu Piplup tiene VEL 40
2. Basculin atacará primero
3. Luego atacas tú

#### Test con Pokémon Lento (Togepi - VEL 20)
1. Tu Piplup tiene VEL 40
2. Atacas primero
3. Luego ataca Togepi

### 8. Verificar Progresión

#### Ver Pokédex
1. Abrir menú de Pokémon
2. Ver nivel de investigación (0-10)
3. Cada victoria: +1 punto
4. Cada captura: +2 puntos

#### Desbloqueo de Movimientos (Nivel 5)
1. Captura/derrota un Pokémon 5 veces (10 puntos)
2. El siguiente encuentro con esa especie tendrá:
   - Stats mejoradas
   - Nuevo movimiento desbloqueado

**Ejemplo con Pichu:**
- Nivel 0-4: Impactrueno, Latigazo
- Nivel 5+: Impactrueno, Latigazo, **Onda Trueno** (nuevo)

### 9. Verificar Daño

#### Fórmula de Daño SIMPLIFICADA (NUEVA)
```
Daño = Poder del Ataque
```

**¡El daño es DIRECTO!** No hay cálculos complejos, multiplicadores de tipo, ni stats involucrados.

#### Ejemplos:
- Piplup usa Burbuja (Poder 14) → **Daño = 14 HP**
- Scyther usa At. Rápido (Poder 16) → **Daño = 16 HP**
- Voltorb usa Trueno (Poder 25) → **Daño = 25 HP**
- Chimchar usa Ascuas (Poder 15) → **Daño = 15 HP**

#### Inmunidades (Daño = 0)
- Gastly (Fantasma) vs Placaje (Normal) → **Daño = 0** (inmune)
- Gible (Tierra) vs Impactrueno (Eléctrico) → **Daño = 0** (inmune)
- Togepi (Hada) vs Dragoaliento (Dragón) → **Daño = 0** (inmune)

#### Texto Visual
- Aparece el daño exacto en rojo sobre el enemigo
- Ejemplo: "-14", "-16", "-25"
- Dura 2 segundos

### 10. Probar Derrota

#### Si tu Pokémon es derrotado:
1. Mensaje: "¡Tu Pokémon se debilitó!"
2. Pierdes 1 objeto crafteado aleatorio
3. Regresas al punto de inicio

## 📊 Estadísticas de Ejemplo

### Piplup (Inicial del Jugador)
```
Nivel 0:
- PS: 53
- ATQ: 51
- AT.E: 61
- VEL: 40
- Ataques: Burbuja, Destructor

Nivel 5+:
- PS: 58 (+5)
- ATQ: 51
- AT.E: 70 (+9)
- VEL: 45 (+5)
- Ataques: Burbuja, Destructor, Gruñido
```

### Scyther (Pokémon Fuerte)
```
Nivel 0:
- PS: 70
- ATQ: 110
- VEL: 105
- Ataques: At. Rápido, Falsotortazo

Nivel 5+:
- PS: 79 (+9)
- ATQ: 119 (+9)
- VEL: 114 (+9)
- Ataques: At. Rápido, Falsotortazo, Aire Afilado
```

## 🐛 Problemas Conocidos

### Si no aparece el botón Capturar:
- Verifica que el enemigo tenga ≤20% PS
- La barra de PS debe estar casi vacía (verde → roja)

### Si dice "No quedan Poké Balls":
- Ir a la pantalla de Crafteo
- Fabricar Poké Balls (2 Plantas + 3 Guijarros)
- O fabricar Heavy Balls (mejor ratio de captura)

### Si el ataque no hace daño:
- Verifica inmunidades en SISTEMA_POKEMON.md
- Ejemplo: Gastly es inmune a Normal y Lucha

## 📁 Archivos Importantes

- **SISTEMA_POKEMON.md**: Documentación completa del sistema
- **AtaqueData.java**: Base de datos de ataques
- **BasePokemonData.java**: Datos de las 20 especies
- **BattleScreen.java**: Lógica de combate y captura

## ✨ Características Implementadas

✅ 20 especies de Pokémon con datos completos
✅ Sistema de progresión Nivel 0-10
✅ Mecánica de captura (≤20% PS)
✅ Sistema de inmunidades por tipo
✅ Orden de turno basado en velocidad
✅ Ataques de daño y estado
✅ Desbloqueo de movimientos en Nivel 5
✅ Visualización de daño en pantalla
✅ Registro de investigación en Pokédex
✅ Múltiples zonas con diferentes probabilidades

---

**¡Listo para probar!** 🎮
