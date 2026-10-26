# WIM 100 · Postulación y evaluación (MVP Android)

Aplicación Android en **Kotlin + Jetpack Compose (Material Design 3)** que digitaliza el flujo de
postulación y evaluación del programa **WIM 100 – DYM Cyber**. Proyecto de la asignatura
**DSY1105 Desarrollo de Aplicaciones Móviles** (Duoc UC), Evaluación Parcial 2.

> ⚠️ MVP académico. Todos los datos son **ficticios/sintéticos**. No se integra la API real de LinkedIn
> y la matriz de evaluación es **provisional**, sujeta a validación del Socio Formador.

## Integrantes

| Nombre | Usuario GitHub | Responsabilidades principales |
|---|---|---|
| Javier Mendez | @YuraniX6 | _completar_ |
| wilian Zela | @WilianZela | _completar_ |

Tablero Trello: _pegar aquí el enlace del tablero_

## Funcionalidades

| Requisito del caso | Implementación |
|---|---|
| Registrar / consultar candidaturas | Formulario validado + lista con búsqueda y filtro por estado |
| Revisar antecedentes | CV en PDF (visor integrado), experiencia y enlace ficticio a perfil profesional |
| Matriz de criterios | 5 criterios ponderados (pesos suman 100 %), puntaje 1–5, total 0–100 en vivo |
| Puntajes, observaciones y evaluadora | Observación por criterio (obligatoria si el puntaje es ≤ 2) + observación general; se guarda el nombre de la evaluadora |
| Evitar evaluaciones duplicadas | Verificación en el Repository **y** índice único `(candidataId, evaluadoraClave)` en Room |
| Trazabilidad | Tabla `historial`: inicio/cierre de sesión, registro, evaluación, intento duplicado, resolución, consulta de CV/perfil, compartir |
| Estado y resultados consolidados | Estados Postulada → En evaluación → Evaluada (≥ 2 evaluaciones) → Seleccionada / No seleccionada; ranking por promedio |
| Perfiles | **Evaluadora** (registra y evalúa) y **Dirección** (consulta y resuelve) |

## Requisitos técnicos (rúbrica) → dónde está en el código

| Requisito | Archivos |
|---|---|
| Interfaz M3 y navegación | `ui/theme/*`, `navigation/WimNavHost.kt`, `ui/WimApp.kt` (barra inferior) |
| Formularios con ícono y error visual | `ui/components/CampoValidado.kt`, `ui/screens/CandidataFormScreen.kt`, `EvaluacionScreen.kt` |
| Validación fuera de los composables | `validation/CandidataValidator.kt`, `validation/EvaluacionValidator.kt`, ViewModels |
| Animaciones | Transiciones del NavHost, `sacudir()` en botones con error, selector de puntaje con rebote (spring), `PuntajeCircular` animado, `animateItem()` en listas, tarjetas expandibles (`animateContentSize`), escudo con pulso infinito, barras de ranking que crecen |
| MVVM | `model/` · `data/local/` (Room) · `data/repository/` · `ui/viewmodel/` · `ui/screens/` + `ui/components/` |
| Persistencia Room | `data/local/WimDatabase.kt`, `data/local/dao/*`, `data/local/DatosSemilla.kt` |
| Recursos nativos (4) | `nativo/` → ver tabla siguiente |

### Recursos nativos y permisos

| Recurso | Permiso | Gestión |
|---|---|---|
| **Biometría / credencial del dispositivo** (ingreso) | `USE_BIOMETRIC` (normal) | Se consulta `BiometricManager.canAuthenticate`; si no hay bloqueo configurado, se informa y se permite ingresar |
| **Notificaciones locales** (al registrar una evaluación) | `POST_NOTIFICATIONS` (en tiempo de ejecución, Android 13+) | Diálogo explicativo → diálogo del sistema; si se rechaza, la app sigue funcionando y Perfil ofrece abrir Ajustes |
| **Selector de documentos + PdfRenderer** (CV) | Ninguno (Storage Access Framework) | Solo se accede al PDF elegido; se persiste el permiso de lectura del URI |
| **Intents** (abrir perfil, abrir PDF con otra app, compartir) | Ninguno | Se controla `ActivityNotFoundException` |

La pantalla **Perfil** muestra el estado real de cada permiso y permite probar cada recurso (útil para la defensa).

## Arquitectura

```
com.example.appsemestral
├── model/              Entidades Room, enums y reglas de negocio puras (CalculadoraPuntaje)
├── data/
│   ├── local/          WimDatabase, Converters, DAOs (dao/), DatosSemilla (datos sintéticos)
│   └── repository/     CandidataRepository, EvaluacionRepository (transacción + anti-duplicados), HistorialRepository
├── validation/         Validadores en Kotlin puro (testeables sin emulador)
├── navigation/         Rutas y NavHost con transiciones animadas
├── nativo/             Biometría, notificaciones, archivos/PDF, intents, UI de permisos
├── di/                 AppContainer (inyección de dependencias manual)
└── ui/
    ├── viewmodel/      Estado con StateFlow + AppViewModelProvider (fábrica)
    ├── screens/        Pantallas (Composables)
    ├── components/     Componentes reutilizables (CampoValidado, PuntajeCircular…)
    └── theme/          Material Design 3 (colores, tipografía, tema claro/oscuro)
```

Flujo de datos: **Screen** (observa `StateFlow` con `collectAsStateWithLifecycle`) → llama funciones del
**ViewModel** → el ViewModel valida y llama al **Repository** → el Repository usa los **DAO** de Room →
Room emite un nuevo `Flow` → la UI se recompone sola.

