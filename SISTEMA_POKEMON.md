# Sistema de Registro de Especies, Daños y Progresión (Nivel 0 al 10)

## Resumen del Sistema

Este documento describe el sistema completo de Pokémon implementado con:
- **20 especies de Pokémon** distribuidas en 5 zonas de dificultad
- **Sistema de progresión** de Nivel 0 a Nivel 10 de investigación
- **Mecánica de captura** (solo cuando el enemigo tiene ≤20% PS)
- **Sistema de inmunidades** por tipo
- **Orden de turno basado en velocidad**
- **Ataques de daño y estado**

## Especies de Pokémon por Zona

### Zona 1 (Nivel de Dificultad 1)
1. **Stantler** - Normal
   - PS: 73 → 82 | ATQ: 95 → 104 | VEL: 85 → 94
   - Ataques iniciales: Placaje (15), Confusión (18)
   - Nivel 5: Desbloquea Hipnosis (Estado)
   - Inmune a: Fantasma

2. **Pichu** - Eléctrico
   - PS: 20 → 25 | ATQ: 40 → 45 | VEL: 60 → 65
   - Ataques iniciales: Impactrueno (12), Latigazo (10)
   - Nivel 5: Desbloquea Onda Trueno (Estado)

3. **Mime Jr.** - Psíquico
   - PS: 20 → 25 | DEF: 45 → 50 | VEL: 60 → 65
   - Ataques iniciales: Confusión (14), Copión (--)
   - Nivel 5: Desbloquea Hipnosis (Estado)

4. **Chimchar** - Fuego
   - PS: 44 → 49 | ATQ: 58 → 63 | VEL: 61 → 66
   - Ataques iniciales: Arañazo (12), Ascuas (15)
   - Nivel 5: Desbloquea Rueda Fuego (22)

5. **Scyther** - Bicho/Volador
   - PS: 70 → 79 | ATQ: 110 → 119 | VEL: 105 → 114
   - Ataques iniciales: At. Rápido (16), Falsotortazo (14)
   - Nivel 5: Desbloquea Aire Afilado (20)
   - Inmune a: Tierra

### Zona 2 (Nivel de Dificultad 2)
6. **Gastly** - Fantasma/Veneno
   - PS: 30 → 35 | AT.E: 100 → 109 | VEL: 80 → 89
   - Ataques iniciales: Impresionar (12), Polución (15)
   - Nivel 5: Desbloquea Infortunio (25)
   - Inmune a: Normal, Lucha

7. **Goomy** - Dragón
   - PS: 45 → 50 | DF.E: 75 → 84 | VEL: 40 → 45
   - Ataques iniciales: Burbuja (12), Ácido (12)
   - Nivel 5: Desbloquea Absorber (10)

8. **Togepi** - Hada
   - PS: 35 → 40 | DEF: 65 → 70 | VEL: 20 → 25
   - Ataques iniciales: Picotazo (10), Beso Drenaje (15)
   - Nivel 5: Desbloquea Dulce Aroma (Estado)
   - Inmune a: Dragón

9. **Turtwig** - Planta
   - PS: 55 → 60 | DEF: 64 → 69 | ATQ: 68 → 73
   - Ataques iniciales: Placaje (14), H. Afilada (18)
   - Nivel 5: Desbloquea Refugio (Defensa+)

### Zona 3 (Nivel de Dificultad 3)
10. **Growlithe H.** - Fuego/Roca
    - PS: 60 → 65 | ATQ: 75 → 84 | VEL: 55 → 60
    - Ataques iniciales: Placaje (15), Ascuas (16)
    - Nivel 5: Desbloquea Mordisco (18)

11. **Qwilfish H.** - Siniestro/Veneno
    - PS: 65 → 70 | ATQ: 95 → 104 | DEF: 85 → 94
    - Ataques iniciales: Picotazo (14), Púas Tóxicas (--)
    - Nivel 5: Desbloquea Pin Misil (18)
    - Inmune a: Psíquico

12. **Piplup** - Agua
    - PS: 53 → 58 | AT.E: 61 → 70 | VEL: 40 → 45
    - Ataques iniciales: Burbuja (14), Destructor (15)
    - Nivel 5: Desbloquea Gruñido (Estado)
    - **Pokémon inicial del jugador**

