# 🔧 GUÍA DE INTEGRACIÓN: syncInputState() en Renderer

**Objetivo:** Hacer llamadas periódicas a `view.syncInputState()` desde el Renderer  
**Criticidad:** ALTA - Sin esto, la solución no es completa  
**Tiempo estimado:** 5 minutos

---

## ¿Dónde Llamar syncInputState()?

### Ubicación Ideal: Render Loop

El render loop es la sección del código que se ejecuta **una vez por frame** en el Renderer. Ahí es donde:
1. Se actualizan las coordinadas (física)
2. Se repintan elementos
3. Se sincroniza estado

**AQUÍ es donde debe vivir la llamada a syncInputState():**

```
RENDER LOOP (cada ~16.6ms a 60 FPS)
│
├─ 1. Obtener snapshot del mundo (del Controller)
├─ 2. Actualizar física
├─ 3. ✅ AQUÍ: view.syncInputState()    ← AGREGAR
├─ 4. Renderizar en canvas
└─ 5. Presentar frame
```

---

## 🔍 Pasos para Encontrar el Render Loop

### Paso 1: Localizar la clase Renderer

**Archivo:** `src/engine/view/core/Renderer.java`

### Paso 2: Buscar el método render()

Buscar un método similar a:
```java
public void render(...) {
    // Este es el render loop
}
```

O el método que contiene el loop principal:
```java
@Override
public void run() {
    // Render loop en un JPanel/Canvas
    while (isRunning) {
        // Aquí
    }
}
```

### Paso 3: Identificar dónde actualizar

El lugar correcto es **después de actualizar física pero ANTES de renderizar:**

```java
// PATRÓN:
private void renderFrame() {
    // 1. Obtener datos
    DynamicSnapshot snapshot = this.controller.getDynamicSnapshot(...);
    
    // 2. Procesar física
    // ... cálculos de física ...
    
    // 3. ✅ SINCRONIZAR ENTRADA (AGREGAR AQUÍ)
    this.view.syncInputState();
    
    // 4. Renderizar
    Graphics2D g = (Graphics2D) getGraphics();
    // ... dibujar ...
}
```

---

## 📝 Código Exacto a Agregar

### En Renderer.java

**Localizar:** El método render() o run() o cualquier método que sea el "render loop"

**Agregar esta línea:**

```java
// Después de actualizar física, antes de renderizar
this.view.syncInputState();
```

**Ejemplo completo:**

```java
@Override
public void run() {
    // Render loop
    while (this.isRunning.get()) {
        long frameStart = System.currentTimeMillis();
        
        // Obtener snapshot dinámico
        DynamicSnapshot snapshot = this.getLatestSnapshot();
        
        // Procesar física
        if (snapshot != null) {
            processPhysics(snapshot);
        }
        
        // ✅ NUEVO: Sincronizar estado de entrada
        this.view.syncInputState();
        
        // Renderizar
        repaint();
        
        // Control de FPS
        long elapsed = System.currentTimeMillis() - frameStart;
        long targetFrameTime = 1000 / 60; // 60 FPS
        if (elapsed < targetFrameTime) {
            sleep(targetFrameTime - elapsed);
        }
    }
}
```

---

## 🎯 Checklist de Integración

### Antes de Agregar

- [ ] Localicé `Renderer.java`
- [ ] Identifiqué el render loop
- [ ] Encontré dónde se actualiza física
- [ ] Tengo acceso a `this.view`

### Agregando

- [ ] Agregué `this.view.syncInputState();` después de física
- [ ] La llamada está ANTES de renderizar
- [ ] La llamada está dentro del loop (se ejecuta cada frame)
- [ ] Compilé sin errores

### Después de Agregar

- [ ] Ejecuté el programa (mvn exec:java)
- [ ] Probé Alt+Tab → funciona correctamente
- [ ] No hay logs de error
- [ ] La nave se controla normalmente

---

## 🧪 Verificación

