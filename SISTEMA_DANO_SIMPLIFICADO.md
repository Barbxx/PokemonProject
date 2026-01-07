# Sistema de Daño Simplificado - Resta Directa de HP

## 🎯 Cambio Fundamental

**ANTES** (Sistema Complejo):
```
Daño = (ATQ del Pokémon × 0.4 + Poder del Ataque) × Multiplicador de Tipo
```

**AHORA** (Sistema Directo):
```
Daño = Poder del Ataque
```

## ✅ Nueva Lógica de Combate

### Fórmula Simplificada

El daño se calcula de forma **directa y simple**:

1. **El poder del ataque ES el daño**
2. **No hay cálculos adicionales**
3. **No hay multiplicadores de tipo**
4. **Solo se verifica inmunidad**

### Ejemplo Práctico

#### Ataque: Burbuja (Poder: 14)
```
Piplup usa Burbuja contra Chimchar
Daño = 14
HP de Chimchar: 44 → 30 (-14)
```

#### Ataque: Placaje (Poder: 15)
```
Stantler usa Placaje contra Pichu
Daño = 15
HP de Pichu: 20 → 5 (-15)
```

#### Ataque: Trueno (Poder: 25)
```
Voltorb usa Trueno contra Basculin
Daño = 25
HP de Basculin: 70 → 45 (-25)
```

## 🛡️ Sistema de Inmunidades

Las **inmunidades** siguen funcionando normalmente:

### Ejemplo 1: Gastly (Fantasma)
```
Piplup usa Placaje (Normal, Poder: 15) contra Gastly
Resultado: ¡Gastly es inmune a Normal!
Daño = 0
HP de Gastly: 30 → 30 (sin cambio)
```

### Ejemplo 2: Gible (Tierra/Dragón)
```
Pichu usa Impactrueno (Eléctrico, Poder: 12) contra Gible
Resultado: ¡Gible es inmune a Eléctrico!
Daño = 0
HP de Gible: 58 → 58 (sin cambio)
```

### Ejemplo 3: Togepi (Hada)
```
Goomy usa Dragoaliento (Dragón, Poder: 20) contra Togepi
Resultado: ¡Togepi es inmune a Dragón!
Daño = 0
HP de Togepi: 35 → 35 (sin cambio)
```

## 📊 Tabla de Daños por Ataque

### Ataques Débiles (10-12 de daño)
| Ataque | Poder | Daño Real |
|--------|-------|-----------|
| Latigazo | 10 | 10 HP |
| Picotazo | 10 | 10 HP |
| Destructor | 10 | 10 HP |
| Absorber | 10 | 10 HP |
| Impactrueno | 12 | 12 HP |
| Arañazo | 12 | 12 HP |
| Burbuja | 12 | 12 HP |
| Ácido | 12 | 12 HP |
| Impresionar | 12 | 12 HP |

### Ataques Medios (14-18 de daño)
| Ataque | Poder | Daño Real |
|--------|-------|-----------|
| Placaje | 14-16 | 14-16 HP |
| Confusión | 14-18 | 14-18 HP |
| Falsotortazo | 14 | 14 HP |
| Ascuas | 15-16 | 15-16 HP |
| Polución | 15 | 15 HP |
| Beso Drenaje | 15 | 15 HP |
| Polvo Nieve | 15 | 15 HP |
| At. Rápido | 16 | 16 HP |
| Sombra Vil | 16 | 16 HP |
| Rencor | 18 | 18 HP |
| H. Afilada | 18 | 18 HP |
| Aqua Jet | 18 | 18 HP |
| Mordisco | 18 | 18 HP |
| Pin Misil | 18 | 18 HP |
| Chispa | 18 | 18 HP |

### Ataques Fuertes (20-25 de daño)
| Ataque | Poder | Daño Real |
|--------|-------|-----------|
| Dragoaliento | 20 | 20 HP |
| Aire Afilado | 20 | 20 HP |
| R. Hielo | 20 | 20 HP |
| Rueda Fuego | 22 | 22 HP |
| Trueno | 25 | 25 HP |
| Infortunio | 25 | 25 HP |

### Ataques de Estado (0 de daño)
| Ataque | Efecto |
|--------|--------|
| Hipnosis | Duerme al objetivo |
| Onda Trueno | Paraliza al objetivo |
| Copión | Copia el último movimiento |
| Dulce Aroma | Reduce evasión |
| Refugio | Aumenta Defensa |
| Gruñido | Reduce Ataque |
| Arena Arena | Reduce Precisión |
| Carga | Aumenta Def. Especial |
| Encanto | Reduce Ataque |
| Fortaleza | Aumenta Defensa |
| Mofa | Provoca al objetivo |
| Púas Tóxicas | Envenena al objetivo |
| Gas Venenoso | Envenena al objetivo |

