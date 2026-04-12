# ERRORS.md - Errores conocidos y soluciones

Este documento registra errores encontrados durante el desarrollo y pruebas de la aplicación IPF Gold.

---

## Error: `java.lang.IllegalStateException` en pantalla "Enviar comentarios"

**Fecha:** 12 de abril de 2026  
**Versión de la app:** 1.5+  
**Contexto:** Pantalla "Enviar comentarios" (feedback screen).

### Descripción del error

```
java.lang.IllegalStateException: Vertically scrollable component was measured with infinite height constraints.
```

Este error ocurre típicamente en Jetpack Compose cuando se anidan componentes desplazables verticales (como `LazyColumn`, `Column` con `verticalScroll`) dentro de otros contenedores que imponen restricciones de altura infinita.

### Captura de pantalla

![Error IllegalStateException en pantalla de comentarios](docs/error-screenshots/feedback-infinite-height-error.jpg)

### Causa raíz

En Compose, cuando un componente desplazable vertical (por ejemplo, un `LazyColumn`) se coloca dentro de un contenedor que no limita su altura (como un `Column` padre sin `Modifier.fillMaxHeight()` o un `Box` sin restricciones), el sistema de medición puede recibir restricciones de altura infinitas, lo que provoca este `IllegalStateException`.

El patrón problemático suele ser:

```kotlin
Column(
    modifier = Modifier.verticalScroll(rememberScrollState()) // ← scroll anidado
) {
    LazyColumn { // ← componente desplazable interno
        items(...) { ... }
    }
}
```

### Solución

1. **Evitar anidar componentes desplazables.**  
   Si necesitas múltiples secciones desplazables, usa un único `LazyColumn` con diferentes tipos de ítems (`item`, `items`).

2. **Asegurar restricciones de altura finitas.**  
   Aplica `Modifier.fillMaxHeight()` al contenedor padre cuando sea apropiado, o usa `Modifier.heightIn(max = ...)` para limitar la altura.

3. **Patrón correcto para pantalla de comentarios:**

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
) {
    // Contenido no desplazable en la parte superior
    Text("Enviar comentarios", style = MaterialTheme.typography.headlineMedium)
    
    // Área de texto con altura fija o limitada
    OutlinedTextField(
        value = feedbackText,
        onValueChange = { feedbackText = it },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 300.dp), // ← límites explícitos
        label = { Text("Tu comentario") }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Botones en la parte inferior
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(onClick = { /* cancelar */ }) {
            Text("Cancelar")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* enviar */ }) {
            Text("Enviar")
        }
    }
}
```

### Estado

- **Corregido en versión:** Pendiente de fix.
- **Prioridad:** Media (afecta a la pantalla de feedback, no al flujo principal).
- **Asignado a:** Desarrollador de UI.

### Referencias

- [Documentación oficial de Jetpack Compose: Restricciones de altura](https://developer.android.com/jetpack/compose/layout#height-constraints)
- [Issue similar en Google IssueTracker](https://issuetracker.google.com/issues/270165538)

---

*Este archivo se actualiza automáticamente cuando se detectan nuevos errores.*