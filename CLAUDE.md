# CLAUDE.md

Este archivo orienta a Claude Code (claude.ai/code) al trabajar en este repositorio.

## Proyecto

**ToolList** — App Android de listas de compras multipropósito (supermercado, ferretería, proyectos, cualquier compra).
Proyecto académico UPB 2026. Desarrollador: Anthony Arango Betancur.

| Campo | Valor |
|---|---|
| Package / applicationId | `com.toolist.app` |
| Firebase Project ID | `app-movil-toolist` |
| minSdk | 29 · targetSdk / compileSdk | 36 |
| Idioma del código | Kotlin |
| Idioma de la UI | **español únicamente** |
| Strings | todos en `res/values/strings.xml`, cero hardcoding en pantallas |

**Referencia de diseño obligatoria:** `docs/design/toolist-mockups.png` — leer antes de implementar cualquier pantalla.
**Spec completa del proyecto:** `CONTEXT.md` — paleta, tipografía, arquitectura, pantallas, fases.

---

## Commits

Los mensajes de commit van **siempre en español**. Formato: tipo + descripción concisa en imperativo.

```
feat: agregar pantalla de login con validación
fix: corregir error de navegación en detalle de lista
refactor: extraer ProductItem a componente reutilizable
chore: actualizar versión de Hilt a 2.59.2
style: ajustar espaciado de ListCard según mockup
test: agregar pruebas unitarias para GetListsUseCase
```

No usar `git commit --amend` sobre commits ya pusheados. Un commit por cambio lógico completo.

---

## Compilación

```bash
# APK debug
./gradlew assembleDebug

# Tests unitarios
./gradlew testDebugUnitTest

# Un test específico
./gradlew testDebugUnitTest --tests "com.toolist.app.SomeTest"

# Tests instrumentados (requiere dispositivo/emulador)
./gradlew connectedDebugAndroidTest

# Limpiar build
./gradlew clean
```

---

## Problemas conocidos del build

- **`android.disallowKotlinSourceSets=false`** en `gradle.properties` es obligatorio — KSP registra fuentes vía `kotlin.sourceSets` y AGP 9.x lo bloquea por defecto. No eliminar.
- **`kotlin-android` NO se aplica en `app/build.gradle.kts`** — AGP 9.1.1 tiene Kotlin integrado; aplicar `kotlin-android` junto con `kotlin-compose` genera "extension already registered". El módulo app solo lleva `kotlin-compose` + `hilt` + `ksp` + `google-services`.
- **Formato de versión KSP** cambió en la serie 2.2.x: es `2.2.10-2.0.2` (no el antiguo `2.2.x-1.0.x`).
- **Hilt compiler** debe ir como `ksp(libs.hilt.compiler)`, nunca `implementation`.
- **Firebase libraries** (auth, firestore, storage) no llevan versión explícita — la gestiona `platform(libs.firebase.bom)`.

---

## Arquitectura

MVVM + Clean Architecture. Tres capas bajo `com.toolist.app/`:

```
data/
  remote/       # Datasources de Firebase (acceso directo a Auth / Firestore / Storage)
  repository/   # Implementaciones concretas de los repositorios
  model/        # DTOs de Firestore + funciones mapper (DTO ↔ entidad de dominio)
domain/
  model/        # Entidades puras de Kotlin: ShoppingList, Product, Category
  repository/   # Interfaces de repositorio (contrato entre domain y data)
  usecase/      # Un caso de uso por clase, con operator fun invoke()
ui/
  theme/        # Color.kt · Type.kt · Theme.kt · Dimens.kt
  components/   # Composables reutilizables: ProductItem, ListCard, PrimaryButton…
  navigation/   # AppNavigation.kt — sealed class Screen + NavHost AppNavGraph
  screens/      # auth/ · lists/ · product/ · categories/ · search/ · settings/
di/             # Módulos Hilt: FirebaseModule, RepositoryModule…
util/           # Extensiones, constantes, validadores
```

**Flujo de datos:**
```
Screen composable
  → ViewModel  (StateFlow<UiState>, sin LiveData)
    → UseCase  (operator fun invoke)
      → Repository interface  (domain)
        → Repository impl  (data)
          → Firebase datasource
```

**Estructura Firestore:**
```
users/{userId}/lists/{listId}
users/{userId}/lists/{listId}/products/{productId}
users/{userId}/categories/{categoryId}
```

---

## Convenciones de código

**UiState**
```kotlin
data class ListsUiState(
    val isLoading: Boolean = false,
    val lists: List<ShoppingList> = emptyList(),
    val error: String? = null,
)
// Sealed class cuando los estados son mutuamente excluyentes
```

**UseCase**
```kotlin
class GetListsUseCase @Inject constructor(
    private val repo: ListRepository,
) {
    operator fun invoke(userId: String): Flow<List<ShoppingList>> =
        repo.getLists(userId)
}
```

**Repositorios** — interfaz en `domain/repository/`, implementación en `data/repository/`. Nunca dejar que las excepciones de Firebase lleguen crudas al ViewModel; envolverlas en `Result<T>` o `sealed class Resource<T>`.

