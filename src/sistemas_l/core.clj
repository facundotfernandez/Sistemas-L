(ns sistemas-l.core
  (:gen-class)
  (:require [sistemas-l.parseos :as parseos]
            [sistemas-l.tortugas :as tortugas]))

(defn generar-info [ruta-sl] (parseos/parse-sl ruta-sl))

(defn generar-operaciones [ruta-sl iteraciones]
  (let [info (generar-info ruta-sl)] (parseos/parse-op-final (:reglas info) (:axioma info) iteraciones)))

(defn -main [ruta-sl iter ruta-svg]
  (let [parseo-test (generar-operaciones ruta-sl (int (read-string iter))) info (generar-info ruta-sl)]
    (parseos/parse2svg (tortugas/crear-tortugas parseo-test (:distancia info) (:angulo info)) ruta-svg)))