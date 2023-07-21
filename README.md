<br/>
<p align="center">
  <h3 align="center">AYUB</h3>

  <p align="center">
    Access Your Unforgettable Beats
    <br/>
    <br/>
  </p>
</p>

![Contributors](https://img.shields.io/github/contributors/aadarshKsingh/AYUB?color=dark-green) ![Issues](https://img.shields.io/github/issues/aadarshKsingh/AYUB) ![License](https://img.shields.io/github/license/aadarshKsingh/AYUB)

## About The Project

![Screenshot](https://raw.githubusercontent.com/aadarshKsingh/AYUB/main/src/main/resources/com/ayu/beats/ayub/images/image.jpg)

AYUB is a minimal media player based on ffmpeg and vlcj. It allows users to access and play their favorite media files with ease, can show metadata and allow adding playlists.

## Gradle Configuration

```groovy
javafx {
    version = '17.0.6'
    modules = ['javafx.controls', 'javafx.fxml', 'javafx.media']
}

dependencies {
    implementation 'uk.co.caprica:vlcj:4.7.0'
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    implementation 'org.bytedeco:javacv-platform:1.5.6'
    implementation 'org.openjfx:javafx-media:15.0.1'
}
```

## Features

- Minimalistic user interface
- Support for various media formats through ffmpeg and VLC
- Based on the MVC architecture

## Compatibility

AYUB has been tested with JDK 17 and Gradle 7.6.1.

## Dependencies

In order to use AYUB, you need to have the following dependencies installed:

- ffmpeg
- VLC

Please make sure these dependencies are properly installed before running the application.

## Build Instructions

To build and run AYUB, follow these steps:

1. Clone the repository.
2. Install the required dependencies (ffmpeg, VLC).
3. Open the project in your preferred IDE.
4. Configure your IDE to use JDK 17.
5. Build the project using Gradle.
6. Run the application.

## License

AYUB is licensed under GNU GPL 3.0 license.

Please refer to [LICENSE](LICENSE) file for more details about the license terms.

---
## Contributing



### Creating A Pull Request

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See [LICENSE](https://github.com/aadarshKsingh/AYUB/blob/main/LICENSE.md) for more information.

## Authors

* **aadarshKsingh** - ** - [aadarshKsingh](https://github.com/aadarshKsingh) - **

