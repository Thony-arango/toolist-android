# Proyecto ToolList — Contexto y Decisiones Finales

## Descripción
App Android de listas de compras multipropósito (supermercado, ferretería,
proyectos, cualquier compra). Proyecto académico para la UPB, año 2026.
Desarrollador: Anthony Arango Betancur.

El mockup completo de la app está en `docs/design/toolist-mockups.png`.
Revisar antes de implementar cualquier pantalla para mantener fidelidad visual.

---

## Paquete e idioma
- **Paquete**: `com.toolist.app` (verificado en `app/build.gradle.kts`)
- **applicationId**: `com.toolist.app`
- **Idioma**: español únicamente
- **Strings**: todos en `res/values/strings.xml` desde el día 1. Cero hardcoding en pantallas.

---

## Versiones actuales del proyecto (verificadas en libs.versions.toml)
| Componente | Versión |
|---|---|
| AGP | 9.1.1 |
| Kotlin | 2.2.10 |
| Compose BOM | 2024.09.00 |
| compileSdk | 36 (release, minorApiLevel = 1) |
| minSdk | 29 |
| targetSdk | 36 |
| Java | 17 |
| core-ktx | 1.10.1 |
| lifecycle-runtime-ktx | 2.6.1 |
| activity-compose | 1.8.0 |

**Dependencias aún no agregadas** (se agregan en Fase 1):
Hilt, Firebase BOM, Navigation Compose, Coroutines, Coil, Google Fonts, KSP plugin, kotlin-android plugin.

---

## Stack técnico
- Kotlin + Jetpack Compose + Material 3
- Arquitectura: MVVM + Clean Architecture (`data / domain / ui`)
- Firebase Auth (email/password) + Cloud Firestore + Firebase Storage
- Hilt para inyección de dependencias
- Navigation Compose (type-safe con rutas selladas o Kotlin Serialization)
- Kotlin Coroutines + Flow
- Coil para carga de imágenes
- Poppins vía Google Fonts Downloadable Fonts (`androidx.compose.ui:ui-text-google-fonts`)

---

## Tipografía — Poppins

**Dependencia**: `androidx.compose.ui:ui-text-google-fonts`
**Método**: Google Fonts Downloadable Fonts (no archivos .ttf locales)

### Pesos a declarar
| Peso | Nombre |
|---|---|
| 400 | Regular |
| 500 | Medium |
| 600 | SemiBold |
| 700 | Bold |

### Mapeo Material 3 en Type.kt
| Token M3 | Peso | Tamaño | Uso |
|---|---|---|---|
| displayLarge | Bold 700 | 32sp | Hero Welcome |
| headlineLarge | Bold 700 | 28sp | Títulos de sección grandes |
| headlineMedium | SemiBold 600 | 24sp | Totales destacados ($87.300) |
| titleLarge | SemiBold 600 | 20sp | Nombres de listas |
| titleMedium | SemiBold 600 | 16sp | Nombres de productos |
| bodyLarge | Regular 400 | 16sp | Texto general |
| bodyMedium | Regular 400 | 14sp | Metadatos, descripciones |
| labelLarge | Medium 500 | 14sp | Botones |
| labelMedium | Medium 500 | 12sp | Chips y badges |

---

## Paleta de colores (extraída del mockup)

### Colores base
| Nombre semántico | Hex | Uso |
|---|---|---|
| Green500 | `#16A34A` | Verde principal: botones primarios, barra progreso, acentos |
| Green700 | `#166534` | Verde oscuro: headers de detalle, texto sobre fondo verde claro |
| Green900 | `#14532D` | Verde muy oscuro: sombras, detalles de header en modo claro |
| Green50 | `#F0FDF4` | Fondo muy claro con tinte verde (total card en Mis Listas) |
| Green100 | `#DCFCE7` | Verde salvia: botón "Iniciar sesión" en Welcome, fondo chip Comprado, barra progreso fondo |
| Gray50 | `#F8FAFC` | Fondo general de la app |
| White | `#FFFFFF` | Cards, superficies |
| Gray900 | `#111827` | Texto primario |
| Gray600 | `#4B5563` | Texto secundario |
| Gray400 | `#9CA3AF` | Placeholders, iconos inactivos nav |
| Gray200 | `#E5E7EB` | Divisores, bordes sutiles |
| Error | `#DC2626` | Acciones destructivas: Eliminar lista, Eliminar producto |
| ErrorLight | `#FEE2E2` | Fondo chip error |
| Warning | `#F97316` | Badge / estado "Pendiente" |
| WarningLight | `#FFEDD5` | Fondo badge Pendiente |

