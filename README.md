# Linaje Framework

Linaje Framework is a set of utilities to make it easy to create Java Standard applications and Java applications with Swing-based UIs.
It is divided into Linaje Core and Linaje GUI.

It is completely independent and has no dependencies on external libraries other than JDK / OpenJDK.

It is in [Spanish](README_ES.md) and English, but it is easily translatable to other languages since the descriptive and message Strings are in localization files.




## Linaje Core

Linaje Core is made up of non-visual classes independent of Swing packages. Utilities to work with Strings, Lists, Files, Traces & logs, Reflection, communication between applications, localization, among many others.

Run some LinajeCore tests:
```
java -cp linajeCore.jar linaje.tests.Tests
```


## Linaje GUI

Linaje GUI is the part of the framework with utilities to facilitate the creation of Swing-based applications. The main thing here is the LineageLookAndFeel, which is an easily configurable UI that allows you to quickly customize any new or existing Swing application. In addition, it is also made up of a set of Swing components that allow the creation of applications to be facilitated and to provide them with advanced functionalities in a simple way.

It is optimized for JAVA8 and has been tested on Windows 10/11 and Linux Mint 20.3

 


To optimize LookAndFeel
```
java -cp linajeCore.jar;linajeGUI linaje.gui.ui.UIConfig
```

Application example with Linaje Framework
```
java -cp linajeCore.jar;linajeGUI linaje.gui.tests.TestApp
```

If you want to test LinajeLookAndFeel in an application not optimized for this LookAndFeel: -- Others menu --> Open [SwingSet](https://github.com/Linaje-Projects/Linaje/raw/v0.1.1-alpha-Linaje-Framework/LinajeGUI/lib/SwingSet2.jar)

```
java -cp linajeCore.jar;linajeGUI;SwingSet2.jar linaje.gui.tests.UITest
```

Other tests
```
java -cp linajeCore.jar;linajeGUI linaje.gui.tests.TestsLauncher
```

On linux separate the jars from the classpath with ':' instead of ';'
```
java -cp linajeCore.jar:linajeGUI linaje.gui.ui.UIConfig
```

If you have problems with 'Assistive Technology' it can be disabled with the following parameter
```
java -Djavax.accessibility.assistive_technologies=" " -cp linajeCore.jar:linajeGUI linaje.gui.ui.UIConfig
```
