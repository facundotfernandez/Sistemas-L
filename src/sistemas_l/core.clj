(ns sistemas-l.core
  (:gen-class)
  (:require
    [sistemas-l.parseos :as parseos]
    [sistemas-l.tortugas :as tortugas]))

(defn generar-operaciones!
  "Genera una lista de operaciones a partir de un archivo que contiene la información
   de un sistema-L y una cantidad de iteraciones dada.

   Parámetros:
     ruta-sl - Ruta relativa del archivo que contiene la información del sistema-L.
     iteraciones - Cantidad de veces que se aplicarán las reglas del sistema-L al axioma inicial.

   Retorna:
     La lista de operaciones resultantes después de aplicar las reglas del sistema-L."
  [ruta-sl iteraciones]
  (let [info (parseos/parse-sl! ruta-sl)] (parseos/parse-operaciones (:reglas info) (:axioma info) iteraciones)))

(defn -main
  "Genera imágenes fractales, mediante un algoritmo basado en sistemas-L, una simulación de gráficos tortuga y el formato de imágenes estándar SVG.

  Parámetros:
    ruta-sl - Ruta relativa del archivo a leer con la información del sistema-L.
    iteraciones - Cantidad de procesamientos a realizar sobre el axioma inicial del sistema-L.
    ruta-svg - Ruta relativa del archivo a escribir/crear con el formato de imágenes SVG."
  [ruta-sl iteraciones ruta-svg]
  (let [tortugas (generar-operaciones! ruta-sl (int (read-string iteraciones))) info (parseos/parse-sl! ruta-sl)]
    (parseos/parse2svg! (tortugas/crear-tortugas tortugas (:distancia info) (:angulo info)) ruta-svg)))