**Navegación** — siempre usar los helpers `Screen.X.createRoute(id)`, nunca construir strings de ruta manualmente.

**Hilt** — `MainActivity` es `@AndroidEntryPoint`. Cada ViewModel de pantalla usa `hiltViewModel()` vía `androidx-hilt-navigation-compose`.

**Offline** — Firestore se inicializa con `PersistentCacheSettings` en `ToolListApplication`. No reinicializar Firestore en ningún otro lugar.

---

## Sistema de UI

### Archivos de tema

| Archivo | Contenido clave |
|---|---|
| `Color.kt` | Paleta completa + colores de lista + variantes dark mode |
| `Type.kt` | `PoppinsFontFamily` + escala M3 completa |
| `Theme.kt` | `ToolistTheme` — light/dark, status bar, sin dynamic color |
| `Dimens.kt` | Spacing, radii, alturas de componentes (`SpacingMd = 16.dp`, etc.) |

Regla absoluta: **cero valores hardcodeados** en pantallas o componentes — ni colores, ni strings, ni dp.
Siempre usar `MaterialTheme.colorScheme.*`, `stringResource(R.string.*)`, constantes de `Dimens.kt`.

### Paleta de colores ToolList

| Token semántico | Hex | Uso |
|---|---|---|
| `Green500` | `#16A34A` | Primary — botones, barra de progreso, acentos |
| `Green700` | `#166534` | Secondary — headers, texto sobre fondo verde claro |
| `Green900` | `#14532D` | onSecondaryContainer, sombras |
| `Green100` | `#DCFCE7` | PrimaryContainer — fondo chip Comprado, barra progreso fondo |
| `Green50` | `#F0FDF4` | SecondaryContainer — total card en Mis Listas |
| `Gray50` | `#F8FAFC` | Background general |
| `Gray900` | `#111827` | Texto primario |
| `Gray600` | `#4B5563` | Texto secundario / onSurfaceVariant |
| `Gray200` | `#E5E7EB` | Divisores, bordes (outline) |
| `ErrorRed` | `#DC2626` | Error — eliminar, acciones destructivas |
| `WarningOrange` | `#F97316` | Badge Pendiente |
| Dark `primary` | `#4ADE80` (`Green300`) | Verde luminoso sobre fondos oscuros |
| Dark `background` | `#0F1A13` | Fondo oscuro con tinte verde (no negro puro) |

**8 colores identificadores de lista** (en orden del selector del mockup):
`ListGreen #16A34A` · `ListBlue #3B82F6` · `ListPurple #8B5CF6` · `ListPink #EC4899` · `ListRed #EF4444` · `ListOrange #F97316` · `ListYellow #EAB308` · `ListGray #6B7280`

Accesibles como lista ordenada en `Color.kt`: `val ListColors = listOf(...)`.

### Tipografía — Poppins

Fuente única de la app. Se carga vía **Google Fonts Downloadable Fonts** (no archivos `.ttf` locales).
Dependencia: `androidx.compose.ui:ui-text-google-fonts` (versionada por Compose BOM).
Los certificados del proveedor están en `res/values/font_certs.xml` (certificados oficiales de Google Play Services).

| Token M3 | Peso | Tamaño | Uso en mockup |
|---|---|---|---|
| `displayLarge` | Bold 700 | 32sp | Hero en Welcome |
| `headlineLarge` | Bold 700 | 28sp | Títulos de sección |
| `headlineMedium` | SemiBold 600 | 24sp | Totales ($87.300) |
| `titleLarge` | SemiBold 600 | 20sp | Nombres de listas |
| `titleMedium` | SemiBold 600 | 16sp | Nombres de productos |
| `bodyLarge` | Regular 400 | 16sp | Texto general |
| `bodyMedium` | Regular 400 | 14sp | Metadatos, descripciones |
| `labelLarge` | Medium 500 | 14sp | Botones |
| `labelMedium` | Medium 500 | 12sp | Chips, badges |

---

## Firebase

| Servicio | Estado | Notas |
|---|---|---|
| Authentication (email/password) | ✅ activo | |
| Firestore Database | ✅ activo | Persistencia offline activada desde `ToolListApplication` |
| Storage | ⏳ Fase 4 | Requiere plan Blaze; `firebase-storage-ktx` ya está declarado en `libs.versions.toml` |

`FirebaseModule` (`di/`) provee `FirebaseAuth` y `FirebaseFirestore` como `@Singleton` vía Hilt.

---

## Fases del proyecto

| Fase | Estado | Alcance |
|---|---|---|
| **1** | ✅ completa | Gradle, tema, navegación scaffold, Hilt/Firebase, strings, Dimens |
| **2** | — | Welcome, Login, Registro, RecuperarContraseña, AuthViewModel, sesión persistente |
| **3** | — | CRUD listas, MyLists, NewList, ListDetail, bottom sheets, diálogos, skeletons |
| **4** | — | CRUD productos, imágenes con Storage + Coil, bottom sheet de 5 acciones |
| **5** | — | Categorías, Búsqueda global, Configuración, Créditos, accesibilidad, animaciones |
