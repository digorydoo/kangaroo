# Kangaroo

## Overview

Github: n/a
Home: www.digorydoo.ch

Kangaroo is a VST3 instrument written in Kotlin/native.

**This is an early stage of the development. Most of the necessary features have not been implemented yet. Expect bugs
or even build problems.**

## License

TBD

## TODO

* Allow creating and destroying text fields from Kotlin (because I don't want to implement my own Edit field or fonts)
* vowel filters
* morphing waveforms
* saturation everywhere
* Arabification quarter tones
* Stereo enhancement by using comb filters and stereo delays
* Try to remove as much SDK dependencies as possible
* Compile for macOS standard architectures. macOS Architecture is currently fixed to x86_64, because Kotlin/native
  compiles for this architecture
* Compile the stuff on PC. CMake might recognise the free version of the Visual Studio compiler?

## Building kangaroo

Run CMake:
* Open CMakeLists.txt in a text editor and change vst3sdk_SOURCE_DIR as appropriate
* Set the source directory to the project root (where CMakeLists.txt is)
* Set the build directory to cmake/
* Push Configure
* Push Generate

Do the following once after running CMake:
* Open project in Xcode
* Go to target Kangaroo > Build phases > Copy files and clear the Subpath (remove "Snapshots")
* Go to target Kangaroo > Build settings > Architectures, and set both Debug and Release to x86_64
* In a terminal, execute ./gradlew build. This will only compile the Kotlin sources.
* The Kotlin build should have created a ./build/bin/native/debugStatic/libnative.a. Add this to the Xcode project.
* The project should now build in Xcode

During development, you can build and run the plugin with:
$ ./make.sh

If you changed the Kotlin/cpp glue code, and gradle was unable to detect this, you need to clean the build with:
$ ./gradlew clean

If you want to clean the entire project including the Xcode build, you can do this with:
$ ./make.sh clean

## Philosophy and coding guidelines

* Make the cpp layer as thin as possible
  * Keep the dependency to the VST3 SDK to a minimum
  * Do as much in Kotlin as is reasonable
* Keep It Simple, Stupid!
  * Keep classes small. Kotlin classes over 300 lines of code should probably need refactoring.
  * Avoid inheritance depth > 1. Prefer composition to inheritance wherever possible.
  * If a feature makes code and/or the UX overly complicated, don't implement it.
  * Keep the UI nice and clean. No photo images. Avoid bitmaps and gradients if possible.
* You're Not Gonna Need It
  * Don't implement functionality until it's actually needed
  * Don't expose VST3 functionality to Kotlin unless actually needed

## Contribution

Pull requests at this stage of development will most likely be ignored, I'm afraid. This is a hobby project, so the time
I can spend on it is limited. I'd rather spend it on coding rather than discussions and code reviews.

## Links, hints

* The VST3 SDK can be downloaded from GitHub: https://github.com/steinbergmedia/vst3sdk
* The installer of the VST3 Plugin Test Host app is located in the VST3 SDK, vst3sdk/bin/Mac OS X/.
