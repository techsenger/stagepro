# Techsenger StagePro

Techsenger StagePro is a library that allows you to create custom stages with nearly any configuration, while remaining
easy to use. The project also includes a sampler module, featuring key samples to help you get started with the library.

## Table of Contents
* [Demo](#demo)
    * [Light Theme](#demo-light-theme)
    * [Dark Theme](#demo-dark-theme)
* [Features](#features)
* [Limitations](#limitations)
* [JavaFX Issues](#javafx-issues)
* [Dependencies](#dependencies)
* [Usage](#usage)
* [Code building](#code-building)
* [Running Sampler](#sampler)
* [License](#license)
* [Contributing](#contributing)
* [Support Us](#support-us)

## Demo <a name="demo"></a>

### Light Theme <a name="demo-light-theme"></a>

![stagepro-light-theme](https://github.com/user-attachments/assets/ceee91fd-f43d-4193-adda-4ff0e70b0d8a)

### Dark Theme <a name="demo-dark-theme"></a>

![stagepro-dark-theme](https://github.com/user-attachments/assets/3fc4a59d-a745-4b99-bce5-d6d7cfa152c0)

## Features <a name="features"></a>

Key features include:

* Fully customizable title bar configurations.
* Support for dynamic configuration changes.
* Ability to place basic buttons on either the left or right side.
* Two maximize button policies.
* Styling via CSS.
* Dark mode support.
* SVG-based button icons.
* Only two events triggered during resizing (start and finish).

## Limitations <a name="limitations"></a>

* No shadow support. Currently, shadows for Stage are not supported as JavaFX provides no built-in way to render
shadows around transparent windows. This would likely require platform-specific native code.
* No native window management features. Edge-based behaviors like Windows Snap Layouts and GNOME Edge Tiling cannot
be properly implemented using Java alone. These features also require platform-specific native code.

## JavaFX Issues <a name="javafx-issues"></a>

* Use JavaFX 16–20 or versions after 24-ea+19 due to known bugs ([JDK-8344372](https://bugs.openjdk.org/browse/JDK-8344372)).
* Resizing the stage via the top/left border causes jitter on the opposite side ([JDK-8347155](https://bugs.openjdk.org/browse/JDK-8347155)).

## Dependencies <a name="dependencies"></a>

This project is available on Maven Central:

```
<dependency>
    <groupId>com.techsenger.stagepro</groupId>
    <artifactId>stagepro-core</artifactId>
    <version>${stagepro.version}</version>
</dependency>
```

## Usage <a name="usage"></a>

To create a standard Stage, use the code below. To explore all features, check out the examples in the sampler.

```
@Override
public void start(Stage stage) {
    var controller = new StandardStageController(stage, 800, 600);
    var content = new VBox(...);
    controller.setContent(content);
    stage.show();
}
```

## Code Building <a name="code-building"></a>

To build the library use standard Git and Maven commands:

    git clone https://github.com/techsenger/stagepro
    cd stagepro
    mvn clean install

## Running Sampler <a name="sampler"></a>

To run the sampler execute the following commands in the root of the project:

    cd stagepro-sampler
    mvn javafx:run

Please note, that debugger settings are in `stagepro-sampler/pom.xml` file.

## License <a name="license"></a>

Techsenger StagePro is licensed under the Apache License, Version 2.0.

## Contributing <a name="contributing"></a>

We welcome all contributions. You can help by reporting bugs, suggesting improvements, or submitting pull requests
with fixes and new features. If you have any questions, feel free to reach out — we’ll be happy to assist you.

## Support Us <a name="support-us"></a>

You can support our open-source work through [GitHub Sponsors](https://github.com/sponsors/techsenger).
Your contribution helps us maintain projects, develop new features, and provide ongoing improvements.
Multiple sponsorship tiers are available, each offering different levels of recognition and benefits.