### Cómo Saber que Funciona

1. **Sin actividad especial:**
   ```
   ✓ Consola: Sin logs relacionados a syncInputState()
   ✓ Nave: Se controla normalmente
   ```

2. **Presionando Alt+Tab:**
   ```
   ✓ Consola: "View: Window lost focus - pressed keys cleared: [38]"
   ✓ Nave: Se detiene automáticamente
   ```

3. **FPS normal:**
   ```
   ✓ Consola: Sin ralentizaciones
   ✓ Nave: Movimiento fluido
   ```

---

## ⚠️ Problemas Comunes

### "No encuentro el render loop"

**Buscar por:**
- `public void paint(...)`
- `public void paintComponent(...)`
- `@Override public void run()`
- Cualquier método que contenga un `while` loop

**Tip:** Buscar "repaint()" o "Graphics2D" en Renderer.java

### "No tengo acceso a this.view"

**Solución:**
```java
// Renderer debería tener un campo
private View view;

// Si no lo tiene, agregarlo en el constructor:
public Renderer(View view) {
    this.view = view;
    // ...
}
```

### "Me da error de compilación"

**Causas posibles:**
1. `this.view` es null
2. `syncInputState()` no existe en View
3. Typo en el nombre

**Soluciones:**
1. Verificar que View tiene el método `public void syncInputState()`
2. Verificar que Renderer tiene campo `view` inicializado
3. Verificar la ortografía exacta

---

## 📊 Diagrama de Ejecución

### ANTES (Sin syncInputState)

```
Frame 1 (t=0ms)
├─ keyPressed(UP) → pressedKeys={UP}
├─ Physics update
├─ Render
└─ Next frame

Frame 2 (t=16.6ms)
├─ [Usuario presiona Alt+Tab]
├─ keyReleased(UP) NO LLEGA ❌
├─ Physics update (thrust aún = 800) ❌
├─ Render
└─ Next frame

Frame 3 (t=33.2ms)
├─ windowLostFocus() dispara
├─ pressedKeys limpiado
├─ Physics update (thrust = 0)
├─ Render
└─ Next frame
```

### DESPUÉS (Con syncInputState)

```
Frame 1 (t=0ms)
├─ keyPressed(UP) → pressedKeys={UP}
├─ Physics update → thrust = 800
├─ syncInputState() ✓ (nada que hacer)
├─ Render
└─ Next frame

Frame 2 (t=16.6ms)
├─ [Usuario presiona Alt+Tab]
├─ keyReleased(UP) NO LLEGA ❌
├─ Physics update → thrust aún = 800
├─ syncInputState() ✓ (sin cambios, verifica)
├─ windowLostFocus() dispara
├─ pressedKeys limpiado
├─ Render
└─ Next frame

Frame 3 (t=33.2ms)
├─ Physics update (thrust = 0) ✓
├─ syncInputState() ✓ (verifica consistencia)
├─ Render
└─ Next frame
```

---

## 💡 Punto Clave

**syncInputState() es llamado CADA frame, por lo que:**

✅ Se ejecuta muy frecuentemente (60 veces por segundo)  
✅ Puede detectar inconsistencias rápidamente  
✅ Costo es negligible (microsegundos)  
✅ Recupera de fallos raros automáticamente  

---

## 📌 Resumen

| Tarea | Responsabilidad |
|-------|---|
| **Implementar syncInputState()** | ✅ View.java (ya hecho) |
| **Llamar syncInputState()** | 👉 Renderer.java (AHORA) |
| **Testing** | 👉 Tú (Alt+Tab test) |

---

## 🚀 Próximos Pasos

1. Abre `src/engine/view/core/Renderer.java`
2. Localiza el render loop (método run() o paint())
3. Agrega `this.view.syncInputState();` después de física
4. Compila: `mvn clean compile`
5. Prueba: `mvn exec:java`
6. Verifica con Alt+Tab

**Una vez hecho, el problema estará RESUELTO.**

