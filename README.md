# ToolList 🛒

Aplicación Android nativa de listas de compras multipropósito (supermercado, ferretería, proyectos, o cualquier compra). Permite crear listas personalizadas, organizar productos por categoría, estimar costos y sincronizar todo entre dispositivos mediante Firebase.

## 📱 Acerca del proyecto

Proyecto académico de la materia **Aplicaciones Móviles** — Universidad Pontificia Bolivariana (UPB), 2026.

## 🛠️ Stack técnico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM + Clean Architecture (data / domain / ui)
- **Backend:** Firebase (Auth, Firestore con persistencia offline, Storage)
- **Inyección de dependencias:** Hilt
- **Navegación:** Navigation Compose
- **Asincronía:** Kotlin Coroutines + Flow
- **Imágenes:** Coil
- **Tipografía:** Poppins vía Google Fonts Downloadable Fonts

## ✨ Funcionalidades principales

- Crear, editar y eliminar listas con colores identificadores
- CRUD completo de productos (nombre, cantidad, unidad, categoría, precio, imagen)
- Marcar productos como comprados con seguimiento de progreso
- Búsqueda global entre todas las listas
- Gestión de categorías propias del usuario + categorías del sistema
- Autenticación con correo y contraseña
- Sincronización en la nube y persistencia offline
- Modo claro y oscuro

## 📐 Diseño

El diseño visual completo se encuentra en `docs/design/toolist-mockups.png`.

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio
2. Abrir en Android Studio (Koala o superior)
3. Agregar el archivo `google-services.json` en `app/` (solicitar al equipo)
4. Sincronizar Gradle
5. Ejecutar en emulador o dispositivo físico (minSdk 29)

## 📋 Estado del desarrollo

Consultar `CONTEXT.md` para ver el alcance completo, decisiones técnicas y plan de fases.

## 👥 Equipo

- Anthony Arango Betancur
- Camilo Marín Muriel
- (otros integrantes del equipo)

## 📄 Licencia

Ver archivo `LICENSE`.
