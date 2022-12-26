#!/bin/bash

# Exit as soon as one of the commands fail.
set -e

PLAIN=$'\e[0m'
RED=$'\e[31m'
GREEN=$'\e[32m'
YELLOW=$'\e[33m'

SELFDIR=$(SELFDIR=$(dirname "$0") && bash -c "cd \"$SELFDIR\" && pwd")
CPPBUILDDIR="$SELFDIR/cmake"
TESTHOSTAPPDIR="/Applications/VST3PluginTestHost.app"
TESTHOSTAPPEXE="./Contents/MacOS/VST3PluginTestHost"

# The Xcode post-build step makes a symbolic link of the compiled plugin here:
PLUGINDIR="$HOME/Library/Audio/Plug-Ins/VST3"

if [[ "$1" == "clean" ]]; then
   echo "${YELLOW}Cleaning...${PLAIN}"

   cd "$SELFDIR"
   ./gradlew clean

   cd "$CPPBUILDDIR"
   xcodebuild clean -quiet

   exit 1
elif [[ "$1" != "" && "$1" != "build" && "$1" != "run" ]]; then
   echo "make.sh: Argument not understood: $1"
   exit 1
fi

# ----------------------------------------------------------------------------
echo
echo "${YELLOW}Compiling kotlin...${PLAIN}"
cd "$SELFDIR"

./gradlew build --warning-mode all
cp ./build/bin/native/debugStatic/libnative_api.h src/nativeMain/cpp

# ----------------------------------------------------------------------------
echo
echo "${YELLOW}Compiling cpp...${PLAIN}"
cd "$CPPBUILDDIR"
xcodebuild -target=Kangaroo -quiet

# ----------------------------------------------------------------------------

if [[ "$1" == "" || "$1" == "run" ]]; then
   echo
   echo "${YELLOW}Launching VST3PluginTestHost...${PLAIN}"

   if [[ ! -d "$PLUGINDIR" ]]; then
      echo "Not found: $PLUGINDIR"
      echo "Something's wrong with the build."
      exit 1
   fi

   if [[ ! -d "$TESTHOSTAPPDIR" ]]; then
      echo "Missing: $TESTHOSTAPPDIR"
      echo "Please download and install VST3PluginTestHost from Steinberg website and try again."
      exit 1
   fi

   cd "$TESTHOSTAPPDIR"
   $TESTHOSTAPPEXE --pluginfolder="$PLUGINDIR"
   STATUS="$?"

   if [[ "$STATUS" != 0 ]]; then
      echo "${RED}Failed with exit status ${STATUS}${PLAIN}"
      exit $STATUS
   fi
fi

echo "${GREEN}Done.${PLAIN}"