## 🎮 Ejemplos de Combate Completo

### Combate 1: Piplup vs Chimchar

**Turno 1 - Piplup ataca primero (VEL 40 vs VEL 61)**
```
Chimchar usa Ascuas (Poder: 15)
Daño = 15
HP de Piplup: 53 → 38 (-15)
```

**Turno 1 - Piplup ataca**
```
Piplup usa Burbuja (Poder: 14)
Daño = 14
HP de Chimchar: 44 → 30 (-14)
```

**Turno 2 - Chimchar ataca primero**
```
Chimchar usa Arañazo (Poder: 12)
Daño = 12
HP de Piplup: 38 → 26 (-12)
```

**Turno 2 - Piplup ataca**
```
Piplup usa Destructor (Poder: 15)
Daño = 15
HP de Chimchar: 30 → 15 (-15)
```

### Combate 2: Scyther vs Gastly

**Turno 1 - Scyther ataca primero (VEL 105 vs VEL 80)**
```
Scyther usa At. Rápido (Normal, Poder: 16) contra Gastly
Resultado: ¡Gastly es inmune a Normal!
Daño = 0
HP de Gastly: 30 → 30 (sin cambio)
```

**Turno 1 - Gastly ataca**
```
Gastly usa Polución (Poder: 15)
Daño = 15
HP de Scyther: 70 → 55 (-15)
```

**Turno 2 - Scyther ataca primero**
```
Scyther usa Falsotortazo (Poder: 14)
Daño = 14
HP de Gastly: 30 → 16 (-14)
```

## 🔍 Ventajas del Sistema Simplificado

### ✅ Claridad
- **Fácil de entender**: El daño es exactamente el poder del ataque
- **Sin sorpresas**: No hay cálculos ocultos
- **Predecible**: Sabes exactamente cuánto daño harás

### ✅ Balance
- **Equilibrado**: Todos los ataques tienen valores fijos
- **Justo**: No depende de stats del Pokémon
- **Estratégico**: Eliges ataques por su poder directo

### ✅ Simplicidad
- **Sin multiplicadores**: No hay que recordar tabla de tipos
- **Sin fórmulas**: No hay cálculos complejos
- **Solo inmunidades**: Única excepción es la inmunidad total

## 📝 Notas Importantes

### Precisión
Los ataques aún pueden **fallar** según su precisión:
- Trueno: 70% de precisión (puede fallar 30% de las veces)
- Aire Afilado: 95% de precisión (puede fallar 5% de las veces)
- Mayoría de ataques: 100% de precisión (siempre aciertan)

### Inmunidades Completas
Las inmunidades siguen siendo **absolutas**:
- Fantasma inmune a Normal y Lucha
- Tierra inmune a Eléctrico
- Hada inmune a Dragón
- Siniestro inmune a Psíquico
- Volador inmune a Tierra
- Normal inmune a Fantasma

### Daño Mínimo
Si un ataque tiene poder 0 (ataques de estado), el daño será 0.
Si un ataque tiene poder 1+, el daño será al menos 1.

## 🎯 Comparación Antes vs Ahora

### Ejemplo: Scyther (ATQ 110) usa At. Rápido (Poder 16)

**ANTES (Sistema Complejo)**:
```
Daño Base = (110 × 0.4) + 16 = 44 + 16 = 60
Daño Final = 60 × 1.0 (sin multiplicador) = 60 HP
```

**AHORA (Sistema Directo)**:
```
Daño = 16 HP
```

### Ejemplo: Piplup usa Burbuja (Poder 14) contra Chimchar (Fuego)

**ANTES (Sistema Complejo)**:
```
Daño Base = (51 × 0.4) + 14 = 20.4 + 14 = 34
Daño Final = 34 × 2.0 (Agua vs Fuego) = 68 HP
```

**AHORA (Sistema Directo)**:
```
Daño = 14 HP
```

## 🚀 Impacto en el Gameplay

### Combates Más Largos
- Los combates durarán más turnos
- Más oportunidades para estrategia
- Más tiempo para decidir capturar

### Más Predecible
- Sabes exactamente cuántos turnos necesitas
- Puedes calcular si sobrevivirás
- Mejor planificación de recursos

### Más Estratégico
- Elección de movimientos más importante
- Timing de captura más crítico
- Gestión de HP más relevante

---

**Fecha**: 2026-01-07
**Versión**: 2.0 - Sistema Simplificado
**Estado**: ✅ Implementado y Compilado
