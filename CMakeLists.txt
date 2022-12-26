cmake_minimum_required(VERSION 3.14.0)
set(CMAKE_OSX_DEPLOYMENT_TARGET 11.1 CACHE STRING "")
set(vst3sdk_SOURCE_DIR /Users/pamberg/Develop/zz-other/vst-sdk/vst3sdk)

if(NOT vst3sdk_SOURCE_DIR)
    message(FATAL_ERROR "Path to VST3 SDK is empty!")
endif()

project(Kangaroo
    # This is your plug-in version number. Change it here only.
    # Version number symbols usable in C++ can be found in
    # source/version.h and ${PROJECT_BINARY_DIR}/projectversion.h.
    VERSION 1.0.0.0 
    DESCRIPTION "Kangaroo VST 3 Plug-in"
)

set(SMTG_VSTGUI_ROOT "${vst3sdk_SOURCE_DIR}")

add_subdirectory(${vst3sdk_SOURCE_DIR} ${PROJECT_BINARY_DIR}/vst3sdk)
smtg_enable_vst3_sdk()

smtg_add_vst3plugin(Kangaroo
    src/nativeMain/cpp/AudioProcessor.cpp
    src/nativeMain/cpp/AudioProcessor.h
    src/nativeMain/cpp/AudioProcessorGlue.cpp
    src/nativeMain/cpp/AudioProcessorGlue.h
    src/nativeMain/cpp/CDrawContextGlue.cpp
    src/nativeMain/cpp/CDrawContextGlue.h
    src/nativeMain/cpp/CanvasView.cpp
    src/nativeMain/cpp/CanvasView.h
    src/nativeMain/cpp/CanvasViewCreator.cpp
    src/nativeMain/cpp/CanvasViewCreator.h
    src/nativeMain/cpp/CanvasViewGlue.cpp
    src/nativeMain/cpp/CanvasViewGlue.h
    src/nativeMain/cpp/IBStreamerGlue.cpp
    src/nativeMain/cpp/IBStreamerGlue.h
    src/nativeMain/cpp/KotlinSym.h
    src/nativeMain/cpp/UIController.cpp
    src/nativeMain/cpp/UIController.h
    src/nativeMain/cpp/UIControllerGlue.cpp
    src/nativeMain/cpp/UIControllerGlue.h
    src/nativeMain/cpp/entry.cpp
    src/nativeMain/cpp/utils.h
    src/nativeMain/cpp/version.h
)

if(SMTG_ADD_VSTGUI)
    target_sources(Kangaroo
        PRIVATE
            src/nativeMain/cpp-res/kangaroo.uidesc
    )
    target_link_libraries(Kangaroo
        PRIVATE
            vstgui_support
    )
    smtg_target_add_plugin_resources(Kangaroo
        RESOURCES
            src/nativeMain/cpp-res/kangaroo.uidesc
    )
endif(SMTG_ADD_VSTGUI)

smtg_target_add_plugin_snapshots(Kangaroo
RESOURCES
    src/nativeMain/cpp-res/184A483BD9924E20810205E1273A6EB1_snapshot.png
    src/nativeMain/cpp-res/184A483BD9924E20810205E1273A6EB1_snapshot_2.0x.png
)

smtg_target_add_plugin_resources(Kangaroo
RESOURCES
    src/nativeMain/cpp-res/au-info.plist
    src/nativeMain/cpp-res/info.rc
    src/nativeMain/cpp-res/logo-small.png
)

target_link_libraries(Kangaroo
    PRIVATE
        sdk
)

smtg_target_configure_version_file(Kangaroo)

if(SMTG_MAC)
    smtg_target_set_bundle(Kangaroo
        BUNDLE_IDENTIFIER ch.digorydoo.kangaroo
        COMPANY_NAME "ch.digorydoo"
    )
    smtg_target_set_debug_executable(Kangaroo
        "/Applications/VST3PluginTestHost.app"
        "--pluginfolder;$(BUILT_PRODUCTS_DIR)"
    )
elseif(SMTG_WIN)
    target_sources(Kangaroo PRIVATE 
        resource/win32resource.rc
    )
    if(MSVC)
        set_property(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} PROPERTY VS_STARTUP_PROJECT Kangaroo)

        smtg_target_set_debug_executable(Kangaroo
            "$(ProgramW6432)/Steinberg/VST3PluginTestHost/VST3PluginTestHost.exe"
            "--pluginfolder \"$(OutDir)/\""
        )
    endif()
endif(SMTG_MAC)