13. **Basculin** - Agua
    - PS: 70 → 79 | ATQ: 92 → 101 | VEL: 116 → 125
    - Ataques iniciales: Placaje (16), Aqua Jet (18)
    - Nivel 5: Desbloquea Burbuja (14)

### Zona 4 (Nivel de Dificultad 4)
14. **Sneasel H.** - Lucha/Veneno
    - PS: 55 → 60 | ATQ: 95 → 104 | VEL: 115 → 124
    - Ataques iniciales: Arañazo (15), Gas Venenoso (--)
    - Nivel 5: Desbloquea Mofa (Estado)

15. **Gible** - Dragón/Tierra
    - PS: 58 → 63 | ATQ: 70 → 79 | VEL: 42 → 47
    - Ataques iniciales: Placaje (14), Dragoaliento (20)
    - Nivel 5: Desbloquea Arena Arena (Estado)
    - Inmune a: Eléctrico

16. **Voltorb H.** - Eléctrico
    - PS: 40 → 45 | AT.E: 55 → 60 | VEL: 100 → 109
    - Ataques iniciales: Trueno (25), Carga (--)
    - Nivel 5: Desbloquea Chispa (18)

17. **Cleffa** - Hada
    - PS: 50 → 55 | DF.E: 55 → 60 | VEL: 15 → 20
    - Ataques iniciales: Destructor (10), B. Drenaje (14)
    - Nivel 5: Desbloquea Encanto (Estado)
    - Inmune a: Dragón

### Zona 5 (Nivel de Dificultad 5)
18. **Bergmite** - Hielo
    - PS: 55 → 60 | ATQ: 69 → 74 | DEF: 85 → 94
    - Ataques iniciales: Placaje (15), Polvo Nieve (15)
    - Nivel 5: Desbloquea Fortaleza (Defensa+)

19. **Snorunt** - Hielo
    - PS: 50 → 55 | ATQ: 50 → 55 | VEL: 50 → 55
    - Ataques iniciales: Polvo Nieve (15), Impresionar (12)
    - Nivel 5: Desbloquea Mordisco (18)

20. **Zorua H.** - Fantasma
    - PS: 35 → 40 | AT.E: 85 → 94 | VEL: 70 → 79
    - Ataques iniciales: Arañazo (14), Rencor (18)
    - Nivel 5: Desbloquea Sombra Vil (16)
    - Inmune a: Normal, Lucha

## Sistema de Investigación (Pokédex)

### Progresión de Nivel
- **Nivel 0-4**: Estadísticas base
- **Nivel 5-10**: Estadísticas base + bonificación + nuevo ataque desbloqueado
- **Nivel 10**: Investigación completa

### Puntos de Investigación
- **Victoria en combate**: +1 punto
- **Captura exitosa**: +2 puntos
- **Máximo**: 10 puntos por especie

## Mecánica de Captura

### Condiciones para Capturar
1. El Pokémon enemigo debe tener **≤20% de PS**
2. Debe haber **Poké Balls** o **PesoBalls** en el inventario
3. Si no hay Poké Balls, aparece: *"¡No quedan Poké Balls! Necesitas fabricar más."*

### Tipos de Poké Balls
- **Poké Ball**: Ratio de captura 1.0x
- **PesoBall (Heavy Ball)**: Ratio de captura 1.2x (mejor probabilidad)

### Resultado de Captura
- **Éxito**: +2 puntos de investigación, Pokémon añadido al equipo
- **Fallo**: El enemigo escapa y ataca en su turno

## Sistema de Combate

### Orden de Turno
El orden de ataque se determina por la **Velocidad**:
- Si Velocidad del Jugador ≥ Velocidad del Enemigo → Jugador ataca primero
- Si Velocidad del Enemigo > Velocidad del Jugador → Enemigo ataca primero

### Fórmula de Daño
```
Daño Base = (Ataque del Pokémon × 0.4) + Poder del Movimiento
Daño Final = Daño Base × Multiplicador de Tipo
```

### Multiplicadores de Tipo
- **Super Efectivo (2x)**:
  - Agua → Fuego
  - Fuego → Planta, Hielo
  - Planta → Agua
  - Eléctrico → Agua
  - Hielo → Planta, Dragón
  - Lucha → Normal
  - Veneno → Planta
  - Fantasma → Psíquico
  - Siniestro → Fantasma, Psíquico
  - Bicho → Psíquico

