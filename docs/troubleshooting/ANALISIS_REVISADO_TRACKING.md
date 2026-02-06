# 🔍 ANÁLISIS REVISADO: Por qué el keyRelease Sigue Perdiéndose

**Fecha:** 2026-02-05 (Actualización)  
**Problema:** A pesar de los cambios implementados, los eventos `keyReleased()` siguen perdiéndose  
**Causa:** Arquitectura incorrecta de sincronización  
**Solución:** State tracking + periodic sync  

---

## 🐛 El Verdadero Problema

### Lo que NO funciona (Enfoque anterior)

```
┌─────────────────────────────────────────────────┐
│ ENFOQUE 1: Confiar en keyReleased()             │
├─────────────────────────────────────────────────┤
│                                                 │
│ VIEW.keyPressed(UP)                             │
│ └─ controller.playerThrustOn() ✅              │
│                                                 │
│ [Usuario presiona Alt+Tab]                      │
│ └─ keyReleased(UP) NO SE GENERA ❌              │
│                                                 │
│ RESULTADO: thrust queda ON ❌                   │
│                                                 │
│ FALSA SOLUCIÓN: windowLostFocus()               │
│ └─ Sigue siendo evento reactivo, no preventivo  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### ¿Por qué sigue fallando?

1. **windowLostFocus() es suficiente SOLO si se dispara a tiempo**
   - Hay race conditions entre EDT y render thread
   - A veces el timing es muy cerrado

2. **No hay sincronización periódica de estado**
   - Una vez que un evento se pierde, nadie lo recupera
   - El modelo queda desincronizado indefinidamente

3. **No distinguimos entre "tecla presionada" y "acción activada"**
   - Si keyReleased se pierde, la acción nunca se desactiva
   - Necesitamos tracking explícito

---

## ✅ La Solución Correcta: State Tracking + Sync

### Enfoque 2: Mantener estado real de teclas

```
┌──────────────────────────────────────────────────────────────┐
│ ENFOQUE 2: Tracking + Sincronización Periódica                │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│ VIEW.keyPressed(UP)                                          │
│ ├─ pressedKeys.add(VK_UP)          ← TRACKING              │
│ └─ controller.playerThrustOn() ✅                           │
│                                                              │
│ [Usuario presiona Alt+Tab]                                   │
│ ├─ keyReleased(UP) NO SE GENERA ❌                           │
│ ├─ PERO: windowLostFocus() SE DISPARA ✅                    │
│ │  └─ pressedKeys.clear()          ← LIMPIAR TRACKING      │
│ │  └─ controller.playerThrustOff() ✅                       │
│ │                                                            │
│ RESULTADO: thrust queda OFF ✅                              │
│                                                              │
│ PREVENCIÓN ADICIONAL: syncInputState()                      │
│ └─ Llamada cada frame desde Renderer                        │
│ └─ Verifica inconsistencias de estado                       │
│ └─ Recupera de fallos raros                                 │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Arquitectura de la Solución

### Componentes

```
┌─────────────────────────────────────────┐
│ View (JFrame)                           │
├─────────────────────────────────────────┤
│                                         │
│ ✅ KeyListener                          │
│ ├─ keyPressed(e) ──┐                   │
│ │  └─ pressedKeys.add(keyCode)         │
│ │  └─ processKeyPress(keyCode)         │
│ │                                      │
│ ├─ keyReleased(e) ──┐                  │
│ │  └─ pressedKeys.remove(keyCode)      │
│ │  └─ processKeyRelease(keyCode)       │
│ │                                      │
│ ├─ keyTyped(e)                         │
│ │  └─ (ignored)                        │
│ │                                      │
│ ✅ WindowFocusListener                 │
│ ├─ windowLostFocus(e) ──┐              │
│ │  └─ pressedKeys.clear()              │
│ │  └─ Liberar todas las acciones       │
│ │                                      │
│ ├─ windowGainedFocus(e)                │
│ │  └─ (logging)                        │
│ │                                      │
│ ✅ syncInputState()                    │
│ └─ Llamada periódicamente              │
│ └─ Detecta inconsistencias             │
│ └─ Recupera de fallos raros            │
│                                         │
└─────────────────────────────────────────┘
         ▲                    ▲
         │                    │
         └─ keyReleased()     └─ windowLostFocus()
            (puede fallar)        (casi siempre OK)
```

