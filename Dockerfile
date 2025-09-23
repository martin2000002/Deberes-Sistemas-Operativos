# ----------------------------
# Etapa 1: Build
# ----------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /jsh

# Copiar el código fuente
COPY jsh/ /jsh

# Crear carpeta de salida y compilar todos los .java recursivamente
RUN mkdir out && find . -name "*.java" > sources.txt && javac -d out @sources.txt

# ----------------------------
# Etapa 2: Runtime
# ----------------------------
FROM eclipse-temurin:21-jdk

# Paquetes para ifconfig/ip
RUN apt-get update && apt-get install -y --no-install-recommends net-tools iproute2 \
    && rm -rf /var/lib/apt/lists/*

# Copiar los .class compilados desde el builder
COPY --from=builder /jsh/out /jsh/out
    
# Copiar archivos de ejemplo
COPY examples/ /examples

WORKDIR /work

# Comando para ejecutar tu shell
CMD ["java", "-cp", "/jsh/out", "jsh.Jsh"]