### Colores identificadores de lista (8 opciones, pantalla Nueva Lista)
| Índice | Nombre | Hex |
|---|---|---|
| 1 | ListGreen | `#16A34A` |
| 2 | ListBlue | `#3B82F6` |
| 3 | ListPurple | `#8B5CF6` |
| 4 | ListPink | `#EC4899` |
| 5 | ListRed | `#EF4444` |
| 6 | ListOrange | `#F97316` |
| 7 | ListYellow | `#EAB308` |
| 8 | ListGray | `#6B7280` |

### Modo claro — lightColorScheme
| Token M3 | Valor |
|---|---|
| primary | `#16A34A` |
| onPrimary | `#FFFFFF` |
| primaryContainer | `#DCFCE7` |
| onPrimaryContainer | `#166534` |
| secondary | `#166534` |
| onSecondary | `#FFFFFF` |
| secondaryContainer | `#F0FDF4` |
| onSecondaryContainer | `#14532D` |
| background | `#F8FAFC` |
| onBackground | `#111827` |
| surface | `#FFFFFF` |
| onSurface | `#111827` |
| surfaceVariant | `#F1F5F9` |
| onSurfaceVariant | `#4B5563` |
| outline | `#E5E7EB` |
| error | `#DC2626` |
| onError | `#FFFFFF` |
| errorContainer | `#FEE2E2` |
| onErrorContainer | `#991B1B` |

### Modo oscuro — darkColorScheme
| Token M3 | Valor |
|---|---|
| primary | `#4ADE80` |
| onPrimary | `#14532D` |
| primaryContainer | `#166534` |
| onPrimaryContainer | `#DCFCE7` |
| secondary | `#86EFAC` |
| onSecondary | `#14532D` |
| secondaryContainer | `#166534` |
| onSecondaryContainer | `#DCFCE7` |
| background | `#0F1A13` |
| onBackground | `#E2E8F0` |
| surface | `#1A2E1F` |
| onSurface | `#E2E8F0` |
| surfaceVariant | `#253326` |
| onSurfaceVariant | `#9CA3AF` |
| outline | `#374151` |
| error | `#F87171` |
| onError | `#7F1D1D` |
| errorContainer | `#991B1B` |
| onErrorContainer | `#FEE2E2` |

---

## Modo oscuro
Ambos temas desde la Fase 1. La identidad verde se mantiene en modo oscuro usando
verdes más luminosos (`#4ADE80`) sobre fondos oscuros con tinte verde (no negro puro).

---

## Firebase
- **Auth**: email/password
- **Firestore**: persistencia offline activada desde el inicio (`FirebaseFirestoreSettings` con caché ilimitada o `memoryCacheSettings`/`persistentCacheSettings` según SDK moderno)
- **Storage**: para imágenes de productos
- **`google-services.json`**: lo agrega el desarrollador manualmente; el proyecto queda listo para recibirlo con el plugin `com.google.gms.google-services` declarado

### Estructura Firestore
```
users/{userId}
users/{userId}/lists/{listId}
users/{userId}/lists/{listId}/products/{productId}
users/{userId}/categories/{categoryId}
```

---

## Arquitectura

MVVM + Clean Architecture en tres capas:

```
app/src/main/java/com/toolist/app/
├── data/
│   ├── remote/          # Firebase datasources
│   ├── repository/      # Implementaciones de repositorios
│   └── model/           # DTOs y mappers
├── domain/
│   ├── model/           # Entidades de dominio
│   ├── repository/      # Interfaces de repositorios
│   └── usecase/         # Casos de uso (invoke operator)
├── ui/
│   ├── theme/           # Color.kt, Type.kt, Theme.kt, Dimens.kt
│   ├── components/      # Componentes reutilizables
│   ├── navigation/      # NavGraph, rutas selladas
│   └── screens/
│       ├── auth/        # Welcome, Login, Registro, RecuperarContrasena
│       ├── lists/       # MisListas, NuevaLista, DetalleLista
│       ├── product/     # AgregarProducto, DetalleProducto, EditarProducto
│       ├── categories/  # Categorias
│       ├── search/      # Busqueda
│       └── settings/    # Configuracion, Creditos
├── di/                  # Módulos Hilt
└── util/                # Extensiones, constantes, validadores
```

---

## Pantallas y estados — Alcance completo