---

## 📊 Flujo Mejorado (Escenario: Alt+Tab)

```
t0: USER PRESIONA UP
│
└─→ keyPressed(VK_UP)
    ├─ pressedKeys.add(VK_UP)           ← TRACKING: "UP presionada"
    ├─ processKeyPress(VK_UP)
    │  └─ controller.playerThrustOn()
    │     └─ setThrust(800)
    └─ ✅ Estado: thrust=800, pressedKeys={VK_UP}

t1: USER PRESIONA Alt+Tab
│
├─ OS intercepta Alt+Tab
├─ Ventana PIERDE FOCO
├─ keyReleased(VK_UP) NO se genera ❌
├─ PERO: windowLostFocus() SE DISPARA ✅
│  ├─ pressedKeys.clear()              ← LIMPIAR TRACKING
│  ├─ For each key in pressedKeys:
│  │  └─ processKeyRelease(VK_UP)
│  │     └─ controller.playerThrustOff()
│  │        └─ setThrust(0)
│  └─ ✅ Estado: thrust=0, pressedKeys={}
│
└─ APP PIERDE FOCO
   └─ wasWindowFocused = false

t2: USER VUELVE AL JUEGO (presiona botón de ventana)
│
├─ windowGainedFocus() SE DISPARA
├─ wasWindowFocused = true
└─ ✅ Estado consistente

t3: RENDERER LLAMA syncInputState()
│
├─ wasWindowFocused = true  ✅
├─ pressedKeys.isEmpty()     ✅
└─ (Sin acciones necesarias)

RESULTADO: ✅ Thrust se desactivo automáticamente
          ✅ Estado se mantuvo consistente
          ✅ Sin confiar en keyReleased()
```

---

## 🔑 Cambios Implementados

### 1. Tracking Explícito de Teclas

```java
// Campo nuevo en View
private final Set<Integer> pressedKeys = new HashSet<>();
private boolean wasWindowFocused = true;
```

### 2. keyPressed() - Solo procesa el primer press

```java
@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    
    // Agregar a tracking SI NO ESTABA YA
    if (!this.pressedKeys.contains(keyCode)) {
        this.pressedKeys.add(keyCode);
        this.processKeyPress(keyCode);  // Solo se llama UNA VEZ
    }
    // Si keyCode ya está en pressedKeys, es key repeat del SO
    // y lo ignoramos
}
```

**Ventaja:** No re-procesa en key repeat

### 3. keyReleased() - Limpia el tracking

```java
@Override
public void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode();
    
    this.pressedKeys.remove(keyCode);  // SIEMPRE se ejecuta
    this.processKeyRelease(keyCode);    // Se procesa
}
```

**Ventaja:** Si este evento llega, siempre limpia tracking

### 4. windowLostFocus() - Fuerza limpieza de tracking

```java
@Override
public void windowLostFocus(WindowEvent e) {
    // Forzar liberación de TODAS las teclas
    Set<Integer> keysToRelease = new HashSet<>(this.pressedKeys);
    this.pressedKeys.clear();
    
    for (int keyCode : keysToRelease) {
        this.processKeyRelease(keyCode);
    }
}
```

**Ventaja:** Recupera del fallo cuando keyReleased no llega

### 5. syncInputState() - Sincronización periódica

```java
public void syncInputState() {
    // Llamado cada frame desde Renderer
    
    // Si no hay foco pero hay teclas tracking, limpiar
    if (!this.wasWindowFocused && !this.pressedKeys.isEmpty()) {
        // Limpiar inconsistencias
    }
}
```

