/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//Note, the above license and copyright applies to this file only.

package com.mypokemon.game.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import static org.lwjgl.system.JNI.invokePPP;
import static org.lwjgl.system.JNI.invokePPZ;
import static org.lwjgl.system.macosx.ObjCRuntime.objc_getClass;
import static org.lwjgl.system.macosx.ObjCRuntime.sel_getUid;

/**
 * Proporciona utilidades para asegurar que la JVM se inicie con el argumento
 * {@code -XstartOnFirstThread}, necesario en macOS para que LWJGL 3 funcione
 * correctamente.
 * También ayuda en Windows cuando los nombres de usuario contienen caracteres
 * no latinos,
 * evitando fallos comunes en el arranque.
 * 
 * <br>
 * <a href=
 * "https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Basado
 * en este post de java-gaming.org por kappa</a>
 * 
 * @author damios
 */
public class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartupHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Inicia una nueva JVM si la aplicación se ejecutó en macOS sin el argumento
     * {@code -XstartOnFirstThread}. También incluye lógica para Windows en casos
     * donde
     * el directorio de usuario contiene caracteres no ASCII, lo cual suele causar
     * fallos inmediatos.
     * 
     * <p>
     * <u>Uso:</u>
     *
     * <pre>
     * <code>
     * public static void main(String... args) {
     * 	if (StartupHelper.startNewJvmIfRequired(true)) return;
     * 	// El código real del método main va aquí
     * }
     * </code>
     * </pre>
     *
     * @param redirectOutput Define si la salida de la nueva JVM debe redirigirse a
     *                       la antigua.
     * @return true si se inició una nueva JVM y, por lo tanto, no se debe ejecutar
     *         más código en la actual.
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT);
        if (!osName.contains("mac")) {
            if (osName.contains("windows")) {
                // Here, we are trying to work around an issue with how LWJGL3 loads its
                // extracted .dll files.
                // By default, LWJGL3 extracts to the directory specified by "java.io.tmpdir",
                // which is usually the user's home.
                // If the user's name has non-ASCII (or some non-alphanumeric) characters in it,
                // that would fail.
                // By extracting to the relevant "ProgramData" folder, which is usually
                // "C:\ProgramData", we avoid this.
                // We also temporarily change the "user.name" property to one without any chars
                // that would be invalid.
                // We revert our changes immediately after loading LWJGL3 natives.
                String programData = System.getenv("ProgramData");
                if (programData == null)
                    programData = "C:\\Temp\\"; // if ProgramData isn't set, try some fallback.
                String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
                String prevUser = System.getProperty("user.name", "libGDX_User");
                System.setProperty("java.io.tmpdir", programData + "/libGDX-temp");
                System.setProperty("user.name",
                        ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_'));
                Lwjgl3NativesLoader.load();
                System.setProperty("java.io.tmpdir", prevTmpDir);
                System.setProperty("user.name", prevUser);
            }
            return false;
        }

        // There is no need for -XstartOnFirstThread on Graal native image
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        // Checks if we are already on the main thread, such as from running via
        // Construo.
        long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
        long NSThread = objc_getClass("NSThread");
        long currentThread = invokePPP(NSThread, sel_getUid("currentThread"), objc_msgSend);
        boolean isMainThread = invokePPZ(currentThread, sel_getUid("isMainThread"), objc_msgSend);
        if (isMainThread)
            return false;

        long pid = LibC.getpid();

        // check whether -XstartOnFirstThread is enabled
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // check whether the JVM was previously restarted
        // avoids looping, but most certainly leads to a crash
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                    "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument.");
            return false;
        }

        // Restart the JVM with -XstartOnFirstThread
        ArrayList<String> jvmArgs = new ArrayList<>();
        String separator = System.getProperty("file.separator", "/");
        // The following line is used assuming you target Java 8, the minimum for
        // LWJGL3.
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
        // If targeting Java 9 or higher, you could use the following instead of the
        // above line:
        // String javaExecPath = ProcessHandle.current().info().command().orElseThrow();

        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                    "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!");
            return false;
        }

        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            if (!redirectOutput) {
                ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
                processBuilder.start();
            } else {
                Process process = (new ProcessBuilder(jvmArgs))
                        .redirectErrorStream(true).start();
                BufferedReader processOutput = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Inicia una nueva JVM si la aplicación se ejecutó en macOS sin el argumento
     * {@code -XstartOnFirstThread}. Redirige la salida de la nueva JVM a la
     * antigua.
     * 
     * <p>
     * <u>Uso:</u>
     *
     * <pre>
     * public static void main(String... args) {
     *     if (StartupHelper.startNewJvmIfRequired())
     *         return;
     *     // El código del método main actual
     * }
     * </pre>
     *
     * @return true si se inició una nueva JVM.
     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }
}