### Pantallas del mockup (16)
1. Welcome
2. Registro
3. Login
4. Mis Listas (con resumen total estimado)
5. Nueva Lista
6. Detalle de Lista — estado vacío
7. Detalle de Lista — estado en progreso
8. Detalle de Lista — estado completado (100%)
9. Bottom sheet Opciones de Lista (Renombrar, Duplicar, Mover a carpeta, Eliminar)
10. Bottom sheet Opciones de Producto (Marcar comprado, Editar, Duplicar, Mover a lista, Eliminar)
11. Agregar Producto
12. Detalle de Producto
13. Categorías
14. Búsqueda global
15. Configuración
16. Créditos

### Estados vacíos
- Mis Listas sin listas (CTA "Crear primera lista")
- Búsqueda sin resultados
- Búsqueda con input vacío (sugerencias / recientes)
- Categorías sin categorías del usuario (solo las del sistema)
- Sin conexión (banner o pantalla según contexto)

### Estados de carga
- Splash/loading inicial mientras verifica sesión activa
- Loading al guardar (crear/editar lista o producto)
- Loading al sincronizar con Firestore
- Skeleton loaders en Mis Listas y Detalle de Lista

### Estados de error
- Credenciales incorrectas en Login
- Correo ya registrado en Registro
- Validación de formularios (correo inválido, contraseñas no coinciden, contraseña débil)
- Error de red / Firestore
- Error al subir imagen de producto

### Diálogos de confirmación
- Eliminar lista (mostrando cuántos productos se perderán)
- Eliminar producto
- Eliminar categoría (solo si no tiene productos asociados)
- Cerrar sesión
- Reiniciar lista (marcar todos como pendientes)
- Descartar cambios al editar lista o producto

### Pantallas adicionales (no en mockup)
- Recuperar contraseña (flujo desde Login)
- Editar Lista (igual a Nueva Lista con datos precargados)
- Editar Producto (igual a Agregar Producto con datos precargados)
- Bottom sheet "Mover producto a otra lista"
- Bottom sheet "Mover lista a carpeta" → **fuera de alcance por ahora** (sin sistema de carpetas)

### Feedback al usuario (Snackbars)
- Tras agregar producto
- Tras duplicar lista o producto
- Tras mover producto (con opción "Deshacer")
- Tras eliminar (con opción "Deshacer" cuando sea seguro)
- Tras marcar como comprado
- Indicador visual de sincronización activa/inactiva en Configuración

---

## Plan de fases

### Fase 1 — Setup
Gradle (KTS), tema M3 con Poppins y paleta fiel al mockup, modo claro + oscuro,
Navigation Compose, Hilt, Firebase config lista para `google-services.json`,
estructura de carpetas, `strings.xml` en español, `Dimens.kt`.

### Fase 2 — Autenticación
Welcome, Login, Registro, Recuperar contraseña, validaciones de formulario,
manejo de errores (credenciales, red, correo duplicado), persistencia de sesión.

### Fase 3 — Listas
CRUD completo (crear, leer, editar, eliminar, renombrar, duplicar),
estados vacío / en progreso / completado, reiniciar lista,
bottom sheet de opciones, diálogos de confirmación, snackbars, skeleton loaders.

### Fase 4 — Productos
CRUD + bottom sheet con 5 acciones (marcar comprado, editar, duplicar, mover, eliminar),
Agregar Producto, Detalle Producto, Editar Producto, mover a otra lista,
animación al marcar comprado, subida de imágenes con Storage + Coil.

### Fase 5 — Terminación
Categorías (sistema + usuario), Búsqueda global, Configuración (perfil, cerrar sesión,
estado sincronización), Créditos, estados de red, accesibilidad, animaciones,
revisión completa de strings y valores mágicos.

---

## Buenas prácticas obligatorias
- Strings en `strings.xml`, colores en `Color.kt`, dimensiones repetidas en `Dimens.kt`
- `ViewModel` con `StateFlow`, no `LiveData`
- `UiState` como `data class` con `isLoading`, `data`, `error`; o `sealed class` si aplica
- Repositorios: interfaz en `domain`, implementación en `data`
- Casos de uso con `operator fun invoke`
- Manejo de errores con `Result<T>` o `sealed class Resource<T>`
- Hilt para inyección, sin singletons manuales
- Navigation Compose type-safe (rutas selladas o con Kotlin Serialization)
- Componentes reutilizables en `ui/components`:
  `ProductItem`, `ListCard`, `PrimaryButton`, `SecondaryButton`, `EmptyState`,
  `LoadingIndicator`, `SkeletonLoader`, `ConfirmDialog`, `AppSnackbar`, etc.
- Sin magic numbers, sin colores hardcodeados, sin strings hardcodeados
