# Martín Montero (00328595) y Noah Palacios (00348958)

## Contribución
- Martín: Parser (`&^`, comillas, `<`), manejo de procesos externos, background jobs, historial (`!n`, `!#`) y formato `stdout/stderr`.
- Noah: Bucle del shell y built-ins (`cd`, `pwd`, `echo`, `history`), sincronización de salida, Dockerfile/runtime (`/jsh/out`, `/work`, `/examples`) y pruebas.

## Ejecución
- Iniciar: `docker run -it martin200002/homework-1:javashell`
  (Arranca en `/work`)
- Probar comandos!
    - Si se quiere probar redirección de entrada "`<`" se pueden usar los ejemplos `SimpleIO` e `input.txt`, se los puede probar directamente en `/examples` o importar a `/work` asi:
        cp -r /examples/. .
     Con los ejemplos ya se puede ejecutar: `java SimpleIO < input.txt`

    - Si se quiere modificar los ejemplos o crear otros, montar una carpeta host en `/work` asi:
        docker run -it -v "{RUTA DEL LA CARPETA HOST}:/work" martin200002/homework-1:javashell
     Importar los ejemplos como se mostro anteriormente, y ya se pueden editar desde el host.

## Comentarios relevantes
- `cd/pwd/echo/history` se manejan como built-ins también en background; se reporta PID `-1`.
- `exit` se ignora dentro de secuencias background.
- No hay globbing ni pipes; usar rutas# Martín Montero (00328595) y Noah Palacios (00348958)

## Contribución
- Martín: Parser (`&^`, comillas, `<`), manejo de procesos externos, background jobs, historial (`!n`, `!#`) y formato `stdout/stderr`.
- Noah: Bucle del shell y built-ins (`cd`, `pwd`, `echo`, `history`), sincronización de salida, Dockerfile/runtime (`/jsh/out`, `/work`, `/examples`) y pruebas.

## Ejecución
- Iniciar: `docker run -it martin200002/homework-1:javashell`
  (Arranca en `/work`)
- Probar comandos!
    - Si se quiere probar redirección de entrada "`<`" se pueden usar los ejemplos `SimpleIO` e `input.txt`, se los puede probar directamente en `/examples` o importar a `/work` asi:
        cp -r /examples/. .
     Con los ejemplos ya se puede ejecutar: `java SimpleIO < input.txt`

    - Si se quiere modificar los ejemplos o crear otros, montar una carpeta host en `/work` asi:
        docker run -it -v "{RUTA DEL LA CARPETA HOST}:/work" martin200002/homework-1:javashell
     Importar los ejemplos como se mostro anteriormente, y ya se pueden editar desde el host.

## Comentarios relevantes
- `cd/pwd/echo/history` se manejan como built-ins también en background; se reporta PID `-1`.
- `exit` se ignora dentro de secuencias background.
- No hay globbing ni pipes; usar rutas