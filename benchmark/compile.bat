rem call variables.bat

set OUTPUT_DIR=build
if exist "%OUTPUT_DIR%" rmdir "%OUTPUT_DIR%" /s /q
mkdir "%OUTPUT_DIR%"

g++ -O3 -I"%PATH_TO_JDK_HOME%\include" ^
   -I"%PATH_TO_JDK_HOME%\include\win32" ^
   -I"%PATH_TO_OCTAVE_HOME%\include\octave-4.2.1" ^
   -I"%PATH_TO_OCTAVE_HOME%\include\octave-4.2.1\octave" ^
   -L"%PATH_TO_OCTAVE_HOME%\bin" ^
   -o "%OUTPUT_DIR%\benchmark.exe" ^
   benchmark.cpp ^
   -loctave-4 -loctinterp-4
