# Kangaroo

## Overview

Kangaroo is a VST3 instrument written in Kotlin/native.

* License: GNU General Public License v3.0
* Github: https://github.com/digorydoo/kangaroo
* Home: https://www.digorydoo.ch/kangaroo

## Project status

Current project status is **experimental**.

The project is currently facing **major performance issues**. The DAW's CPU usage indicator clearly shows Kangaroo is
using way too much CPU time. When all three oscillators are enabled, audible glitches occur at least on a Mac Mini 2018
i7 3.2 GHz.

It may or may not be possible to overcome these issues.

## Features

The following features have been implemented:

* A nice and clean UI
* Polyphone voices management (currently set to 32 voices)
* Three oscillators with variable shapes
* Amplitude envelope
* Low pass filter
* High pass filter
* Band pass filter
* Formant/vowel filter

Implementation of more features (e.g. LFOs, effects) are **currently on hold** due to the performance issues mentioned
above.

## Building kangaroo

Kangaroo is developed with IntelliJ IDEA under macOS. The project may or may not build on PC (a proper PC build is
planned, once the performance issues are gone).

First of all, clone the vst3sdk Github project https://github.com/steinbergmedia/vst3sdk. Follow the instructions of
that repo and build the SDK.

Next, run CMake on kangaroo. Here's how you do it with the UI version of CMake:

* Open CMakeLists.txt in a text editor and change vst3sdk_SOURCE_DIR as appropriate
* Set the source directory to the project root (where CMakeLists.txt is)
* Set the build directory to `<kangaroo project root>/cmake/`
* Push Configure
* Push Generate

Do the following once after running CMake:

* Open project in Xcode
* There is an issue with M1 builds that needs to be fixed. Currently, only x86_64 builds are supported. Therefore, go to
  target Kangaroo > Build settings > Architectures, and set both Debug and Release to x86_64 (FIXME fix M1 builds)
* In a terminal, execute ./gradlew build. This will only compile the Kotlin sources.
* The Kotlin build should have created a ./build/bin/native/debugStatic/libnative.a. Add this to the Xcode project.
  (FIXME make this extra step unnecessary somehow).
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
    * Keep the UI nice and clean. Avoid bitmaps and gradients if possible.

## Links, hints

* The VST3 SDK can be downloaded from GitHub: https://github.com/steinbergmedia/vst3sdk
* The installer of the VST3 Plugin Test Host app is located in the VST3 SDK, vst3sdk/bin/Mac OS X/.
