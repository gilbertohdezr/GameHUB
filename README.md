# 🎮 GameHUB

<p align="center">

Aplicación de escritorio desarrollada en Java que funciona como un concentrador de minijuegos con una arquitectura modular orientada al aprendizaje de Programación Orientada a Objetos y trabajo colaborativo con Git.

</p>

---

## 📖 Acerca del proyecto

GameHUB es un proyecto educativo desarrollado para servir como base de las prácticas de **Programación Orientada a Objetos (POO)** de la **Universidad Tecnológica de Tecámac (UTTT)**.

Su objetivo principal es proporcionar una infraestructura sólida sobre la cual los estudiantes puedan desarrollar minijuegos de forma independiente, aplicando conceptos de:

- Programación Orientada a Objetos
- Arquitectura de Software
- Interfaces
- Modularidad
- Trabajo colaborativo
- Git y GitHub

A diferencia de otros proyectos académicos donde cada equipo comienza desde cero, GameHUB proporciona una arquitectura común que permite concentrar el esfuerzo en el diseño e implementación de funcionalidades, manteniendo una estructura uniforme para todos los participantes.

---

# 🎯 Objetivos

- Aprender Programación Orientada a Objetos mediante un proyecto real.
- Comprender la importancia de una arquitectura bien diseñada.
- Aplicar separación de responsabilidades.
- Desarrollar software modular.
- Facilitar el trabajo colaborativo mediante Git.
- Comprender el flujo de trabajo profesional basado en ramas y Pull Requests.
- Servir como base para futuras generaciones de estudiantes.

---

# 🏛 Filosofía del proyecto

GameHUB fue construido siguiendo una idea muy simple:

> **La arquitectura debe construirse antes que las funcionalidades.**

Por esta razón, la primera versión del proyecto no implementa ningún juego completo.

En cambio, proporciona una infraestructura estable y desacoplada sobre la cual cada equipo podrá desarrollar su propio módulo sin modificar el núcleo del sistema.

Esto permite que el aprendizaje se centre en:

- diseño
- organización
- reutilización
- mantenimiento
- colaboración

antes que únicamente en "hacer funcionar el programa".

---

# 🏗 Arquitectura

```
                 Main
                  │
                  ▼
              GameHub
                  │
                  ▼
        VentanaPrincipal (JFrame)
                  │
      ┌───────────┴───────────┐
      │                       │
      ▼                       ▼
 PanelMenu              CardLayout
                              │
     ┌────────────────────────┼────────────────────────┐
     ▼                        ▼                        ▼
 PanelInicio         Adivina Número         Piedra Papel Tijera

     ▼                        ▼                        ▼
 Tres en Raya              Dados                 Memorama
```

Toda la navegación se realiza mediante **CardLayout**, desacoplando completamente el menú del contenido mostrado.

---

# 📁 Estructura del proyecto

```
src
└── main
    ├── java
    │
    ├── hub
    │      GameHub.java
    │
    ├── interfaces
    │      MiniJuego.java
    │      MenuListener.java
    │
    ├── juegos
    │      adivina/
    │      construccion
    │           PanelEnConstruccion.java
    │      dados/
    │      memorama/
    │      piedra/
    │      tres/
    │
    ├── vista
    │      VentanaPrincipal.java
    │      PanelMenu.java
    │      PanelInicio.java
    │
    └── Main.java

resources
├── icons
└── images
```

---

# 🧩 Componentes principales

## Main

Punto de entrada de la aplicación.

---

## GameHub

Coordina el inicio de la aplicación y crea la ventana principal.

---

## VentanaPrincipal

Contiene toda la interfaz principal.

Responsabilidades:

- crear la ventana
- administrar CardLayout
- mostrar vistas
- recibir eventos del menú

---

## PanelMenu

Menú lateral de navegación.

No conoce la implementación de la ventana principal.

La comunicación se realiza mediante la interfaz:

```
MenuListener
```

---

## PanelInicio

Pantalla de bienvenida del sistema.

---

## PanelEnConstruccion

Panel reutilizable utilizado temporalmente por los módulos que aún no han sido desarrollados.

---

## MiniJuego

Interfaz base que permitirá implementar cualquier nuevo juego.

Cada juego deberá cumplir el siguiente contrato:

```java
String getNombre();

JPanel getPanel();

void reiniciar();
```

---

# 🎮 Minijuegos

La infraestructura contempla los siguientes módulos:

- 🎯 Adivina el Número
- ✋ Piedra, Papel o Tijera
- ⭕ Tres en Raya
- 🎲 Dados
- 🧠 Memorama

En la versión **1.0.0** todos los módulos se encuentran preparados para su implementación.

---

# 🚀 Tecnologías

- Java
- Swing
- Maven
- Git
- GitHub

---

# 🌳 Flujo de trabajo recomendado

```
main
│
├── develop
│
├── feature/adivina
├── feature/piedra
├── feature/tres
├── feature/dados
└── feature/memorama
```

Cada equipo desarrollará una funcionalidad en una rama independiente.

Posteriormente se integrará mediante Pull Request.

---

# 📚 Objetivos académicos

Durante el desarrollo del proyecto los estudiantes aplicarán:

- Clases
- Objetos
- Encapsulamiento
- Interfaces
- Polimorfismo
- Modularidad
- Arquitectura
- Git
- GitHub
- Trabajo colaborativo

---

# 📈 Roadmap

## v1.0.0

- ✅ Infraestructura base
- ✅ Navegación
- ✅ Arquitectura modular
- ✅ Documentación

---

## v1.1.0

- Implementación de Adivina el Número.

---

## v1.2.0

- Implementación de Piedra, Papel o Tijera.

---

## v1.3.0

- Implementación de Dados.

---

## v1.4.0

- Implementación de Tres en Raya.

---

## v1.5.0

- Implementación de Memorama.

---

## Futuro

Posibles mejoras:

- Sistema de puntajes.
- Persistencia de partidas.
- Configuración.
- Estadísticas.
- Sistema de plugins.
- Nuevos minijuegos.

---

# 🤝 Contribuciones

Este proyecto fue diseñado principalmente como herramienta educativa.

Las contribuciones son bienvenidas siempre que respeten la arquitectura existente y mantengan el objetivo didáctico del proyecto.

---

# 📜 Principios del proyecto

GameHUB sigue los siguientes principios de diseño:

1. La arquitectura tiene prioridad sobre las funcionalidades.
2. El código debe ser sencillo de comprender para estudiantes.
3. Cada módulo debe desarrollarse de forma independiente.
4. La colaboración mediante Git forma parte del aprendizaje.
5. Se favorece la simplicidad sobre la complejidad innecesaria.
6. Toda nueva funcionalidad debe respetar la arquitectura existente.
7. El proyecto debe poder crecer sin modificar su núcleo.

---

# 👨‍🏫 Autor

**Gilberto Hernández**

Profesor de Programación Orientada a Objetos

Universidad Tecnológica de Tula-Tepeji (UTTT)

---

# ✨ Créditos

La arquitectura inicial, la documentación, el diseño modular y diversas decisiones técnicas de este proyecto fueron desarrolladas por **Gilberto Hernández** con la asistencia de **ChatGPT (OpenAI)** como herramienta de apoyo para el análisis, revisión y generación de propuestas técnicas.

Todas las decisiones finales relacionadas con el diseño, la implementación, la organización del proyecto y los objetivos académicos fueron tomadas y validadas por el autor.

---

# 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.

Consulta el archivo **LICENSE** para obtener más información.# GameHUB