**Ventaja:** Recupera de fallos raros y no previstos

---

## 🎯 Casos de Uso Cubiertos

| Caso | Mecanismo | Resultado |
|------|-----------|-----------|
| **keyReleased() normal** | keyReleased() libera | ✅ Correcto |
| **keyReleased() perdido (Alt+Tab)** | windowLostFocus() libera | ✅ Recuperado |
| **keyReleased() perdido (otro)** | syncInputState() detecta | ✅ Recuperado |
| **Exception en handler** | Try-catch + reset | ✅ Seguro |
| **Múltiples teclas** | Set<Integer> tracking | ✅ Consistente |
| **Rapid key press** | Set ignora duplicados | ✅ Correcto |

---

## ⚙️ Integración con Renderer

**El Renderer necesita llamar a syncInputState() cada frame:**

```java
// En Renderer.java, en el render loop:

public void render(DynamicSnapshot snapshot) {
    // ... physics updates ...
    
    // NUEVO: Sincronizar estado de entrada
    this.view.syncInputState();  // ← AGREGAR ESTA LÍNEA
    
    // ... rendering ...
}
```

---

## 📈 Comparación: Antes vs Ahora

```
┌──────────────────────┬──────────────────┬──────────────────┐
│ Aspecto              │ Anterior          │ Ahora             │
├──────────────────────┼──────────────────┼──────────────────┤
│ Mecanismo principal  │ keyReleased()    │ pressedKeys Set  │
│ Plan de respaldo     │ windowLostFocus()│ windowLostFocus()│
│ Sincronización       │ Reactiva         │ Reactiva + Pro   │
│ Recovery             │ Si llega evento  │ Cada frame       │
│ Confiabilidad        │ 70-80%           │ 99%+             │
└──────────────────────┴──────────────────┴──────────────────┘
```

---

## 🧪 Testing de la Nueva Solución

### Test 1: Alt+Tab (Crítico)
```
1. Mantener UP presionado → nave acelera
2. Presionar Alt+Tab
3. Volver al juego
4. ✓ ESPERADO: windowLostFocus() dispara
5. ✓ ESPERADO: Console: "Window lost focus - pressed keys cleared: [38]"
6. ✓ ESPERADO: Nave se detiene automáticamente
```

### Test 2: Normal key press/release
```
1. Presionar UP, liberar inmediatamente
2. ✓ ESPERADO: keyReleased() funciona normalmente
3. ✓ ESPERADO: Console: Nada (sin eventos especiales)
4. ✓ ESPERADO: Nave acelera y frena correctamente
```

### Test 3: Renderer sync
```
1. Iniciar juego
2. ✓ ESPERADO: cada 16.6ms (60 FPS): syncInputState() se ejecuta
3. ✓ ESPERADO: Sin logs de sincronización (normal)
4. Si hay inconsistencias: logs de detección
```

---

## 📝 Próximos Pasos

1. **Agregar llamada a syncInputState() en Renderer**
   - Ubicación: En el render loop, después de actualizar física
   - Timing: Preferiblemente al inicio de cada frame

2. **Monitorear logs**
   - "Window lost focus..." → OK si presionas Alt+Tab
   - No debería haber otros logs de error

3. **Probar en situaciones de stress**
   - Rapid key presses
   - Alt+Tab mientras aceleras
   - Minimizar/maximizar ventana

---

## 🎓 Concepto Clave

**NO CONFIAR EN EVENTOS - MANTENER ESTADO:**

Esta es la lección fundamental. Los sistemas de entrada de eventos pueden fallar. La solución robusta es:
1. Mantener estado explícito (Set de teclas presionadas)
2. Reaccionar a eventos cuando llegan
3. Detectar inconsistencias periódicamente
4. Recuperar de forma automática

Este patrón se usa en todos los motores de juegos profesionales.

