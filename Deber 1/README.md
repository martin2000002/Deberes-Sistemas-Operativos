# Martín Montero (00328595) y Noah Palacios (00348958)

## Contribución
- Martín: Interpretación de comandos mediante `Parser.java`, captura de salidas en segundo plano sin bloquear procesos con `BytesCollector.java`, redirección en tiempo real de stdout/stderr a consola con `StreamGobbler.java`, impresión sincronizada para evitar salidas entremezcladas entre hilos con `SynchronizedPrinter.java`, y creación del `Dockerfile`.

- Noah: Implementación del bucle principal en `Shell.java`, gestión del historial de comandos con `History.java`, almacenamiento del estado global del shell en `State.java`, punto de entrada de la aplicación en `Jsh.java`, y creación del `README.md`.

## Ejecución
- Iniciar el contenedor (arranca en `/work`):
  ```
  docker run --rm -it martin200002/homework-1:javashell
  ```

- Probar comandos!

- Probar ejemplos de inputs desde archivos:
  - Opción A: ejecutar desde `/examples`
    ```
    cd /examples
    java SimpleIO < input.txt
    ```
  - Opción B: copiar a `/work`
    ```
    cp -r /examples/. /work
    cd /work
    java SimpleIO < input.txt
    ```

- NOTA: Si se quiere probar con otros archivos, montar una carpeta del host en `/work` y agregar los arhivos que se quieran probar:
  ```
  docker run --rm -it -v "C:\RUTA\DEL\HOST:/work" martin200002/homework-1:javashell
  ```

## Comentarios relevantes
- `cd/pwd/echo/history` se manejan como built-ins también en background; se reporta PID `-1`.
- `exit` se ignora dentro de secuencias background.