- **Poco Efectivo (0.5x)**:
  - Fuego → Agua
  - Agua → Planta
  - Eléctrico → Planta

### Sistema de Inmunidades
Cuando un Pokémon es inmune a un tipo de ataque, aparece el mensaje:
**"¡[Pokémon] es inmune a [Tipo]!"**

#### Inmunidades por Tipo
- **Fantasma** (Gastly, Zorua H.): Inmune a Normal y Lucha
- **Volador** (Scyther): Inmune a Tierra
- **Hada** (Togepi, Cleffa): Inmune a Dragón
- **Siniestro** (Qwilfish H.): Inmune a Psíquico
- **Tierra** (Gible): Inmune a Eléctrico
- **Normal** (Stantler): Inmune a Fantasma

## Interfaz de Usuario

### Menú de Batalla
- **Atacar**: Abre submenú con los movimientos del Pokémon
- **Mochila**: Gestiona items (Poké Balls, Bayas, Pociones)
- **Pokémon**: Ver características y estado del equipo
- **Capturar**: Solo visible cuando el enemigo tiene ≤20% PS
- **Huir**: Escapar del combate

### Indicadores Visuales
- **Barra de PS**: Muestra la salud actual del Pokémon enemigo
- **Texto de Daño**: Aparece en rojo mostrando el daño infligido (ej: "-25")
- **Nivel**: Muestra el nivel de investigación del Pokémon (0-10)

## Pokémon Debilitados

### Condición
Un Pokémon se marca como "Debilitado" cuando sus PS llegan a 0.

### Curación
Los Pokémon debilitados pueden ser curados con:
- **Baya**: Recupera PS
- **Poción**: Recupera PS
- **Revivir Casero**: Revive Pokémon debilitados

## Derrota del Explorador

Si todos los Pokémon del jugador son derrotados:
- Se pierde **1 objeto crafteado aleatorio** del inventario
- El jugador regresa al punto de inicio

## Archivos Modificados/Creados

### Nuevos Archivos
1. **AtaqueData.java**: Base de datos de todos los ataques (daño y estado)

### Archivos Modificados
1. **BasePokemonData.java**: Datos completos de las 20 especies con progresión
2. **Pokemon.java**: Sistema de nivel de investigación y desbloqueo de movimientos
3. **Movimiento.java**: Cálculo de daño mejorado e inmunidades
4. **BattleScreen.java**: Mecánica de captura, orden de turno, inmunidades
5. **Explorador.java**: Métodos getInventario() y agregarAlEquipo()
6. **Inventario.java**: Métodos getCantidad() y consumirItem()

## Pruebas Recomendadas

### Test 1: Pokémon Inicial
- El jugador comienza con **Piplup** (PS: 53)
- Imagen de espalda: `piplup atras.png`
- Verificar que aparece correctamente en batalla

### Test 2: Captura
1. Reducir PS del enemigo a ≤20%
2. Verificar que aparece el botón "Capturar"
3. Intentar captura con Poké Ball
4. Verificar mensaje de éxito/fallo
5. Confirmar +2 puntos de investigación en caso de éxito

### Test 3: Inmunidades
1. Enfrentar a Gastly (Fantasma)
2. Usar ataque Normal (ej: Placaje)
3. Verificar mensaje: "¡Gastly es inmune a Normal!"
4. Confirmar que no recibe daño

### Test 4: Orden de Turno
1. Enfrentar a Basculin (VEL: 116)
2. Usar Piplup (VEL: 40)
3. Verificar que Basculin ataca primero
4. Luego el jugador ataca

### Test 5: Progresión
1. Capturar un Pokémon en Nivel 0
2. Verificar stats base
3. Capturar/derrotar hasta alcanzar Nivel 5
4. Verificar que se desbloquea el nuevo ataque
5. Verificar aumento de stats

## Notas Técnicas

- Todos los Pokémon inician en **Nivel 0** de investigación
- Las estadísticas se calculan dinámicamente según el nivel
- Los movimientos de Nivel 5 solo se agregan si el nivel ≥ 5
- El sistema de inmunidades retorna -1 como código especial
- La velocidad determina el orden de ataque en cada turno
- El daño se muestra visualmente durante 2 segundos

---

**Implementado por**: Antigravity AI
**Fecha**: 2026-01-07
**Versión**: 1.0