## Cómo ejecutar

1. Android Studio reciente (compatible con AGP 9.2) con JDK 21 (el que trae Android Studio).
2. `File > Open` y elegir la carpeta del proyecto. Esperar la sincronización de Gradle
   (AGP 9.2.1, Kotlin 2.2.10, KSP 2.2.10-2.0.2, Room 2.8.4, Gradle 9.4.1).
3. Ejecutar en un emulador con **API 24+** (recomendado API 34 o superior con Google Play).
4. Para probar la biometría en el emulador: Ajustes → Seguridad → configurar un PIN y una huella
   (en el panel *Extended controls → Fingerprint* se simula el toque).
5. Tests unitarios de validación: `./gradlew test`.

### Datos de prueba precargados

- 6 candidatas sintéticas con CV de prueba en PDF generado por la app.
- Evaluaciones de **“Evaluadora Demo Uno”** y **“Evaluadora Demo Dos”**.
  Ingresa como *Evaluadora Demo Uno* y abre a *Ignacia Valdés Soto* para ver el bloqueo de duplicados.
- Ingresa como **Dirección** para resolver candidaturas con estado *Evaluada*.

## Cambios implementados (práctica para la defensa)

Se implementaron los tres ejercicios de práctica que cubren todas las capas MVVM
(la carpeta `docs/` con el material de práctica fue eliminada tras implementarlos).

### 1. Campo «Ciudad» con validación e ícono de error

| Capa | Archivo | Cambio |
|---|---|---|
| Modelo | `model/Candidata.kt` | Nuevo campo `ciudad: String = ""` |
| Base de datos | `data/local/WimDatabase.kt` | `version` sube de 1 a **2** (la migración destructiva recrea las tablas y se vuelven a cargar los datos semilla) |
| Validación | `validation/CandidataValidator.kt` | `validarCiudad()` (obligatoria, 2–50 caracteres, solo letras, espacios, tildes, `-`, `.` y `'`) + constantes `CIUDAD_MIN` / `CIUDAD_MAX` |
| ViewModel | `ui/viewmodel/CandidataFormViewModel.kt` | `ciudad: CampoState` en el `UiState`, `onCiudad()`, incluida en `validarTodo()`, en `hayErrores` y en el `Candidata(...)` guardado |
| UI | `ui/screens/CandidataFormScreen.kt` | `CampoValidado` con `Icons.Filled.LocationCity` (ícono de error y mensaje en rojo) |
| Extras | `DatosSemilla.kt`, `DetalleCandidataScreen.kt`, `DetalleCandidataViewModel.kt` | Las 6 candidatas sintéticas tienen ciudad; se muestra en el detalle y en el resumen compartido |
| Tests | `CandidataValidatorTest.kt` | 4 tests nuevos (vacía, números/símbolos, muy larga, ciudades chilenas válidas) |

### 2. Nueva función de almacenamiento local: evaluaciones por evaluadora

- `EvaluacionDao`: `observarTotalPorEvaluadora(clave): Flow<Int>` con
  `SELECT COUNT(*) FROM evaluaciones WHERE evaluadoraClave = :clave`.
  Devuelve un `Flow`, así el número se actualiza solo cuando se registra una evaluación.
- `EvaluacionRepository`: `observarTotalPorEvaluadora(evaluadora)` normaliza el nombre con
  `Evaluacion.normalizarClave()` (igual que al guardar).
- `ui/viewmodel/PerfilViewModel.kt` (nuevo): expone `totalEvaluaciones` como `StateFlow<Int>`
  (`flatMapLatest` sobre el nombre de la sesión) y se registra en `AppViewModelProvider`.
- `PerfilScreen`: la tarjeta de perfil muestra **«Evaluaciones registradas: N»** (solo para el rol
  Evaluadora) con `AnimatedContent` al cambiar el número.

### 3. Nuevos composables y animaciones

- **Badge en la barra inferior**: la pestaña *Candidatas* muestra la cantidad de candidatas
  «En evaluación» (`BadgedBox` + `Badge`, número animado con `AnimatedContent`; se oculta si es 0).
  - `CandidataDao.observarCantidadPorEstado(estado): Flow<Int>` →
    `CandidataRepository.observarCantidadPorEstado()` →
    `ui/viewmodel/BarraNavegacionViewModel.kt` (nuevo) → `ui/WimApp.kt`.
- **Aviso con `AnimatedVisibility`** en la matriz de evaluación: al superar **85 puntos** aparece
  (fade + expansión vertical) un banner «¡Excelente perfil!…» bajo el puntaje y desaparece si el
  puntaje baja.
  - Regla de negocio en `CalculadoraPuntaje` (`UMBRAL_AVISO = 85.0`, `superaUmbralAviso()`),
    con test en `CalculadoraPuntajeTest`. La UI está en `EvaluacionScreen.kt` (`ResumenPuntaje`).

### 4. Identidad de la evaluadora insensible a tildes

- `model/Evaluacion.kt`: `normalizarClave()` ahora usa `Normalizer` (NFD) para quitar tildes, además de pasar a minúsculas y colapsar espacios. Así "Ana Pérez" y "Ana Perez" son la misma evaluadora y no se puede burlar el bloqueo de evaluaciones duplicadas.
- `CalculadoraPuntajeTest.kt`: test existente actualizado y 2 tests nuevos.
- Si ya tenías evaluaciones guardadas con nombres con tilde, borra los datos de la app (o desinstálala) para que se vuelvan a generar las claves.
