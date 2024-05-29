(ns sistemas-l.core
  (:gen-class)
  (:require [sistemas-l.parseos :as parseos]
            [sistemas-l.tortugas :as tortugas]))

(defn generar-info [ruta-sl] (parseos/parse-sl ruta-sl))

(defn generar-operaciones [ruta-sl iteraciones]
  (let [info (generar-info ruta-sl)] (parseos/parse-op-final (:reglas info) (:axioma info) iteraciones)))

(defn -main [ruta-sl iter ruta-svg]
  ; Pruebas iniciales
  (let [parseo-test (generar-operaciones ruta-sl (int (read-string iter))) info (generar-info ruta-sl)]
    (println parseo-test)
    (println (doseq [tortuga (tortugas/crear-tortugas parseo-test (:distancia info) (:angulo info))] (println "x:" (:x tortuga) ", y:" (:y tortuga) ", ang:" (:orientacion tortuga) ", pluma:" (:pluma tortuga))))
    (println (tortugas/crear-tortugas parseo-test (:distancia info) (:angulo info)))))


;(defn crear-svg [operaciones distancia angulo ruta-svg]
;  (let [tortugas (tortugas/crear-tortugas operaciones distancia angulo)
;        contenido-svg (parseos/parse2svg tortugas)]
;    (spit ruta-svg contenido-svg)))