# Linaje Framework

Linaje Framework es un conjunto de utilidades para facilitar la creación de aplicaciones Java Standard y de aplicaciones Java con UI basada en Swing.
Está dividido en Linaje Core y Linaje GUI.

Es totalmente independiente y no tiene dependencias con otras librerías externas distintas al JDK / OpenJDK.

Está en Español y en [Inglés](README.md), pero es fácilmente traducible a otros idiomas ya que los Strings descriptivos y de mensajes están en ficheros de localización.




## Linaje Core

Linaje Core está compuesto de clases no visuales independientes de los paquetes de Swing. Utilidades para trabajar con Strings, Listas, Ficheros, Trazas & logs, Recursividad, comunicación entre aplicaciones, localización, entre muchas otras.

Si usas Maven simplemente añade la siguiente dependencia a tu pom.xml:
```
<dependency>
  <groupId>io.github.linaje-projects</groupId>
  <artifactId>LinajeCore</artifactId>
  <version>1.0.1</version>
</dependency>
```

Ejecutar algunos tests de  LinajeCore:
```
java -cp linajeCore.jar linaje.tests.Tests
```


## Linaje GUI

Linaje GUI es la parte del framework con utilidades para facilitar la creación de aplicaciones basadas en Swing. Aquí lo principal es el LinajeLookAndFeel, que es un UI fácilmente configurable que permite personalizar rápidamente cualquier aplicación Swing nueva o ya existente. Además también está compuesto de un conjunto de componentes Swing que permiten facilitar la creción de aplicaciones y de dotarlas con un funcionalizades avanzadas de forma sencilla.

Está optimizado para JAVA8 y se ha probado en Windows 10/11 y en Linux Mint 20.3

 
Si usas Maven simplemente añade la siguiente dependencia a tu pom.xml:
```
<dependency>
  <groupId>io.github.linaje-projects</groupId>
  <artifactId>LinajeGUI</artifactId>
  <version>1.0.1</version>
</dependency>
```

Para optimizar el LookAndFeel:
```
java -cp linajeCore.jar;linajeGUI.jar linaje.gui.ui.UIConfig
```

Ejemplo de aplicación con Linaje Framework
```
java -cp linajeCore.jar;linajeGUI.jar linaje.gui.tests.TestApp
```

Si quieres probar LinajeLookAndFeel en una aplicación no optimizada para este LookAndFeel: -- Menú otros --> Abrir [SwingSet](https://github.com/Linaje-Projects/Linaje/raw/v0.1.1-alpha-Linaje-Framework/LinajeGUI/lib/SwingSet2.jar)

```
java -cp linajeCore.jar;linajeGUI.jar;SwingSet2.jar linaje.gui.tests.UITest
```

Otros tests
```
java -cp linajeCore.jar;linajeGUI.jar linaje.gui.tests.TestsLauncher
```

En linux separa los jars del classpath con ':' en lugar de con ';'
```
java -cp linajeCore.jar:linajeGUI.jar linaje.gui.ui.UIConfig
```

Si tienes problemas con 'Assistive Technology' se puede desactivar con el siguiente parámetro
```
java -Djavax.accessibility.assistive_technologies=" " -cp linajeCore.jar:linajeGUI.jar linaje.gui.ui.UIConfig
```
