# 📸 Aplicación de Cámara y Micrófono 🎤

![min-sdk](https://img.shields.io/badge/minSdk-26-brightgreen) ![target-sdk](https://img.shields.io/badge/targetSdk-36-blue) ![made-with-kotlin](https://img.shields.io/badge/Made%20with-Kotlin-%237F52FF)

Aplicación Android nativa y completa, desarrollada en Kotlin, que ofrece una experiencia rica en funcionalidades multimedia, desde la captura de fotos y audio hasta la organización avanzada con álbumes, etiquetas y temas personalizables.

---

## ✨ Características Principales

*   **📷 Cámara Avanzada**:
    *   Captura con **CameraX** y vista previa en tiempo real.
    *   Controles de Flash (Auto/On/Off), Temporizador (0/3/5/10 s) y cambio de cámara (frontal/trasera).
    *   Filtros en vivo: Escala de grises, Sepia, Brillo.
    *   Soporte para formatos **JPEG** y **PNG**.
    *   Guardado en `MediaStore` con metadatos EXIF (para JPEG).
    *   Retroalimentación háptica y sonora al tomar una foto.

*   **🎙️ Grabadora de Audio**:
    *   Grabación con `MediaRecorder` en formatos **M4A/AAC**.
    *   Visualización del nivel de audio en tiempo real.
    *   Controles de grabación: Iniciar, Pausar, Reanudar y Detener.
    *   Configuración de calidad y duración máxima.
    *   Guardado en `MediaStore` (`Music/Recordings`).

*   **🖼️ Galerías Inteligentes**:
    *   Galerías separadas para fotos y audios.
    *   Búsqueda por nombre y múltiples opciones de ordenamiento (fecha, nombre, duración).
    *   Selección múltiple (con pulsación larga) para acciones en lote.

*   **✏️ Organización y Edición**:
    *   **Visor de imágenes**: Zoom, compartir y ver metadatos EXIF.
    *   **Editor de imágenes**: Recortar y rotar.
    *   **Reproductor de audio**: Interfaz con forma de onda, controles de reproducción, y opciones para renombrar, compartir o eliminar.
    *   **Organizador**: Asigna álbumes y etiquetas personalizadas a tus archivos.

*   **🎨 Temas y Personalización**:
    *   Dos temas principales: **Azul** y **Guinda**.
    *   Soporte completo para modo **claro y oscuro**.
    *   Cambio de tema en tiempo real sin necesidad de reiniciar la aplicación.

*   **📤 Exportación**:
    *   Exporta múltiples archivos multimedia como un único archivo `.zip`.
    *   El archivo se guarda en la carpeta de Descargas del dispositivo.

---

## 📱 Capturas de Pantalla

<details>
  <summary>Haz clic para ver las capturas de pantalla</summary>
  
  | | |
  |:---:|:---:|
  | ![1](https://github.com/user-attachments/assets/25c0d02b-4e12-49b7-97f1-ec5b1659e0df) | ![2](https://github.com/user-attachments/assets/269fe66f-ad4a-4971-80fa-c4c1fc1eac3a) |
  | ![3](https://github.com/user-attachments/assets/a7aed140-9d43-44a2-a7b5-bcc8a25d839d) | ![4](https://github.com/user-attachments/assets/9003a788-f756-4612-8f01-ab656b309532) |
  | ![8](https://github.com/user-attachments/assets/5e245247-1502-4602-82c0-952491c3e3ca) | ![7](https://github.com/user-attachments/assets/2b7095b3-6393-4dba-a361-50986c80f01a) |
  | ![6](https://github.com/user-attachments/assets/6b881512-52c0-4293-aa31-eeede099f1b0) | ![5](https://github.com/user-attachments/assets/c9af8d35-e89f-45d7-a057-5753c1f8f428) |

</details>

---

## 🚀 Primeros Pasos

### Requisitos

*   Android Studio Giraffe o superior.
*   Android SDK 36.
*   Dispositivo o emulador con Android 8.0 (API 26) o superior.

### Instalación

1.  Clona el repositorio en tu máquina local.
2.  Abre el proyecto con Android Studio.
3.  Sincroniza el proyecto con los archivos Gradle.
4.  Ejecuta la aplicación en un dispositivo o emulador.

---

## 🛠️ Pila Tecnológica y Dependencias Clave

Esta aplicación está construida con un enfoque moderno de desarrollo de Android, utilizando las siguientes tecnologías:

| Componente | Descripción |
|---|---|
| **Lenguaje** | **Kotlin** |
| **UI** | **ViewBinding**, **Material Components** |
| **Cámara** | **CameraX** para una API de cámara moderna y consistente. |
| **Audio** | **MediaRecorder** (grabación), **Media3 ExoPlayer** (reproducción). |
| **Base de Datos** | **Room** para la persistencia de metadatos (álbumes y etiquetas). |
| **Imágenes** | **Glide** (carga y caché), **PhotoView** (zoom), **uCrop** (edición). |
| **Asincronía** | **Coroutines** para operaciones en segundo plano. |
| **Almacenamiento** | **MediaStore** para una gestión segura y moderna de archivos. |

---

## 🗂 Estructura del Proyecto

El proyecto sigue una arquitectura limpia y modular, con los siguientes directorios clave:

*   `activities`: Contienen la lógica de la interfaz de usuario para cada pantalla.
*   `adapters`: Adaptadores para las `RecyclerView` de las galerías.
*   `utils`: Clases de utilidad para funciones transversales (retroalimentación háptica, sonora).
*   `layout`: Archivos XML que definen la interfaz de usuario de cada componente.

---

## 🔐 Permisos

La aplicación gestiona los permisos de forma segura y contextual:

*   **Android 13 (API 33)+**: `READ_MEDIA_IMAGES`, `READ_MEDIA_AUDIO`.
*   **Versiones Anteriores**: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` (solo en API ≤ 28).
*   **Otros Permisos**: `CAMERA`, `RECORD_AUDIO`.

---

## ⚠️ Limitaciones Conocidas

*   **Formato de Audio**: `MediaRecorder` no soporta la codificación a MP3 de forma nativa. Se utilizan los formatos M4A/AAC, que son estándar en Android.
*   **Metadatos de Imagen**: El estándar EXIF solo es compatible con archivos JPEG. Las imágenes guardadas en formato PNG no contendrán estos metadatos.
